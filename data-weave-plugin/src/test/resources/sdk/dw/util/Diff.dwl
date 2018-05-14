/**
* This utility module returns calculates the difference between two values and returns the list of differences
*/
%dw 2.0

import entrySet, keySet from dw::core::Objects


/**
* Describes the entire difference beteween two values
*/
type Diff = {
    "matches": Boolean,
    diffs: Array<Difference>
}

/**
* Describes a single difference between two values at a given structure
*/
type Difference = {
    expected: String,
    actual: String,
    path: String
}

/**
* Returns the structural diference between two values. It returns an Difference.
* When comparing objects it can be either ordered or unordered. By default is ordered this means that two object
* are not going to have a difference if their key value pairs are in the same order. To change this behaviour
* specify the diffConfig parameter with {unordered: true}
*
* .Example:
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* import * from dw::util::Diff
* output application/json
* var a = { age: "Test" }
* var b = { age: "Test2" }
* ---
*  a diff b
* ----
*
* .Output:
* [source,xml,linenums]
* ----
* {
* "matches": false,
*  "diffs": [
*    {
*      "expected": "\"Test2\"",
*      "actual": "\"Test\"",
*      "path": "(root).age"
*    }
*  ]
* }
* ----
*
*/
fun diff(actual: Any, expected:Any, diffConfig: {unordered?: Boolean} = {} , path:String = "(root)"): Diff  = do {

    fun createDiff(expected:String, actual:String, path: String): Diff =
        {
            matches: false,
            diffs: [{ expected: expected, actual: actual, path: path }]
        }
    fun createMatch(): Diff =
        {
            matches: true,
            diffs: []
        }

    fun mergeDiff(left:Diff, right:Diff):Diff =
        {
            matches: left.matches and right.matches,
            diffs: left.diffs ++ right.diffs,
        }

    fun keyToString(k:Key):String = do {
        var namespace = if(k.#?) (k.# as Object) else {}
        ---
        if(namespace.prefix?)
            "$(namespace.prefix!)#$(k)"
        else
            k as String
    }

    fun entries(obj:Object) = obj pluck [$$,$]

    fun namesAreEquals(akey:Key, ekey:Key):Boolean =
        (akey as String == ekey as String) and (akey.#.uri == ekey.#.uri)

    fun isEmptyAttribute(attrs: Object | Null) =
        attrs == null or attrs == {}

    fun diffAttributes(actual: Any, expected:Any, path:String):Diff = do {
        var actualAttributes = actual.@
        var expectedAttributes = expected.@
        ---
        if(isEmptyAttribute(actualAttributes) and not isEmptyAttribute(expectedAttributes))
            createDiff("Attributes $(write(expectedAttributes))", "Empty attributes.", path)
        else if((not isEmptyAttribute(actualAttributes)) and isEmptyAttribute(expectedAttributes))
            createDiff("Empty attributes", "Attributes $(write(expectedAttributes))", path)
        else if(isEmptyAttribute(expectedAttributes) and isEmptyAttribute(actualAttributes))
            createMatch()
        else
            diff(actualAttributes, expectedAttributes, diffConfig, path)
    }

    fun diffObjects(actual:Object, expected:Object, path: String = "(root)"): Diff = do {
        var aobject = if(diffConfig.unordered default false) actual orderBy $$ else actual
        var eobject = if(diffConfig.unordered default false) expected orderBy $$ else expected
        ---
        if(sizeOf(aobject) == sizeOf(eobject)) do {
            var matchResult = {diff: createMatch(), remaining: aobject }
            ---
            zip(entries(aobject), entries(eobject)) map ((actualExpected) -> do {
                var actualEntry = actualExpected[0]
                var expectedEntry = actualExpected[1]
                var expectedKey = expectedEntry[0]
                var expectedKeyString = keyToString(expectedKey)
                var attributeDiff = diffAttributes(actualEntry[0], expectedKey, "$(path).$(expectedKeyString).@")
                var valueDiff = diff(actualEntry[1], expectedEntry[1], diffConfig, "$(path).$(expectedKeyString)")
                ---
                if(namesAreEquals(actualEntry[0], expectedKey))
                    mergeDiff(attributeDiff, valueDiff)
                else do {
                    var expectedValueType = typeOf(expectedEntry[1])
                    ---
                    createDiff("Entry $(path).$(expectedKeyString) with type $(expectedValueType)", "was not present in object.", "$(path).$(expectedKeyString)")
                }

            })
            reduce ((value, acc = createMatch()) -> mergeDiff(value, acc))
        }
        else
            createDiff("Object size is $(sizeOf(eobject))", "$(sizeOf(aobject))", path)
    }

    ---
    expected match {
        case eobject is Object -> do {
            actual match {
                 case aobject is Object ->
                    diffObjects(eobject, aobject, path)
                 else ->
                    createDiff("Object type", "$(typeOf(actual)) type" , path)
             }
        }
        case earray is Array -> do {
             actual match {
                 case aarray is Array ->
                    if(sizeOf(aarray) == sizeOf(earray))
                      zip(aarray, earray)
                        map ((actualExpected, index) ->
                            diff(actualExpected[0], actualExpected[1], diffConfig, "$(path)[$(index)]")
                        )
                        reduce ((value, acc = createMatch()) ->
                            mergeDiff(value, acc)
                        )
                    else
                      createDiff("Array size is $(sizeOf(earray))", "was $(sizeOf(aarray))" , path)
                 else ->
                    createDiff("Array type", "$(typeOf(actual)) type", path)
             }
        }
        else ->
            if(actual == expected)
              createMatch()
            else
              createDiff("$(write(expected))", "$(write(actual))", path)
    }
}