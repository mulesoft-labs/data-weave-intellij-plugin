/**
 * This module contains all the core data weave functionality. It is automatically imported into any data weave script.
 *
*/
%dw 2.0

/**
 * This are the native types of DataWeave.
 * Those are the only types that allow the ??? definition.
 */

/**
 * `String` type
 */
type String = ???
type Boolean = ???
type Number = ???
type Range = ???
type Namespace = ???
type Uri = ???
type DateTime = ???
type LocalDateTime = ???
type Date = ???
type LocalTime = ???
type Time = ???
type TimeZone = ???
type Period = ???
type Binary = ???
type Null = ???
type Regex = ???
type Nothing = ???
/**
 * `Any` type, is the top level type. Any extends all of the system types.
 * That means anything can be assigned to a `Any` typed variable.
 */
type Any = ???
/**
 * `Array` type, requires a Type(T) to represent the elements of the list.
 * Example: Array<Number> represents an array of numbers.
 */
type Array<T> = ???
/**
 * `Object` type. Represents any object, an
 */
type Object = ???
type Type<T> = ???
type Key = ???

/**
* Generic Dictionary interface
*/
type Dictionary<T> = {_?: T}

/**
* A union type that represents all the types that  can be compared to each other.
*/
type Comparable = String | Number | Boolean | DateTime | LocalDateTime | LocalTime | Time | TimeZone

/**
* Logs the specified value with the specified `prefix`, it then returns the value unchanged. +
*
* .Example:
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* in payload application/json
* output application/xml
* ---
*  { age: log("My Age", payload.age) }
* ----
* .Input:
* [source,json,linenums]
* ----
* { "age" : 33 }
* ----
* This will print output: `My Age - 33`
* .Output:
* [source,xml,linenums]
* ----
* <age>33</age>
* ----
*
* Note that besides producing the expected output, it also logs it.
*
*/
fun log <T>(prefix: String = "", value: T): T = native("system::log")

/**
* The read function returns the result of parsing the content parameter with the specified mimeType reader.
*
* The first argument points the content that must be read, the second is the format in which to write it. A third optional argument lists reader configuration properties.
*
* .Example:
*  [source,DataWeave,linenums]
*  ----
* %dw 2.0
* output application/xml
* ---
*  read('{"name":"DataWeave"}', "application/json")
*  ----
* .Output:
*  [source,xml,linenums]
*  ----
* <name>DataWeave</name>
*  ----
*/
fun read(stringToParse: String, contentType: String = "application/dw", readerProperties: Object = {}) = native("system::read")

/**
* Same as the `read` operator, but using a URL as the content provider.
*/
fun readUrl(url: String, contentType: String = "application/dw", readerProperties: Object = {}) = native("system::readUrl")

/**
* The write function returns a string with the serialized representation of the value in the specified mimeType.
*
* The first argument points to the element that must be written, the second is the format in which to write it. A third optional argument lists writer configuration properties. See link:/mule-user-guide/v/3.8/dataweave-language-introduction#output-directive[Output Directive] and its sub-sections for a full list of available configuration options for each different format.
*
* .Transform
* [source, dataweave, linenums]
* ----
* %dw 2.0
* output application/xml
* ---
* {
*  output: write(payload, "application/csv", {"separator" : "|"})
* }
* ----
*
* .Input
* [source,json,linenums]
* ----
* [
*   {
*     "Name": "Mr White",
*     "Email": "white@mulesoft.com",
*     "Id": "1234",
*     "Title": "Chief Java Prophet"
*   },
*   {
*     "Name": "Mr Orange",
*     "Email": "orange@mulesoft.com",
*     "Id": "4567",
*     "Title": "Integration Ninja"
*   }
* ]
* ----
*
* .Output
* [source,xml,linenums]
* ----
* <?xml version='1.0' encoding='US-ASCII'?>
* <output>Name|Email|Id|Title
* Mr White|white@mulesoft.com|1234|Chief Java Prophet
* Mr Orange|orange@mulesoft.com|4567|Integration Ninja
* </output>
* ----
*
**/
fun write (value: Any, contentType: String = "application/dw", writerProperties: Object = {}): Any = native("system::write")

/**
* Returns a random number of type link:/mule-user-guide/v/4.0/dataweave-types#number[(:number)] between 0 and 1.
*
*
* .Transform
* [source, dataweave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   price: random() * 1000
* }
* ----
*
*/
fun random(): Number = native("system::random")

/**
* Returns a link:/mule-user-guide/v/4.0/dataweave-types#dates[(Datetime)] object with the current date and time.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: now(),
*   b: now().day,
*   c: now().minutes
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "2015-12-04T18:15:04.091Z",
*   "b": 4,
*   "c": 15
* }
* ----
*
* [TIP]
* See link:/mule-user-guide/v/4.0/dataweave-selectors[DataWeave Selectors] for a list of possible selectors to use here.
*
*/
fun now(): DateTime = native("system::now")

/**
* Loads a native function using the specified identifier.
*/
fun native(identifier: String): Nothing = ???//This function is just a place holder



/**
* This type is based in the link:https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html[iterator Java class]. The iterator contains a collection, and includes methods to iterate through and filter it.
*
* [NOTE]
* Just like the Java class, the iterator is designed to be consumed only once. For example, if you then pass this value to a link:/mule-user-guide/v/3.8/logger-component-reference[logger] would result in consuming it and it would no longer be readable to further elements in the flow.
*
*
**/
type Iterator = Array {iterator: true}

/**
* This type is based in the link:https://docs.oracle.com/javase/7/docs/api/java/lang/Enum.html[Enum java class].
* It must always be used with the `class` property, specifying the full java class name of the class, as shown in the example below.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/java
* ---
* "Male" as Enum {class: "com.acme.GenderEnum"}
* ----
*
*/
type Enum = String {enumeration: true}


/**
*
* XML defines a custom type named CData, it extends from string and is used to identify a CDATA XML block.
* It can be used to tell the writer to wrap the content inside CDATA or to check if the input string arrives inside a CDATA block. `:cdata` inherits from the type `:string`.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/xml
* ---
* {
*   users:
*   {
*     user : "Mariano" as CData,
*     age : 31 as CData
*   }
* }
* ----
*
* .Output
* [source,xml,linenums]
* ----
* <?xml version="1.0" encoding="UTF-8"?>
* <users>
*   <user><![CDATA[Mariano]]></user>
*   <age><![CDATA[31]]></age>
* </users>
* ----
*
**/
type CData = String {cdata: true}

//---------------------------------------------------------------------------------------------------------

/**
*
* It returns the resulting array of concatenating two existing arrays.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: [0, 1, 2] ++ [3, 4, 5]
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": [0, 1, 2, 3, 4, 5]
* }
* ----
*
* Note that the arrays can contain any supported data type, for example:
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: [0, 1, true, "my string"] ++ [2, [3,4,5], {"a": 6}]
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": [0, 1, true, "my string", 2, [3, 4, 5], { "a": 6}]
* }
* ----
*
**/
fun ++ <S,T>(lhs: Array<S> , rhs: Array<T>): Array<S | T> = native("system::ArrayAppendArrayFunctionValue")

/**
*
* Strings are treated as arrays of characters, so the operation works just the same with strings.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   name: "Mule" ++ "Soft"
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "name": MuleSoft
* }
* ----
*
**/
fun ++(lhs: String, rhs: String): String = native("system::StringAppendStringFunctionValue")

/**
* Returns the resulting object of concatenating two existing objects.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/xml
* ---
* concat: {aa: "a"} ++ {cc: "c"}
* ----
*
* .Output
* [source,xml,linenums]
* ----
* <?xml version="1.0" encoding="UTF-8"?>
* <concat>
*   <aa>a</aa>
*   <cc>c</cc>
* </concat>
* ----
*
* The example above concatenates object {aa: a} and {cc: c} in a single one -> {aa: a , cc: c}
*
**/
fun ++(lhs: Object , rhs: Object): Object = native("system::ObjectAppendObjectFunctionValue")

/**
* You can append a date to a time (or localtime) object so as to provide a more precise value.
*
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: |2003-10-01| ++ |23:57:59|,
*   b: |2003-10-01| ++ |23:57:59Z|
* }
*
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*     "a": "2003-10-01T23:57:59",
*     "b": "2003-10-01T23:57:59Z"
* }
* ----
*
*
* Note that the order in which the two objects are appended is irrelevant, so logically a 'Date' + 'Time'  will result in the same as a '#Time' + 'Date'.
*
**/
fun ++(lhs: Date , rhs: LocalTime): LocalDateTime = native("system::LocalDateAppendLocalTimeFunctionValue")

/**
* You can append a date to a time (or localtime) object so as to provide a more precise value.
*
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: |23:57:59| ++ |2003-10-01|,
*   b: |23:57:59Z| ++ |2003-10-01|
* }
*
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*     "a": "2003-10-01T23:57:59",
*     "b": "2003-10-01T23:57:59Z"
* }
* ----
*
*
* Note that the order in which the two objects are appended is irrelevant, so logically a 'Date' + 'Time'  will result in the same as a '#Time' + 'Date'.
*
**/
fun ++(lhs: LocalTime , rhs: Date): LocalDateTime = native("system::LocalTimeAppendLocalDateFunctionValue")

/**
* You can append a date to a time (or localtime) object so as to provide a more precise value.
*
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: |2003-10-01| ++ |23:57:59|,
*   b: |2003-10-01| ++ |23:57:59Z|
* }
*
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*     "a": "2003-10-01T23:57:59",
*     "b": "2003-10-01T23:57:59Z"
* }
* ----
*
*
* Note that the order in which the two objects are appended is irrelevant, so logically a 'Date' + 'Time'  will result in the same as a '#Time' + 'Date'.
*
**/
fun ++(lhs: Date , rhs: Time): DateTime = native("system::LocalDateAppendTimeFunctionValue")

/**
* You can append a date to a time (or localtime) object so as to provide a more precise value.
*
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: |23:57:59| ++ |2003-10-01|,
*   b: |23:57:59Z| ++ |2003-10-01|
* }
*
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*     "a": "2003-10-01T23:57:59",
*     "b": "2003-10-01T23:57:59Z"
* }
* ----
*
*
* Note that the order in which the two objects are appended is irrelevant, so logically a 'Date' + 'Time'  will result in the same as a '#Time' + 'Date'.
*
**/
fun ++(lhs: Time , rhs: Date): DateTime = native("system::TimeAppendLocalDateFunctionValue")

/**
* Appends a time zone to a date type value.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* a: |2003-10-01T23:57:59| ++ |-03:00|
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "2003-10-01T23:57:59-03:00"
* }
* ----
*
**/
fun ++(lhs: Date , rhs: TimeZone): DateTime = native("system::LocalDateAppendTimeZoneFunctionValue")

/**
* Appends a time zone to a date type value.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* a: |-03:00| ++ |2003-10-01T23:57:59|
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "2003-10-01T23:57:59-03:00"
* }
* ----
*
**/
fun ++(lhs: TimeZone , rhs: Date): DateTime = native("system::TimeZoneAppendLocalDateFunctionValue")

/**
* Appends a time zone to a date type value.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* a: |2003-10-01T23:57:59| ++ |-03:00|
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "2003-10-01T23:57:59-03:00"
* }
* ----
*
**/
fun ++(lhs: LocalDateTime , rhs: TimeZone): DateTime = native("system::LocalDateTimeAppendTimeZoneFunctionValue")

/**
* Appends a time zone to a date type value.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* a: |-03:00| ++ |2003-10-01T23:57:59|
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "2003-10-01T23:57:59-03:00"
* }
* ----
*
**/
fun ++(lhs: TimeZone , rhs: LocalDateTime): DateTime = native("system::TimeZoneAppendLocalDateTimeFunctionValue")

/**
* Appends a time zone to a date type value.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* a: |2003-10-01T23:57:59| ++ |-03:00|
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "2003-10-01T23:57:59-03:00"
* }
* ----
*
**/
fun ++ (lhs: LocalTime,rhs: TimeZone): Time = native('system::LocalTimeAppendTimeZoneFunctionValue')

/**
* Appends a time zone to a date type value.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* a: |-03:00| ++ |2003-10-01T23:57:59|
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "2003-10-01T23:57:59-03:00"
* }
* ----
*
**/
fun ++ (lhs: TimeZone,rhs: LocalTime): Time = native('system::TimeZoneValueAppendLocalTimeFunctionValue')

/**
* Similar to Map, but instead of processing only the values of an object, it processes both keys and values as a tuple. Also instead of returning an array with the results of processing these values through the lambda, it returns an object, which consists of a list of the key:value pairs that result from processing both key and value of the object through the lambda.
*
* The lambda is invoked with three parameters: *value*, *key* and *index*.
* If these parameters are not named, the value is defined by default as *$*, the key *$$* and the index *$$$*.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* var conversionRate=13.45
* ---
* priceList: payload.prices mapObject (
*   '$$':{
*     dollars: $,
*     localCurrency: $ * conversionRate
*   }
* )
* ----
*
*
* .Input
* [source,xml,linenums]
* ----
* <prices>
*     <basic>9.99</basic>
*     <premium>53</premium>
*     <vip>398.99</vip>
* </prices>
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "priceList": {
*     "basic": {
*       "dollars": "9.99",
*       "localCurrency": 134.3655
*     },
*     "premium": {
*       "dollars": "53",
*       "localCurrency": 712.85
*     },
*     "vip": {
*       "dollars": "398.99",
*       "localCurrency": 5366.4155
*     }
*   }
* }
* ----
*
*
* [TIP]
* Note that when you use a parameter to populate one of the keys of your output, as with the case of $$ in this example, you must either enclose it in quote marks or brackets. '$$' or ($$) are both equally valid.
*
* In the example above, as key and value are not defined, they're identified by the placeholders *$$* and *$*.
* For each key:value pair in the input, the key is preserved and the value becomes an object with two properties:
* one of these is the original value, the other is the result of multiplying this value by a constant that is defined as a directive in the header.
*
* The mapping below performs exactly the same transform, but it defines custom names for the properties of the operation, instead of using $ and $$. Here, 'category' is defined as referring to the original key in the object, and 'money' to the value in that key.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* var conversionRate=13.45
* ---
* priceList: payload.prices mapObject ((money, category, index) ->
*   '$category':{
*     dollars: money,
*     localCurrency: money * conversionRate
*   }
* )
* ----
*
* [TIP]
* Note that when you use a parameter to populate one of the keys of your output, as with the case of *category* in this example, you must either enclose it in brackets or enclose it in quote marks adding a $ to it, otherwise the name of the property is taken as a literal string. '$category' or (category) are both equally valid.
**/
fun mapObject <K,V>(lhs: {(K)?: V}, rhs : (V, K, Number) -> Object): Object = native('system::MapObjectObjectFunctionValue')

/**
* Helper function that allows mapObject to work with null values
*/
fun mapObject(value: Null, lambda : (Any, Any, Number) -> Any): Null = null

/**
* Pluck is useful for mapping an object into an array. Pluck is an alternate mapping mechanism to mapObject.
* Like mapObject, pluck executes a lambda over every key:value pair in its processed object as a tuple,
* but instead of returning an object, it returns an array, which may be built from either the values or the keys in the object.
*
* The lambda is invoked with three parameters: *value*, *key* and *index*.
* If these parameters are not named, the value is defined by default as *$*, the key *$$* and the index *$$$*.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* result: {
*   keys: payload.prices pluck $$,
*   values: payload.prices pluck $
* }
* ----
*
* .Input
* [source,xml,linenums]
* ----
* <prices>
*     <basic>9.99</basic>
*     <premium>53</premium>
*     <vip>398.99</vip>
* </prices>
* ----
* .Output
* [source,json,linenums]
* ----
* {
*   "result": {
*     "keys": [
*       "basic",
*       "premium",
*       "vip"
*     ],
*     "values": [
*       "9.99",
*       "53",
*       "398.99"
*     ]
*   }
* }
* ----
**/
fun pluck <K,V,R>(lhs: {(K)?: V}, rhs: (V, K, Number) -> R): Array<R> = native('system::PluckObjectFunctionValue')

/**
* Helper function that allows *pluck* to work with null values
*/
fun pluck(lhs: Null, rhs:(Nothing, Nothing, Nothing) -> Any): Null = null

/**
*
* Given two or more separate lists, the zip function can be used to merge them together into a single list of consecutive n-tuples.  Imagine two input lists each being one side of a zipper: similar to the interlocking teeth of a zipper, the zip function interdigitates each element from each input list, one element at a time.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: [0, 1, 2, 3] zip ["a", "b", "c", "d"],
*   b: [0, 1, 2, 3] zip ["a"],
*   c: [0, 1, 2, 3] zip ["a", "b"]
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": [
*     [0,"a"],
*     [1,"b"],
*     [2,"c"],
*     [3,"d"]
*     ],
*   "b": [
*     [0,"a"],
*     [1,"a"],
*     [2,"a"],
*     [3,"a"]
*   ],
*   "c": [
*     [0,"a"],
*     [1,"b"]
*   ]
* }
* ----
*
* Note that in example b, since only one element was provided in the second array, it was matched with every element of the first array. Also note that in example c, since the second array was shorter than the first, the output was only as long as the shortest of the two.
*
*
* Here is another example of the zip function with more than two input lists.
*
* .Transform
* [source,DataWeave, linenums]
* ----------------------------------------------------------------------
* %dw 2.0
* output application/json
* ---
* payload.list1 zip payload.list2 zip payload.list3
* ----------------------------------------------------------------------
*
* .Input
* [source,json,linenums]
* ----------------------------------------------------------------------
* {
*   "list1": ["a", "b", "c", "d"],
*   "list2": [1, 2, 3],
*   "list3": ["aa", "bb", "cc", "dd"],
*   "list4": [["a", "b", "c"], [1, 2, 3, 4], ["aa", "bb", "cc", "dd"]]
* }
* ----------------------------------------------------------------------
* .Output
* [source,json,linenums]
* ----------------------------------------------------------------------
* [
*   [
*     "a",
*     1,
*     "aa"
*   ],
*   [
*     "b",
*     2,
*     "bb"
*   ],
*   [
*     "c",
*     3,
*     "cc"
*   ]
* ]
* ----------------------------------------------------------------------
*
**/
fun zip <T,X>(lhs: Array<T> , rhs: Array<X>): Array<Array<T | X>> = native("system::ArrayZipArrayFunctionValue")

/**
*
* Returns an array that is the result of applying a transformation function (lambda) to each of the elements.
* The lambda is invoked with two parameters: *value* and the *index*.
* If these parameters are not named, the index is defined by default as *$$* and the value as *$*.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* users: ["john", "peter", "matt"] map  upper($)
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*  "users": [
*    "JOHN",
*    "PETER",
*    "MATT"
*   ]
* }
* ----
*
* In the following example, custom names are defined for the index and value parameters of the map operation, and then both are used to construct the returned value.
* In this case, value is defined as *firstName* and its index in the array is defined as *position*.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* users: ["john", "peter", "matt"] map ((firstName, position) -> position ++ ":" ++ upper(firstName))
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "users": [
*     "0:JOHN",
*     "1:PETER",
*     "2:MATT"
*   ]
* }
* ----
**/
fun map <T,R>(lhs: Array<T>, rhs: (T, Number) -> R ): Array<R> = native("system::ArrayMapFunctionValue")

/**
* Helper function that allows *map* to work with null values
*/
fun map(value: Null, filter: (Nothing, Nothing) -> Boolean): Null = null

/**
*
* Returns an array that only contains those elements that pass the criteria specified in the lambda.
* The lambda is invoked with two parameters: *value* and the *index*.
* If these parameters are not named, the index is defined by default as *$$* and the value as *$*.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   biggerThanTwo: [0, 1, 2, 3, 4, 5] filter $ > 2
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "biggerThanTwo": [3,4,5]
* }
* ----
*
* The next example passes named key and value parameters.
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*  example2: [0, 1, 2, 3, 4, 5] filter ((key1, value1) -> key1 > 3 and value1 < 5 )
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "example2": [4]
* }
* ----
*
**/
fun filter <T,T>(lhs: Array<T> , rhs: (T, Number) -> Boolean): Array<T> = native("system::ArrayFilterFunctionValue")

/**
* Helper function that allows *filter* to work with null values
*/
fun filter(value: Null, filter: (Nothing, Nothing) -> Boolean): Null = null


/**
*
* Returns an object that filters an input object based on a matching condition.
* The lambda is invoked with three parameters: *value*, *key* and *index*.
* If these parameters are not named, the value is defined by default as *$*, the key *$$* and the index *$$$*.
*
* This example filters an object by its value.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {"letter1": "a", "letter2": "b"} filter ((value1) -> value1 == "a")
*
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "letter1": "a"
* }
* ----
*
* You can produce the same results with this input:
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {"letter1": "a", "letter2": "b"} filter ($ == "a")
*
* ----
*
---
*/
fun filter <K,V>(lhs: {(K)?: V}, rhs: (V, K, Number) -> Boolean): Object = native("system::ObjectFilterFunctionValue")

/**
* Replaces a section of a string for another, in accordance to a regular expression, and returns a modified string.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* b: "admin123" replace /(\d+)/ with "ID"
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "b": "adminID"
* }
* ----
*
**/
fun replace(lhs: String, rhs: Regex): ((Array<String>, Number) -> String) -> String = native("system::ReplaceStringRegexFunctionValue")

/**
* Replaces the occurance of a given string inside other string with the specified value
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* b: "admin123" replace "123" with "ID"
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "b": "adminID"
* }
* ----
*
**/
fun replace(toBeReplaced: String, matcher: String): ((Array<String>, Number) -> String) -> String = native("system::ReplaceStringStringFunctionValue")

/**
* Used with the replace applies the specified function
*/
fun with<V,U,R,X>(toBeReplaced: ((V, U) -> R) -> X, callback: (V, U) -> R ): X = toBeReplaced(callback)


/**
*
* Apply a reduction to the array using just two parameters:
* the accumulator (*$$*), and the value (*$*).
* By default, the accumulator starts at the first value of the array.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* sum: [0, 1, 2, 3, 4, 5] reduce ($$ + $)
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "sum": 15
* }
* ----
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* concat: ["a", "b", "c", "d"] reduce ($$ ++ $)
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "concat": "abcd"
* }
* ----
*
* In some cases, you may not want to use the first element of the array as an accumulator. To set the accumulator to something else, you must define this in a lambda.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* concat: ["a", "b", "c", "d"] reduce ((val, acc = "z") -> acc ++ val)
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "concat": "zabcd"
* }
* ----
*
* In other cases, you may want to turn an array into a string keeping the commas in between. The example below defines a lambda that also adds commas when concatenating.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* concat: ["a", "b", "c", "d"] reduce ((val, acc) -> acc ++ "," ++ val)
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "concat":  "a,b,c,d"
* }
* ----
**/
fun reduce <T>(lhs: Array<T>, rhs: (T, T) -> T ): T = native("system::ArrayReduceFunctionValue")
//Works like fold left
fun reduce <T,A>(lhs: Array<T>, rhs: (T, A) -> A ): A = native("system::ArrayReduceFunctionValue")

/**
* Partitions an Array into a Object that contains Arrays, according to the discriminator lambda you define.
* The lambda is invoked with three parameters: *value*, *key* and *index*.
* If these parameters are not named, the value is defined by default as *$*, the key *$$* and the index *$$$*.
*
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* "language": payload.langs groupBy $.language
* ----
*
*
* .Input
* [source,json,linenums]
* ----
* {
*   "langs": [
*     {
*       "name": "Foo",
*       "language": "Java"
*     },
*     {
*       "name": "Bar",
*       "language": "Scala"
*     },
*     {
*       "name": "FooBar",
*       "language": "Java"
*     }
*   ]
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "language": {
*     "Scala": [
*         {"name":"Bar", "language":"Scala"}
*       ],
*     "Java": [
*         {"name":"Foo", "language":"Java"},
*         {"name":"FooBar", "language":"Java"}
*       ]
*   }
* }
* ----
*
**/
fun groupBy <T,R>(lhs: Array<T> , criteria: (T, Number) -> R): {(R): Array<T>} = native("system::ArrayGroupByFunctionValue")

/**
* Partitions an `Object` into a `Object` that contains `Arrays`, according to the discriminator lambda you define.
* The lambda is invoked with two parameters: *value* and the *key*.
*/
fun groupBy <K,V,R,T>(lhs: {(K)?: V}, criteria: (V, K, Number) -> R): {(R): Array<T>} = native("system::ObjectGroupByFunctionValue")

/**
* Helper function that allows *groupBy* to work with null values
*/
fun groupBy(lhs: Null, criteria: (Nothing, Nothing) -> Any): Null = null

//REMOVE
/**
*
* Removes a set of elements from an array when an element in the base array matches one of the values in the substracted array. If multiple elements in the array match a value, they will all be removed.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* a: [0, 1, 1, 2] -- [1,2]
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": [0],
* }
* ----
**/
fun -- <S>(lhs: Array<S> , rhs: Array<Any>): Array<S> = native("system::ArrayRemoveFunctionValue")

/**
* Removes all the entries from the source that are present on the toRemove parameter
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
*
* ---
* {
*    hello: 'world',
*    name: "DW"
*  } -- {hello: 'world'}
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*    "name": "DW"
* }
* ----
*/
fun -- <K,V>(source: {(K)?: V} , toRemove: Object): {(K)?:V} = native("system::ObjectRemoveFunctionValue")

/**
 * Removes the properties from the source that are present the given list of keys.
 * .Transform
 * [source,DataWeave,linenums]
 * ----
 * %dw 2.0
 * output application/json
 *
 * ---
 * {
 *    hello: 'world',
 *    name: "DW"
 *  } -- ['hello']
 * ----
 *
 * .Output
 * [source,json,linenums]
 * ----
 * {
 *    "name": "DW"
 * }
 * ----
 */
fun --(source: Object, keys: Array<String>) =
  keys reduce (key, obj = source) -> (obj - key)

/**
* Returns the array of index where the element to be found where present
*
* .Transform
* [source,DataWeave,lineums]
* ----
* %dw 2.0
* output application/json
* ---
* ["name", "lastName"] find "name"
* ----
*
* .Output
* [source,json,linenums]
* ----
* [
*    0
* ]
* ----
*
*/
fun find <T>(lhs: Array<T> , rhs: Any): Array<Number> = native("system::ArrayFindFunctionValue")

/**
* Returns the array of index where the regex matched in the text
*
* .Transform
* [source,DataWeave,lineums]
* ----
* %dw 2.0
* output application/json
* ---
* "DataWeave" find /a/
* ----
* .Output
* [source,json,linenums]
* ----
* [
*    [1], [3], [6]
* ]
* ----
*/
fun find(lhs: String , rhs: Regex): Array<Array<Number>> = native("system::StringFindRegexFunctionValue")

/**
* Given a string, it returns the index position within the string at which a match was matched. If found in multiple parts of the string, it returns an array with the various idex positions at which it was found. You can either look for a simple string or a regular expression.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: "aabccde" find /(a).(b)(c.)d/,
*   b: "aabccdbce" find "a",
*   c: "aabccdbce" find "bc"
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": [[0,0,2,3]],
*   "b": [0,1],
*   "c": [2,6]
* }
* ----
*
**/
fun find(lhs: String , rhs: String): Array<Number> = native("system::StringFindStringFunctionValue")

/**
* Returns only unique values from an array that may have duplicates.
* The lambda is invoked with two parameters: *value* and *index*.
* If these parameters are not defined, the index is defined by default as $$ and the value as $.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*
*   	book : {
*       title : payload.title,
*       year: payload.year,
*       authors: payload.author distinctBy $
*     }
* }
* ----
*
* .Input
* [source,json,linenums]
* ----
* {
*   "title": "XQuery Kick Start",
*   "author": [
*     "James McGovern",
*     "Per Bothner",
*     "Kurt Cagle",
*     "James Linn",
*     "Kurt Cagle",
*     "Kurt Cagle",
*     "Kurt Cagle",
*     "Vaidyanathan Nagarajan"
*   ],
*   "year":"2000"
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "book": {
*     "title": "XQuery Kick Start",
*     "year": "2000",
*     "authors": [
*       "James McGovern",
*       "Per Bothner",
*       "Kurt Cagle",
*       "James Linn",
*       "Vaidyanathan Nagarajan"
*     ]
*   }
* }
* ----
*
**/
fun distinctBy <T>(lhs: Array<T> , rhs: (T, Number) -> Any): Array<T> = native("system::ArrayDistinctFunctionValue")

/**
* Returns an object with unike key value pairs .
* The lambda is invoked with two parameters: *value* and *key*.
* If these parameters are not defined, the index is defined by default as $$ and the value as $.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/xml
* ---
* {
*
*   	book : {
*         title : payload.book.title,
*         authors: payload.book.&author distinctBy $
*      }
* }
* ----
*
* .Input
* [source,xml,linenums]
* ----
* <book>
*   <title> "XQuery Kick Start"</title>
*   <author>
*     James Linn
*   </author>
*   <author>
*     Per Bothner
*   </author>
*   <author>
*     James McGovern
*   </author>
*   <author>
*     James McGovern
*   </author>
*   <author>
*     James McGovern
*   </author>
* </book>
* ----
*
* .Output
* [source,xml,linenums]
* ----
* <book>
*   <title> "XQuery Kick Start"</title>
*   <authors>
*       <author>
*         James Linn
*       </author>
*       <author>
*         Per Bothner
*       </author>
*       <author>
*         James McGovern
*       </author>
*   </authors>
* </book>
* ----
*
**/
fun distinctBy <K, V>(lhs: {(K)?: V}, rhs: (V, K) -> Any): Object = native("system::ObjectDistinctFunctionValue")

/**
* Returns a range within the specified boundries. The upper boundry is inclusive.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*     "myRange": 1 to 10
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*     "myRange": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
* }
* ----
*/
fun to(from: Number , to: Number): Range = native("system::ToRangeFunctionValue")

//CONTAINS
/**
* You can evaluate if any value in an array matches a given condition:
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* ContainsRequestedItem: payload.root.*order.*items contains "3"
* ----
*
* .Input
* [source,xml,linenums]
* ----
* <?xml version="1.0" encoding="UTF-8"?>
* <root>
*     <order>
*       <items>155</items>
*     </order>
*     <order>
*       <items>30</items>
*     </order>
*     <order>
*       <items>15</items>
*     </order>
*     <order>
*       <items>5</items>
*     </order>
*     <order>
*       <items>4</items>
*       <items>7</items>
*     </order>
*     <order>
*       <items>1</items>
*       <items>3</items>
*     </order>
*     <order>
*         null
*     </order>
* </root>
* ----
* .Output
* [source,json,linenums]
* ----
* {
*   "ContainsRequestedItem": true
* }
* ----
**/
fun contains <T>(lhs: Array<T> , rhs: Any): Boolean = native("system::ArrayContainsFunctionValue")

/**
*
* You can also use contains to evaluate a substring from a larger string:
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* ContainsString: payload.root.mystring contains "me"
* ----
*
* .Input
* [source,xml,linenums]
* ----
* <?xml version="1.0" encoding="UTF-8"?>
* <root>
*   <mystring>some string</mystring>
* </root>
* ----
* .Output
* [source,json,linenums]
* ----
* {
*   "ContainsString": true
* }
* ----
**/
fun contains(lhs: String , rhs: String): Boolean = native("system::StringStringContainsFunctionValue")

/**
* Instead of searching for a literal substring, you can also match it against a regular expression:
*
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* ContainsString: payload.root.mystring contains /s[t|p]ring/
* ----
*

* .Input
* [source,xml,linenums]
* ----
* <?xml version="1.0" encoding="UTF-8"?>
* <root>
*   <mystring>A very long string</mystring>
* </root>
* ----
* .Output
* [source,json,linenums]
* ----
* {
*   "ContainsString": true
* }
* ----
*
**/
fun contains(lhs: String , rhs: Regex): Boolean = native("system::StringRegexContainsFunctionValue")

//ORDERBY
/**
*
* Returns the provided array (or object) ordered according to the value returned by the lambda. The lambda is invoked with two parameters: *value* and the *index*.
* If these parameters are not named, the index is defined by default as *$$* and the value as *$*.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* orderByLetter: [{ letter: "d" }, { letter: "e" }, { letter: "c" }, { letter: "a" }, { letter: "b" }] orderBy $.letter
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "orderByLetter": [
*     {
*       "letter": "a"
*     },
*     {
*       "letter": "b"
*     },
*     {
*       "letter": "c"
*     },
*     {
*       "letter": "d"
*     },
*     {
*       "letter": "e"
*     }
*   ]
* }
* ----
*
* [TIP]
* ====
* The *orderBy* function doesn't have an option to order in descending order instead of ascending. What you can do in these cases is simply invert the order of the resulting array.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* orderDescending: ([3,8,1] orderBy -$)
* ----
*
* .Output
* [source,json,linenums]
* ----
* { "orderDescending": [8,3,1] }
* ----
*
* ====
**/
fun orderBy <K,V,R, O <: {(K)?: V}>(lhs: O, rhs: (V, K) -> R): O = native('system::ObjectOrderByFunctionValue')

/**
* Sorts the array using the specified criteria
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
*  in payload application/json
*  output application/json
*  ---
*  [3,2,3] orderBy $
* ----
* .Output
* [source,json,linenums]
* ----
* [
*   2,
*   3,
*   3
* ]
* ----
**/
fun orderBy <T,R>(lhs: Array<T> , rhs: (T, Number) -> R): Array<T> = native("system::ArrayOrderByFunctionValue")

//UNARY OPERATORS
/**
* Creates an average of all the values in an array and outputs a single number. The array must of course contain only numerical value in it.
*
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: avg([1, 1000]),
*   b: avg([1, 2, 3])
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": 500.5,
*   "b": 2.0
* }
* ----
*
**/
fun avg(rhs: Array<Number>): Number = sum(rhs) / sizeOf(rhs)

/**
* Returns the highest element in an array.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: max([1, 1000]),
*   b: max([1, 2, 3]),
*   d: max([1.5, 2.5, 3.5])
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": 1000,
*   "b": 3,
*   "d": 3.5
* }
* ----
**/
fun max <T <: Comparable>(rhs: Array<T>): T = rhs maxBy $

/**
* Returns the lowest element in an array.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: min([1, 1000]),
*   b: min([1, 2, 3]),
*   d: min([1.5, 2.5, 3.5])
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": 1,
*   "b": 1,
*   "d": 1.5
* }
* ----
**/
fun min <T <: Comparable>(rhs: Array<T>): T = rhs minBy $

/**
* Given an array of numbers, it returns the result of adding of all of them.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* sum([1, 2, 3])
* ----
*
* .Output
* [source,json,linenums]
* ----
* 6
* ----
*/
fun sum(rhs: Array<Number>): Number = rhs reduce $$ + $

//SIZEOF
/**
*
* Returns the number of elements in an array (or anything that can be converted to an array such as a string).
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   arraySize: sizeOf([1,2,3])
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "arraySize": 3
* }
* ----
**/
fun sizeOf <T>(rhs: Array<T>): Number = native("system::ArraySizeOfFunctionValue")

/**
*
* Returns the number of elements in an object .
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   objectSize: sizeOf({a:1,b:2})
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "objectSize": 2
* }
* ----
**/
fun sizeOf(rhs: Object): Number = native("system::ObjectSizeOfFunctionValue")


/**
*
* Returns the byte length of a binary value.
*
**/
fun sizeOf(rhs: Binary): Number = native("system::BinarySizeOfFunctionValue")

/**
*
* Returns the number of characters in an string
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   textSize: sizeOf("MuleSoft")
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "textSize": 8
* }
* ----
**/
fun sizeOf(rhs: String): Number = native("system::StringSizeOfFunctionValue")

/**
*
* If you have an array of arrays, this operator can flatten it into a single simple array.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* flatten(payload)
* ----
*
* .Input
* [source,json,linenums]
* ----
* [
*    [3,5],
*    [9,5],
*    [154,0.3]
* ]
* ----
*
* .Output
* [source,json,linenums]
* ----
* [
*   3,
*   5,
*   9,
*   5,
*   154,
*   0.3
* ]
* ----
*
**/
fun flatten <T, Q>(rhs: Array<Array<T> | Q>): Array<T | Q> = native("system::ArrayFlattenFunctionValue")

/**
* Performs the opposite function of <<zip arrays>>, that is: given a single array where each index contains an array with two elements, it outputs two separate arrays, each with one of the elements of the pair. This can also be scaled up, if the indexes in the provided array contain arrays with more than two elements, the output will contain as many arrays as there are elements for each index.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: unzip([[0,"a"],[1,"b"],[2,"c"],[3,"d"]]),
*   b: unzip([ [0,"a"], [1,"a"], [2,"a"], [3,"a"]]),
*   c: unzip([ [0,"a"], [1,"a","foo"], [2], [3,"a"]])
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*    "a":[
*       [0, 1, 2, 3],
*       ["a", "b", "c", "d"]
*     ],
*   "b": [
*       [0,1,2,3],
*       ["a","a","a","a"]
*     ],
*   "c": [
*       [0,1,2,3]
*     ]
* }
* ----
*
* Note even though example b can be considered the inverse function to the example b in <<zip array>>, the result is not analogous, since it returns an array of repeated elements instead of a single element. Also note that in example c, since the number of elements in each component of the original array is not consistent, the output only creates as many full arrays as it can, in this case just one.
*
**/
fun unzip <T>(rhs: Array<Array<T>>): Array<Array<T>> = native("system::ArrayUnzipFunctionValue")

/**
* Returns true or false depending on if a string ends with a provided substring.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: "Mariano" endsWith "no",
*   b: "Mariano" endsWith "to"
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": true,
*   "b": false
* }
* ----
*
**/
fun endsWith(lhs: String, rhs: String): String = native("system::StringEndsWithFunctionValue")

/**
*
* Merges an array into a single string value, using the provided string as a separator between elements.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* aa: ["a","b","c"] joinBy "-"
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "aa": "a-b-c"
* }
* ----
**/
fun joinBy(lhs: Array, rhs: String): String = native("system::ArrayJoinFunctionValue")

/**
*
* Returns an array with all of the matches in the given string. Each match is returned as an array that contains the complete match, as well as any capture groups there may be in your regular expression.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* hello: "anniepoint@mulesoft.com,max@mulesoft.com" scan /([a-z]*)@([a-z]*).com/
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "hello": [
*     [
*       "anniepoint@mulesoft.com",
*       "anniepoint",
*       "mulesoft"
*     ],
*     [
*       "max@mulesoft.com",
*       "max",
*       "mulesoft"
*     ]
*   ]
* }
* ----
*
* In the example above, we see that the search regular expression describes an email address. It contains two capture groups, what's before and what's after the @. The result is an array with two matches, as there are two email addresses in the input string. Each of these matches is an array of three elements, the first is the whole email address, the second matches one of the capture groups, the third matches the other one.
**/
fun scan(lhs: String, rhs: Regex): Array<Array<String>> = native("system::StringScanFunctionValue")

/**
*
* Performs the opposite operation as Join By. It splits a string into an array of separate elements, looking for instances of the provided string and using it as a separator.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* split: "a-b-c" splitBy /-/
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "split": ["a","b","c"]
* }
* ----
**/
fun splitBy(lhs: String, rhs: Regex): Array<String> = native("system::StringSplitStringFunctionValue")

/**
*
* Performs the opposite operation as Join By. It splits a string into an array of separate elements, looking for instances of the provided string and using it as a separator.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* split: "a-b-c" splitBy "-"
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "split": ["a","b","c"]
* }
* ----
**/
fun splitBy(lhs: String, rhs: String): Array<String> = native("system::StringSplitRegexFunctionValue")

/**

* Returns true or false depending on if a string starts with a provided substring.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: "Mariano" startsWith "Mar",
*   b: "Mariano" startsWith "Em"
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": true,
*   "b": false
* }
* ----
**/
fun startsWith(lhs: String, rhs: String): Boolean = native("system::StringStartsWithFunctionValue")

/**
* Matches a string against a regular expression, and returns *true* or *false*.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* b: "admin123" matches /(\d+)/
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "b": false
* }
* ----
*
* [TIP]
* For more advanced use cases where you need to output or conditionally process the matched value, see link:/mule-user-guide/v/3.8/dataweave-language-introduction#pattern-matching[Pattern Matching].
*
**/
fun matches(lhs: String, rhs: Regex): Boolean = native("system::StringMatchesFunctionValue")

/**
* Matches a string against a regular expression. It returns an array that contains
* the entire matching expression, followed by all of the capture groups that match
* the provided regex.
*
* It can be applied to the result of any evaluated expression, and can return any evaluated expression. See the Match operator in link:/mule-user-guide/v/3.8/dataweave-language-introduction[the DataWeave Language Introduction].
*
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* hello: "anniepoint@mulesoft.com" match /([a-z]*)@([a-z]*).com/
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "hello": [
*     "anniepoint@mulesoft.com",
*     "anniepoint",
*     "mulesoft"
*   ]
* }
* ----
*
* In the example above, we see that the search regular expression describes an email address. It contains two capture groups, what's before and what's after the @. The result is an array of three elements: the first is the whole email address, the second matches one of the capture groups, the third matches the other one.
*
**/
fun match(lhs: String, rhs: Regex): Array<String> = native("system::StringRegexMatchFunctionValue")

/**
* Returns the provided string in lowercase characters.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   name: lower("MULESOFT")
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "name": "mulesoft"
* }
* ----
*
**/
fun lower(rhs: String): String = native("system::StringLowerFunctionValue")

/**
* Removes any excess spaces at the start and end of a string.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   "a": trim("   my long text     ")
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "my long text"
* }
* ----
**/
fun trim(rhs: String): String = native("system::StringTrimFunctionValue")

/**
*
* Returns the provided string in uppercase characters.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   name: upper("mulesoft")
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "name": "MULESOFT"
* }
* ----
**/
fun upper(rhs: String): String = native("system::StringUpperFunctionValue")

/**
*
* Returns the result of the first number `a` to the power of the number following the `pow` operator.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: 2 pow 3,
*   b: 3 pow 2,
*   c: 7 pow 3
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": 8,
*   "b": 9,
*   "c": 343
* }
* ----
**/
fun pow(lhs: Number, rhs: Number): Number = native("system::PowNumberFunctionValue")

/**
*
* Returns the remainder after performing a division of the first number by the second one.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: 3 mod 2,
*   b: 4 mod 2,
*   c: 2.2 mod 2
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": 1,
*   "b": 0,
*   "c": 0.2
* }
* ----
**/
fun mod(lhs: Number, rhs: Number): Number = native("system::ModuleNumberFunctionValue")

/**
* Returns the square root of the provided number.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: sqrt(4),
*   b: sqrt(25),
*   c: sqrt(100)
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": 2.0,
*   "b": 5.0,
*   "c": 10.0
* }
* ----
**/
fun sqrt(rhs: Number): Number = native("system::SqrtNumberFunctionValue")

/**
*
* Returns the absolute value of a number.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: abs(-2),
*   b: abs(2.5),
*   c: abs(-3.4),
*   d: abs(3)
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": 2,
*   "b": 2.5,
*   "c": 3.4,
*   "d": 3
* }
* ----
*
**/
fun abs(rhs: Number): Number = native("system::AbsNumberFunctionValue")

/**
* Rounds a number upwards, returning the first full number above than the one provided.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
*
* {
*   a: ceil(1.5),
*   b: ceil(2.2),
*   c: ceil(3)
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": 2,
*   "b": 3,
*   "c": 3
* }
* ----
**/
fun ceil(rhs: Number): Number = native("system::CeilNumberFunctionValue")

/**
* Rounds a number downwards, returning the first full number below than the one provided.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: floor(1.5),
*   b: floor(2.2),
*   c: floor(3)
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": 1,
*   "b": 2,
*   "c": 3
* }
* ----
**/
fun floor(rhs: Number): Number = native("system::FloorNumberFunctionValue")

/**
* Returns the type of a value.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* typeOf("A Text")
* ----
*
* .Output
* [source,json,linenums]
* ----
* "String"
* ----
*/
fun typeOf <T>(rhs:T): Type<T> = native("system::TypeOfAnyFunctionValue")

/**
* Rounds the value of a number to the nearest integer.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: round(1.2),
*   b: round(4.6),
*   c: round(3.5)
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": 1,
*   "b": 5,
*   "c": 4
* }
* ----
**/
fun round(rhs: Number): Number = native("system::RoundNumberFunctionValue")

/**
* Returns wether an Array is empty or not.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   empty: isEmpty([]),
*   nonEmpty: isEmpty([1])
* }
* ----
*
* .Output
* [source,Json,linenums]
* ----
*   {
*     "empty": true,
*     "nonEmpty": false
*   }
* ----
*/
fun isEmpty(rhs: Array): Boolean = native("system::EmptyArrayFunctionValue")

/**
* Returns wether a String is empty or not.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   empty: isEmpty(""),
*   nonEmpty: isEmpty("DataWeave")
* }
* ----
*
* .Output
* [source,Json,linenums]
* ----
*   {
*     "empty": true,
*     "nonEmpty": false
*   }
* ----
*/
fun isEmpty(rhs: String): Boolean = native("system::EmptyStringFunctionValue")

/**
* Returns whether an Object is empty or not.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   empty: isEmpty({}),
*   nonEmpty: isEmpty({name: "DataWeave"})
* }
* ----
*
* .Output
* [source,Json,linenums]
* ----
*   {
*     "empty": true,
*     "nonEmpty": false
*   }
* ----
*/
fun isEmpty(rhs: Object): Boolean = native("system::EmptyObjectFunctionValue")

/**
* Returns true if it receives a `DateTime` for a leap year.
*/
fun isLeapYear(dateTime: DateTime): Boolean = native("system::LeapDateTimeFunctionValue")

/**
* Returns true if it receives a `Date` for a leap year.
*/
fun isLeapYear(date: Date): Boolean = native("system::LeapLocalDateFunctionValue")

/**
* Returns true if it receives a `LocalDateTime` for a leap year.
*/
fun isLeapYear(rhs: LocalDateTime): Boolean = native("system::LeapLocalDateTimeFunctionValue")

/**
* Returns `true` if if receives a number that has any decimals in it.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   decimal: isDecimal(1.1),
*   integer: isDecimal(1)
* }
* ----
*
* .Output
* [source,Json,linenums]
* ----
*   {
*     "decimal": true,
*     "integer": false
*   }
* ----
*/
fun isDecimal(rhs: Number): Boolean = native("system::DecimalNumberFunctionValue")

/**
* Returns true is the number doesn't have any decimals.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   decimal: isInteger(1.1),
*   integer: isInteger(1)
* }
* ----
*
* .Output
* [source,Json,linenums]
* ----
*   {
*     "decimal": false,
*     "integer": true
*   }
* ----
*/
fun isInteger(rhs: Number): Boolean = native("system::IntegerNumberFunctionValue")

/**
* Returns `true` if it receives a string composed of only whitespace characters.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output  application/json
* ---
* {
*   empty: isBlank(""),
*   withSpaces: isBlank("      "),
*   withText: isBlank(" 1223")
* }
* ----
*
* .Output
* [source,Json,linenums]
* ----
*   {
*     "empty": true,
*     "withSpaces": true,
*     "withText": false
*   }
* ----
*/
fun isBlank(value: String): Boolean = isEmpty(trim(value))

/**
* Returns true if the specified number is Odd.
*/
fun isOdd(n: Number): Boolean =  mod(n, 2) != 0

/**
* Returns true if the specified number is Even.
*/
fun isEven(n: Number): Boolean =
  mod(n, 2) == 0

/**
 * Returns the element used to get the minimum result using a function.
 *
 *.Transform
 * [source,DataWeave,linenums]
 * ----
 * %dw 2.0
 * output  application/json
 * ---
 * [ { a: 1 }, { a: 2 }, { a: 3 } ] minBy (item) -> item.a
 * ----
 *
 * .Output
 * [source,Json,linenums]
 * ----
 * { "a": 1 }
 * ----
 */
fun minBy<T>(array: Array<T>, func: (item: T) -> Comparable): T =
  reduce(array, (val, prev) ->
    if(func(val) < func(prev))
      val
    else
      prev
  )

/**
 * Returns the element used to get the maximum result using a function.
 *
 *.Transform
 * [source,DataWeave,linenums]
 * ----
 * %dw 2.0
 * output  application/json
 * ---
 * [ { a: "1" }, { a: "2" }, { a: "3" } ] maxBy ((item) -> item.a as Number)
 * ----
 *
 * .Output
 * [source,Json,linenums]
 * ----
 * { "a": "3" }
 * ----
 */
fun maxBy<T>(array: Array<T>, func: (item: T) -> Comparable): T =
  reduce(array, (val, prev) ->
    if (func(val) > func(prev))
      val
    else
      prev
  )

