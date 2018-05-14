%dw 2.0

// sduke: punctuation and change to .Transform, and
// updated .Output test of transform in DW editor produced new attributes: null
/**
 * Returns a list of key-value pair objects that describe the object entries.
 *
 * .Transform
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * import dw::core::Objects
 * ---
 * Objects::entrySet({a: true, b: 1})
 * ----
 *
 * .Output
 * [source,JSON, linenums]
 * ----
 * [
 *  {
 *    key: "a",
 *    value: true,
 *    attributes: null
 *  },
 *  {
 *    key: "b",
 *    value: 1,
 *    attributes: null
 *  }
 * ]
 * ----
 *
 */
fun entrySet<T <: Object>(obj: T) =
  obj pluck (value, key) -> {
    key: key,
    value: value,
    attributes: key.@
  }

// sduke tweak: punctuation and change to .Transform
/**
* Returns the list of key names from an object.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import dw::core::Objects
* ---
* Objects::nameSet({a: true, b: 1})
* ----
*
* .Output
* [source,JSON, linenums]
* ----
*  ["a","b"]
* ----
*/
fun nameSet(obj: Object): Array<String> = obj pluck ($$ as String)

// sduke tweak: punctuation and change to .Transform, fix transform example.
// would be nice to show a difference between nameSet and keySet
/**
* Returns the list of key names from an object.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import dw::core::Objects
* ---
* Objects::keySet({a: true, b: 1})
* ----
*
* .Output
* [source,JSON, linenums]
* ----
*  ["a","b"]
* ----
*/
fun keySet<T <: Object>(obj: T): ? = obj pluck $$

// sduke tweak: punctuation and change to .Transform
/**
* Returns the list of key values of an object.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import dw::core::Objects
* ---
* Objects::valueSet({a: true, b: 1})
* ----
*
* .Output
* [source,JSON, linenums]
* ----
*  [true,1]
* ----
*/
fun valueSet <K,V>(obj: {(K)?: V}): Array<V> = obj pluck $

// sduke: Modification from Ethan Port.
/**
 * Keeps the target object intact, then appends to the target object any
 * key-value pairs from the source where the key is not already in the target.
 *
 * .Transform
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * import mergeWith from dw::core::Objects
 * ---
 * {a: true, b: 1} mergeWith {a: false, c: "Test"}
 * ----
 *
 * .Output
 * [source,JSON, linenums]
 * ----
 * {"a": false, "b": 1 , "c": "Test"}
 * ----
 *
 */
fun mergeWith<T <: Object,V <: Object>(source: T, target: V): ? =
  (source -- keySet(target)) ++ target

// sduke tweak
/**
* Helper method to make `mergeWith` null friendly.
*/
fun mergeWith<T <: Object>(a: Null, b: T): T = b

// sduke tweak
/**
* Helper method to make `mergeWith` null friendly.
*/
fun mergeWith<T <: Object>(a: T, b: Null): T = a

// sduke: "number" not "amount", since they are specifying a number. Fix dw header.
/**
* Divides the object into sub-objects with the specified number of properties.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import divideBy from dw::core::Objects
* ---
*  {a: 123,b: true, a:123, b:false} divideBy 2
* ----
*
* .Output
* [source,JSON, linenums]
* ----
* [
*    {
*      "a": 123,
*      "b": true
*    },
*    {
*      "a": 123,
*      "b": false
*    }
*  ]
* ----
*
*/
fun divideBy(items: Object, amount: Number): Array<{}> = do {
    fun internalDivideBy<T>(items: Object, amount: Number, carry:{} ): Array<{}> =
        items match {
          case {k:v ~ xs} ->
            if(sizeOf(carry) == amount - 1)
                [carry ++ {(k):v} ~ internalDivideBy(xs, amount, {})]
            else
               internalDivideBy(xs, amount, carry ++ {(k):v} )
          else ->
            if(isEmpty(carry))
             []
            else
             [carry]
        }
    ---
    internalDivideBy(items, amount, {})
}
