/**
 * This module contains all the core data weave functionality. It is
 * automatically imported into any DataWeave script.
 */
%dw 2.0

// sduke tweak: formatting
/**
 * These are the native types of DataWeave.
 *
 * They are the only types that allow the `???` definition.
 */

/**
 * `String` type
 */
type String = ???
/**
* A `Boolean` type `true` of `false`
*/
type Boolean = ???
/**
* A number any number decimals and integers are represented by `Number` type
*/
type Number = ???
/**
* A Range type represents a sequence of numbers
*/
type Range = ???
/**
* A namespace type represented by an URI and a Prefix
*/
type Namespace = ???
/**
* An Uri
*/
type Uri = ???
/**
* A Date Time with in a TimeZone
*/
type DateTime = ???
/**
* A DateTime in the current TimeZone
*/
type LocalDateTime = ???
/**
* A Date represented by Year Month Day
*/
type Date = ???
/**
* A Time in the current TimeZone
*/
type LocalTime = ???
/**
* A Time in a specific TimeZone
*/
type Time = ???
/**
* A TimeZone
*/
type TimeZone = ???
/**
* A Period
*/
type Period = ???
/**
* A Blob
*/
type Binary = ???
/**
* A null type
*/
type Null = ???
/**
* Regex Type
*/
type Regex = ???
/**
* Bottom type. This type is can be assigned to all the types
*/
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
 * `Object` type. Represents any object, collection of Key Value Pairs
 */
type Object = ???
/**
* Represents a Type in the DataWeave Type System
*/
type Type<T> = ???
/**
* A Key of an Object
*/
type Key = ???

/**
* Generic Dictionary interface
*/
type Dictionary<T> = {_?: T}

/**
* A union type that represents all the types that can be compared to each other.
*/
type Comparable = String | Number | Boolean | DateTime | LocalDateTime | Date | LocalTime | Time | TimeZone

/**
* A union type that represents all the simple types.
*/
type SimpleType = String | Boolean | Number | DateTime | LocalDateTime | Date | LocalTime | Time | TimeZone | Period

/**
* Logs the specified value with a given `prefix`. Then it returns the
* value unchanged.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* payload application/json
* ---
* { age: log("My Age", payload.age) }
* ----
*
* .Input:
* [source,JSON,linenums]
* ----
* { "age" : 33 }
* ----
* This will print this output: `My Age - 33`.
*
* .Output
* [source,XML,linenums]
* ----
* <age>33</age>
* ----
*
* Note that besides producing the expected output, it also logs it.
*/
fun log <T>(prefix: String = "", value: T): T = native("system::log")

// sduke: significant additions to the description
/**
* Reads the input content (string or binary) with a mimeType reader for the data
* format of the input and returns the result of parsing that content.
*
* The first argument provides the content to read. The second is its
* format (or content type). The default content type is `application/dw`.
* A third, optional argument sets reader configuration properties.
*
* For other formats and reader configuration properties, see
* link:dataweave-formats[Data Formats Supported by DataWeave].
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/xml
* ---
*  read('{"name":"DataWeave"}', "application/json")
* ----
*
* .Output
* [source,XML,linenums]
* ----
* <name>DataWeave</name>
* ----
*/
fun read(stringToParse: String | Binary, contentType: String = "application/dw", readerProperties: Object = {}): Any = native("system::read")

// sduke: added an example and more detailed description
/**
* Same as the `read` operator, but `readURL` uses a URL as input. Otherwise, it
* accepts the same arguments as `read`.
*
* The default input content type is `application/dw`.
*
* For other formats and reader configuration properties, see
* link:dataweave-formats[Data Formats Supported by DataWeave].
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/xml
* ---
* read('{"url":"https://www.mulesoft.com/"}')
* ----
*
* .Output
* [source,XML,linenums]
* ----
* <?xml version='1.0' encoding='UTF-8'?>
* <url>https://www.mulesoft.com/</url>
* ----
*/
fun readUrl(url: String, contentType: String = "application/dw", readerProperties: Object = {}): Any = native("system::readUrl")

// sduke: tried to dumb down the initial description... pls verify. fixed example.
/**
* Writes input content to a specific format. Specifically, the `write` function
* returns a string with the serialized representation of the value in the
* specified mimeType (format).
*
* The first argument points to the input to write. The second is the format
* in which to write it. The default is `application/dw`. A third, optional
* argument lists writer configuration properties.
*
* See link:dataweave-formats[Data Formats Supported by DataWeave] for a full
* list of configuration properties for each format-specific writer.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/xml
* ---
* {
*   "output" : write(payload, "application/csv", {"separator" : "|"})
* }
* ----
*
* .Input
* [source,JSON,linenums]
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
* [source,XML,linenums]
* ----
* <?xml version='1.0' encoding='US-ASCII'?>
* <output>Name|Email|Id|Title
* Mr White|white@mulesoft.com|1234|Chief Java Prophet
* Mr Orange|orange@mulesoft.com|4567|Integration Ninja
* </output>
* ----
*/
fun write (value: Any, contentType: String = "application/dw", writerProperties: Object = {}): Any = native("system::write")

// sduke: tweak
/**
* Returns a pseudo-random number greater than or equal to 0.0 and less than 1.0.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   price: random() * 1000
* }
* ----
*/
fun random(): Number = native("system::random")

/**
* Returns a pseudo-random integer number between 0 and the specified number
* (exclusive).
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   price: randomInt(1000) //Returns an integer from 0 to 1000
* }
* ----
*/
fun randomInt(n: Number): Number = floor(random() * n)

//sduke: added basic example
/**
* Returns a v4 UUID using random numbers as the source.
*
* .Transform
* ----
* %dw 2.0
* output application/json
* ---
* uuid()
* ----
*
* .Output Example
* ----
* "4a185c3b-05b3-4554-b72e-d5c07524cf91"
* ----
*/
fun uuid(): String = native("system::uuid")

// sduke: tweak formatting
/**
* Returns a `DateTime` object with the current date and time.
*
* .Transform
* [source,DataWeave,linenums]
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
* [source,JSON,linenums]
* ----
* {
*   "a": "2015-12-04T18:15:04.091Z",
*   "b": 4,
*   "c": 15
* }
* ----
*
* See link:dataweave-selectors[DataWeave Selectors] for a list of possible
* selectors to use here.
*/
fun now(): DateTime = native("system::now")

/**
* Loads a native function using the specified identifier.
*/
fun native(identifier: String): Nothing = ??? //This function is just a place holder



/**
* This type is based in the link:https://docs.oracle.com/javase/8/docs/api/java/util/Iterator.html[iterator Java class]. The iterator contains a collection, and includes methods to iterate through and filter it.
*
* [NOTE]
* Just like the Java class, the iterator is designed to be consumed only once.
* For example, if you then pass this value to a link:logger-component-reference[Logger component] would result
* in consuming it and it would no longer be readable to further elements
* in the flow.
**/
type Iterator = Array {iterator: true}

// sduke: tweak
/**
* This type is based in the link:https://docs.oracle.com/javase/7/docs/api/java/lang/Enum.html[Enum Java class].
*
* It must always be used with the `class` property, specifying the full Java
* class name of the class, as shown in the example below.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/java
* ---
* "Male" as Enum {class: "com.acme.GenderEnum"}
* ----
*/
type Enum = String {enumeration: true}

/**
* java.lang.Float and java.lang.Double have speciall cases for NaN and Infinit. DataWeave doesn't have this concepts for its number multi precision nature.
* So when it is mapped to DataWeave values it is being wrapped in a Null with a Schema marker.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/java
* ---
* payload.double is NaN
* ----
*
* This scripts shows how to determine if a number came from a NaN
*/
type NaN = Null {NaN: true}

// sduke: tweak formatting
/**
* XML defines a custom type named CData that extends from String and is used
* to identify a CDATA XML block.
*
* It can be used to tell the writer to wrap the content inside CDATA or to
* check if the input string arrives inside a CDATA block. `:cdata` inherits
* from the type `:string`.
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
* [source,XML,linenums]
* ----
* <?xml version="1.0" encoding="UTF-8"?>
* <users>
*   <user><![CDATA[Mariano]]></user>
*   <age><![CDATA[31]]></age>
* </users>
* ----
**/
type CData = String {cdata: true}

//---------------------------------------------------------------------------------------------------------

/**
*
* Concatenates the elements of two arrays into a new array.
*
* If the two arrays contain different types of elements, the resulting array
* is all of `S` type elements of `Array<S>` followed by all the `T` type elements
* of `Array<T>`. Either of the arrays can also have mixed-type elements.
*
* The example concatenates an `Array<Number>` with an `Array<String>`.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   result: [0, 1, 2] ++ ["a", "b", "c"]
* }
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   "result": [0, 1, 2, "a", "b", "c"]
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
* [source,JSON,linenums]
* ----
* {
*   "a": [0, 1, true, "my string", 2, [3, 4, 5], { "a": 6}]
* }
* ----
*
**/
fun ++ <S,T>(lhs: Array<S> , rhs: Array<T>): Array<S | T> = native("system::ArrayAppendArrayFunctionValue")

//sduke: tweaks
/**
* Concatenates the characters of two strings.
*
* Strings are treated as arrays of characters, so the `++` operator concatenates
* the characters of each String as if they were arrays of single character String.
* In the example, the String 'Mule' is treated as `Array<String> ['M', 'u', 'l', 'e']`.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   name: 'Mule' ++ 'Soft'
* }
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   'name': MuleSoft
* }
* ----
**/
fun ++(lhs: String, rhs: String): String = native("system::StringAppendStringFunctionValue")

//sduke: tweaks
/**
* Concatenates two input objects and returns one flattened object.
*
* The `++` operator extracts all the key-values pairs from each object,
* then combines them together into one result object.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/xml
* ---
* concat: {aa: 'a', bb: 'b'} ++ {cc: 'c'}
* ----
*
* .Output
* [source,XML,linenums]
* ----
* <?xml version='1.0' encoding='UTF-8'?>
* <concat>
*   <aa>a</aa>
*   <bb>b</bb>
*   <cc>c</cc>
* </concat>
* ----
*
* If you leave the output as `application/dw`, the example above concatenates
* each key-value pair from the two objects `{aa: 'a', bb: 'b'} ++ {cc: 'c'}` and
* returns a single object `{aa: 'a' , bb: 'b', cc: 'c'}`.
**/
fun ++(lhs: Object , rhs: Object): Object = native("system::ObjectAppendObjectFunctionValue")

// sduke: tweak, fix types in descriptions
/**
* Appends a `LocalTime` with a `Date` object and returns a more precise
* `LocalDateTime` value.
*
* `Date` and `LocalTime` instances are written in standard Java notation,
* surrounded by pipe (`|`) symbols. The result is a `LocalDateTime` object
* in the standard Java format.
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
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*     'a': '2003-10-01T23:57:59',
*     'b': '2003-10-01T23:57:59Z'
* }
* ----
*
* Note that the order in which the two objects are concatenated is irrelevant, so
* logically, `Date` + `LocalTime` produces the same result as `LocalTime` + `Date`.
*
**/
fun ++(lhs: Date , rhs: LocalTime): LocalDateTime = native("system::LocalDateAppendLocalTimeFunctionValue")

// sduke: tweak and fix types in description
/**
* Appends a `LocalTime` with a `Date` object and returns a more precise
* `LocalDateTime` value.
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
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*     'a': '2003-10-01T23:57:59',
*     'b': '2003-10-01T23:57:59Z'
* }
* ----
*
* Note that the order in which the two objects are concatenated is irrelevant, so
* logically, `LocalTime` + `Date` produces the same result as `Date` + `LocalTime`.
*
**/
fun ++(lhs: LocalTime , rhs: Date): LocalDateTime = native("system::LocalTimeAppendLocalDateFunctionValue")

// sduke: tweaks
/**
* Appends a `Date` to a `Time` object and returns a more precise `DateTime`
* value.
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
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*     'a': '2003-10-01T23:57:59',
*     'b': '2003-10-01T23:57:59Z'
* }
* ----
*
* Note that the order in which the two objects are concatenated is irrelevant,
* so logically, `Date` + `Time`  produces the same result as `Time` + `Date`.
*
**/
fun ++(lhs: Date , rhs: Time): DateTime = native("system::LocalDateAppendTimeFunctionValue")

/**
* Appends a `Date` to a `Time` object to return a more precise `DateTime` value.
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
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*     'a': '2003-10-01T23:57:59',
*     'b': '2003-10-01T23:57:59Z'
* }
* ----
*
* Note that the order in which the two objects are concatenated is irrelevant,
* so logically, `Date` + `Time`  produces the same result as a `Time` + `Date`.
*
**/
fun ++(lhs: Time , rhs: Date): DateTime = native("system::TimeAppendLocalDateFunctionValue")

/**
* Appends a `TimeZone` to a `Date` type value and returns a `DateTime` result.
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
* [source,JSON,linenums]
* ----
* {
*   'a': '2003-10-01T23:57:59-03:00'
* }
* ----
*
**/
fun ++(lhs: Date , rhs: TimeZone): DateTime = native("system::LocalDateAppendTimeZoneFunctionValue")

/**
* Appends a `Date` to a `TimeZone` type value and returns a `DateTime` result.
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
* [source,JSON,linenums]
* ----
* {
*   'a': '2003-10-01T23:57:59-03:00'
* }
* ----
*
**/
fun ++(lhs: TimeZone , rhs: Date): DateTime = native("system::TimeZoneAppendLocalDateFunctionValue")

/**
* Appends a `TimeZone` to a `LocalDateTime` type value and returns a `DateTime`
* result.
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
* [source,JSON,linenums]
* ----
* {
*   'a': '2003-10-01T23:57:59-03:00'
* }
* ----
**/
fun ++(lhs: LocalDateTime , rhs: TimeZone): DateTime = native("system::LocalDateTimeAppendTimeZoneFunctionValue")

/**
* Appends a `LocalDateTime` to a `TimeZone` type value and returns a `DateTime`
* result.
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
* [source,JSON,linenums]
* ----
* {
*   'a': '2003-10-01T23:57:59-03:00'
* }
* ----
**/
fun ++(lhs: TimeZone , rhs: LocalDateTime): DateTime = native("system::TimeZoneAppendLocalDateTimeFunctionValue")

/**
* Appends a `TimeZone` to a `LocalTime` type value and returns a `Time` result.
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
* [source,JSON,linenums]
* ----
* {
*   'a': '2003-10-01T23:57:59-03:00'
* }
* ----
*
**/
fun ++ (lhs: LocalTime,rhs: TimeZone): Time = native('system::LocalTimeAppendTimeZoneFunctionValue')

/**
* Appends a `LocalTime` to a `TimeZone` type value and returns a `Time` result.
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
* [source,JSON,linenums]
* ----
* {
*   'a': '2003-10-01T23:57:59-03:00'
* }
* ----
*
**/
fun ++ (lhs: TimeZone,rhs: LocalTime): Time = native('system::TimeZoneValueAppendLocalTimeFunctionValue')

// sduke: pls verify this, lots of revised text in attempt to simplify...
/**
* Iterates through key-value pairs within an object and returns an object. You
* can retrieve the key, value, or index of any key-value pair in the object.
*
* This function is similar to `map`. However, instead of processing only values
* of an object, `mapObject` processes both keys and values as a tuple. Also,
* instead of returning an array with the results of processing these values
* through the function, it returns an object, which consists of a list of the
* key-value pairs that result from processing both key and value of the object
* through the function (a lambda).
*
* The function is invoked with three parameters: `value`, `key` and the `index`.
* The third parameter, `index`, is optional.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* var conversionRate=13
* ---
* priceList: payload.prices mapObject(value, key, index) -> {
*   (key) : {
*        dollars: value,
*        localCurrency: value * conversionRate,
*        index_plus_1: index + 1
*    }
* }
* ----
*
* [[input_mapobject]]
* .Input
* [source,XML,linenums]
* ----
* <prices>
*     <basic>9.99</basic>
*     <premium>53</premium>
*     <vip>398.99</vip>
* </prices>
* ----
*
* [[output]]
* .Output
* [source,JSON,linenums]
* ----
* {
*   'priceList': {
*     'basic': {
*       'dollars': '9.99',
*       'localCurrency': 129.87,
*       'index_plus_1': 1
*     },
*     'premium': {
*       'dollars': '53',
*       'localCurrency': 689,
*       'index_plus_1': 2
*     },
*     'vip': {
*       'dollars': '398.99',
*       'localCurrency': 5186.87,
*       'index_plus_1': 3
*     }
*   }
* }
* ----
*
* For each key-value pair in the input in the example, the key is preserved,
* and the value becomes an object with two properties: the original value and
* the result of multiplying the original value by a constant, a `var` that is
* defined as a directive in the header of the DataWeave script.
*
* Important:
* When you use a parameter to populate one of the keys of your output, you must
* either enclose the parameter in parentheses (for example, `(key)`), or
* you need to prepend it with a `&#36;` and enclose it in quotation marks (for
* example, `'&#36;key'`, as shown above). Otherwise, the name of the property is
* treated as a literal string.
*
* If you do not name the parameters, you need to reference them through
* placeholders: `&#36;` for the value, `&#36;&#36;` for the key, and `&#36;&#36;&#36;` for the index,
* for example:
*
* .Transform
* ----
* %dw 2.0
* output application/xml
* ---
* { 'price' : 9 } mapObject('$$' : $)
* ----
*
* .Output
* ----
* <?xml version='1.0' encoding='UTF-8'?>
* <price>9</price>
* ----
*
* The next example incorporates each index of the <<input_mapobject, price input>>
* above, as well as the input keys and values.
*
* .Transform
* ----
* %dw 2.0
* output application/json
* ---
* priceList: payload.prices mapObject(
*  ($$) : { '$$$' : $ }
* )
* ----
*
* Notice that the index is surrounded in quotes (`'&#36;&#36;&#36;'`) because this Numeric
* key must be coerced to a String and cannot be a Number. Alternatively, you
* could write `'&#36;&#36;&#36;'` as `(&#36;&#36;&#36; as String)`.
*
* When the preceding script receives the <<input_mapobject, price input>> above, it
* produces the following output:
*
* .Output
* ----
* {
*   'priceList': {
*     'basic': {
*       '0': '9.99'
*     },
*     'premium': {
*       '1': '53'
*     },
*     'vip': {
*       '2': '398.99'
*     }
*   }
* }
* ----
*
* The next example returns the same output as the first <<output, `mapObject`
* example>> above, which explicitly invokes the named parameters
* `(value, key, index)`.
*
* .Transform
* ----
* %dw 2.0
* output application/json
* var conversionRate=13
* ---
* priceList: payload.prices mapObject(
*  ($$): {
*    dollars: $,
*    localCurrency: $ * conversionRate,
*    index_plus_1: $$$ + 1
*  }
* )
* ----
*
* When you use a parameter to populate one of the keys of your output,
* as with the case of `&#36;&#36;`, you must either enclose it in quote marks
* (for example, `'&#36;&#36;'`) or parentheses (for example, `(&#36;&#36;)`). Both
* are equally valid.
*
**/
fun mapObject <K,V>(lhs: {(K)?: V}, rhs : (V, K, Number) -> Object): Object = native('system::MapObjectObjectFunctionValue')

/**
* Helper function that allows `mapObject` to work with null values.
*/
fun mapObject(value: Null, lambda : (Any, Any, Number) -> Any): Null = null

// sduke: pls verify the first sentence especially; examples tested.
/**
* Useful for mapping an object into an array, `pluck` iterates over an input
* object and returns an array consisting of keys, values, or indices of that
* object. It is an alternative to `mapObject`, which is similar but returns
* an object, instead of an array.
*
* The function can be invoked with any of these parameters: `value`, `key`,
* `index`. In the next example, 'pluck' iterates over each object within
* 'prices' and returns arrays of their keys, values, and indices.
*
* If the parameters are not named, the value is defined by default as `&#36;`,
* the key as `&#36;&#36;`, and the index as `&#36;&#36;&#36;`.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* result: {
*   keys: payload.prices pluck($$),
*   values: payload.prices pluck($),
*   indices: payload.prices pluck($$$)
* }
* ----
*
* [[input_pluck]]
* .Input
* [source,XML,linenums]
* ----
* <prices>
*     <basic>9.99</basic>
*     <premium>53.00</premium>
*     <vip>398.99</vip>
* </prices>
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   'result': {
*     'keys': [
*       'basic',
*       'premium',
*       'vip'
*     ],
*     'values': [
*       '9.99',
*       '53',
*       '398.99'
*     ],
*     'indices': [
*       0,
*       1,
*       2
*     ]
*   }
* }
* ----
*
* You can also use named keys and values as parameters. For example, the next
* transformation example iterates over the <<input_pluck, `prices` input>>
* above and outputs an array with a single element.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* payload pluck(payload.prices)
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* [
*   {
*     'basic': '9.99',
*     'premium': '53.00',
*     'vip': '398.99'
*   }
* ]
* ----
*
* Note that `payload pluck(payload.prices)` produces the same result as
* `payload pluck(payload[0])`.
*
*/
fun pluck <K,V,R>(lhs: {(K)?: V}, rhs: (V, K, Number) -> R): Array<R> = native('system::PluckObjectFunctionValue')

// sduke tweak: punctuation
/**
* Helper function that allows *pluck* to work with null values.
*/
fun pluck(lhs: Null, rhs:(Nothing, Nothing, Nothing) -> Any): Null = null

//sduke: tweak formatting only
/**
* Given two or more separate lists, the zip function can be used to merge them
* together into a single list of consecutive n-tuples.  Imagine two input lists
* each being one side of a zipper: similar to the interlocking teeth of a
* zipper, the zip function interdigitates each element from each input list,
* one element at a time.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   a: [0, 1, 2, 3] zip ['a', 'b', 'c', 'd'],
*   b: [0, 1, 2, 3] zip ['a'],
*   c: [0, 1, 2, 3] zip ['a', 'b']
* }
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   'a': [
*     [0,'a'],
*     [1,'b'],
*     [2,'c'],
*     [3,'d']
*     ],
*   'b': [
*     [0,'a']
*   ],
*   'c': [
*     [0,'a'],
*     [1,'b']
*   ]
* }
* ----
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
* [source,JSON,linenums]
* ----------------------------------------------------------------------
* {
*   'list1': ['a', 'b', 'c', 'd'],
*   'list2': [1, 2, 3],
*   'list3': ['aa', 'bb', 'cc', 'dd'],
*   'list4': [['a', 'b', 'c'], [1, 2, 3, 4], ['aa', 'bb', 'cc', 'dd']]
* }
* ----------------------------------------------------------------------
*
* .Output
* [source,JSON,linenums]
* ----------------------------------------------------------------------
* [
*   [
*     'a',
*     1,
*     'aa'
*   ],
*   [
*     'b',
*     2,
*     'bb'
*   ],
*   [
*     'c',
*     3,
*     'cc'
*   ]
* ]
* ----------------------------------------------------------------------
*
**/
fun zip<T,R>(left:Array<T>, right:Array<R>): Array<Array<T | R>> =
   left match {
       case [lh ~ ltail] ->
         right match {
            case [rh ~ rtail] -> [[lh, rh] ~ zip(ltail, rtail)]
            case [] -> []
         }
       case [] -> []
   }

// sduke: lots of changes, pls verify initial description; examples were tested
/**
* Iterates over each item in an array and returns the array of items that
* results from applying a transformation function to the elements.
*
* The function (a lambda) is invoked with the `value` and the `index` parameters.
* In the following example, custom names are defined for these parameters. Then
* both are used to construct the returned value. In this case, value is defined
* as `firstName`, and its index is defined as `position`.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* users: ['john', 'peter', 'matt'] map ((firstName, position) -> position ++ ':' ++ upper(firstName))
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   'users': [
*     '0:JOHN',
*     '1:PETER',
*     '2:MATT'
*   ]
* }
* ----
*
* If the parameters to `map` are not named, the index is defined by default as
* `&#36;&#36;`, and the value is `&#36;`. The next example produces the same output as the
* script above. Note that the selector for the key in the next example must be
* surrounded by parentheses (for example, `(&#36;&#36;)`).
*
* .Transform
* ----
* %dw 2.0
* output application/json
* ---
* users: ['john', 'peter', 'matt'] map (($$) ++ ':' ++ upper($))
* ----
*
* This next transformation script produces an array of objects from an input
* array.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* payload.users map { ($$) : upper($) }
* ----
*
* .Input
* ----
* { 'users' : ['fer', 'steven', 'lorraine', 'george'] }
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* [
*   {
*     '0': 'FER'
*   },
*   {
*     '1': 'STEVEN'
*   },
*   {
*     '2': 'LORRAINE'
*   },
*   {
*     '3': 'GEORGE'
*   }
* ]
* ----
*
**/
fun map <T,R>(items: Array<T>, mapper: (item: T, index: Number) -> R ): Array<R> = native("system::ArrayMapFunctionValue")

/**
* Helper function that allows `map` to work with null values.
*/
fun map(value: Null, mapper: (Nothing, Nothing) -> Any): Null = null

// sduke: tweak, prob need to simplify/dumb down the initial description
/**
* Maps an array of items using the specified `callback` and applies `flatten`
* to the resulting array.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* users: ['john', 'peter', 'matt'] flatMap([$$ as String, $])
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*    'users': [
*      '0',
*      'john',
*      '1',
*      'peter',
*      '2',
*      'matt'
*    ]
*  }
* ----
*/
fun flatMap<T,R>(items: Array<T>, mapper:(item: T, index: Number) -> Array<R>): Array<R> =
    flatten(items map (value,index) -> mapper(value,index))

// sduke: tweak, punctuation
/**
* Helper function that allows `flatMap` to work with null values.
*/
fun flatMap<T,R>(value: Null, mapper: (Nothing, Nothing) -> Any): Null = null

// sduke: lots of changes descriptions and examples
/**
* Returns an array that containing elements that meet the criteria specified
* by a function (a lambda). The function is invoked with two parameters: `value`
* and `index`.
*
* If the parameters are not named, the index is defined by default as `&#36;&#36;`, and
* the value is `&#36;`. In the next example, the value (`&#36;`) in the returned array
* must be greater than `2`.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   biggerThanTwo: [1, 2, 3, 4, 5] filter($ > 2)
* }
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   'biggerThanTwo': [3,4,5]
* }
* ----
*
* In the next example, the _index_ (`&#36;&#36;`) of the returned array must be greater
* than `2`.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*   indexBiggerThanTwo: [1, 2, 3, 4, 5] filter($$ > 2)
* }
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   'indexBiggerThanTwo': [4,5]
* }
* ----
*
* The next example passes named key and value parameters.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*  example3: [0, 1, 2, 3, 4, 5] filter ((key1, value1) -> key1 > 3 and value1 < 5 )
* }
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   'example3': [4]
* }
* ----
**/
fun filter <T,T>(items: Array<T> , rhs: (item: T, index: Number) -> Boolean): Array<T> = native("system::ArrayFilterFunctionValue")

// sduke: tweak
/**
* Helper function that allows `filter` to work with null values.
*/
fun filter(value: Null, filter: (item: Nothing, index: Nothing) -> Boolean): Null = null

// sduke: formatting tweaks only
/**
* Returns an object that filters an input object based on a matching condition.
*
* The function (a lambda) is invoked with three parameters: `value`, `key`, and
* `index`. If these parameters are not named, the value is defined by default as
* `&#36;`, the key `&#36;&#36;` and the index `&#36;&#36;&#36;`.
*
* This example filters an object by its value.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {'letter1': 'a', 'letter2': 'b'} filterObject ((value1) -> value1 == 'a')
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   'letter1': 'a'
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
* {'letter1': 'a', 'letter2': 'b'} filter ($ == 'a')
* ----
*/
fun filterObject <K,V>(value: {(K)?: V}, condition: (value: V, key: K, index: Number) -> Boolean): {(K)?: V} = native("system::ObjectFilterFunctionValue")

/**
* Helper function that allows `filterObject` to work with null values.
*/
fun filterObject(value: Null, condition: (value: Nothing, key: Nothing, index: Nothing) -> Boolean): Null = null

// sduke: tweak formatting
/**
* Replaces a section of a string for another, in accordance to a regular
* expression, and returns a modified string.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* b: 'admin123' replace /(\d+)/ with 'ID'
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   'b': 'adminID'
* }
* ----
**/
fun replace(lhs: String, rhs: Regex): ((Array<String>, Number) -> String) -> String = native("system::ReplaceStringRegexFunctionValue")

// sduke: tweak spelling, format
/**
* Replaces the occurrence of a given string inside other string with the
* specified value.
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
* [source,JSON,linenums]
* ----
* {
*   "b": "adminID"
* }
* ----
**/
fun replace(toBeReplaced: String, matcher: String): ((Array<String>, Number) -> String) -> String = native("system::ReplaceStringStringFunctionValue")

// sduke: tweak punctuation
/**
* When used with `replace`, `with` applies the specified function.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* ssn: "987-65-4321" replace /[0-9]/ with("x")
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* { ssn: "xxx-xx-xxxx" }
* ----
*
*/
fun with<V,U,R,X>(toBeReplaced: ((V, U) -> R) -> X, callback: (V, U) -> R ): X = toBeReplaced(callback)

// sduke: tweak formatting
/**
* Apply a reduction to the array using just two parameters: the accumulator
* (`&#36;&#36;`), and the value (`&#36;`). By default, the accumulator starts at the first
* value of the array. If the array is empty and no default value was set to the
* accumulator then null value is returned.
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
* [source,JSON,linenums]
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
* [source,JSON,linenums]
* ----
* {
*   "concat": "abcd"
* }
* ----
*
* In some cases, you may not want to use the first element of the array as an
* accumulator. To set the accumulator to something else, you must define this
* in a lambda.
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
* [source,JSON,linenums]
* ----
* {
*   "concat": "zabcd"
* }
* ----
*
* In other cases, you may want to turn an array into a string keeping the commas
* in between. The example below defines a lambda that also adds commas when
* concatenating.
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
* [source,JSON,linenums]
* ----
* {
*   "concat":  "a,b,c,d"
* }
* ----
**/
fun reduce <T>(items: Array<T>, rhs: (item: T, acumulator: T) -> T ): T | Null = native("system::ArrayReduceFunctionValue")
//Works like fold left
fun reduce <T,A>(items: Array<T>, rhs: (item: T, acumulator: A) -> A ): A = native("system::ArrayReduceFunctionValue")

// sduke: tweak
/**
* Partitions an Array into a Object that contains Arrays, according to the
* discriminator function (a lambda) that you define.
*
* The lambda is invoked with three parameters: `value`, `key`, and `index`.
* If these parameters are not named, the value is defined by default as `&#36;`,
* the key `&#36;&#36;` and the index `&#36;&#36;&#36;`.
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
* .Input
* [source,JSON,linenums]
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
* [source,JSON,linenums]
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
**/
fun groupBy <T,R>(items: Array<T> , criteria: (item: T, index: Number) -> R): {(R): Array<T>} = native("system::ArrayGroupByFunctionValue")

// sduke: formatting only
/**
* Partitions an `Object` into a `Object` that contains `Arrays`, according to
* the discriminator lambda you define.
*
* The lambda is invoked with two parameters: `value` and the `key`.
*/
fun groupBy <K,V,R,T>(object: {(K)?: V}, criteria: (value: V, key: K) -> R): {(R): Array<T>} = native("system::ObjectGroupByFunctionValue")

// sduke: formatting only
/**
* Helper function that allows *groupBy* to work with null values.
*/
fun groupBy(lhs: Null, criteria: (Nothing, Nothing) -> Any): Null = null

//REMOVE
/**
* Returns a new array that removes every occurrence of elements listed in the
* right-hand side (`rhs`) array from the left-hand side (`lhs`) array. The result
* is that same as iteratively taking `lhs - elementN`, for each `elementN` in `rhs`.
*
* When an element in the `lhs` array matches one of the values in the `rhs` array,
* it is removed. If multiple elements in the `lhs` array match a value, all matching
* values are removed from the
* `lhs`.
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
* [source,JSON,linenums]
* ----
* {
*   "a": [0],
* }
* ----
**/
fun -- <S>(lhs: Array<S> , rhs: Array<Any>): Array<S> = native("system::ArrayRemoveFunctionValue")

// sduke: tweak
/**
* Removes all the entries from the source that are present on the `toRemove`
* parameter.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*    hello: 'world',
*    name: "DW"
*  } -- {hello: 'world'}
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*    "name": "DW"
* }
* ----
*/
fun -- <K,V>(source: {(K)?: V} , toRemove: Object): {(K)?:V} = native("system::ObjectRemoveFunctionValue")

/**
 * Removes the properties from the source that are present the given list of keys.
 *
 * .Transform
 * [source,DataWeave,linenums]
 * ----
 * %dw 2.0
 * output application/json
 * ---
 * {
 *    hello: 'world',
 *    name: "DW"
 *  } -- ['hello']
 * ----
 *
 * .Output
 * [source,JSON,linenums]
 * ----
 * {
 *    "name": "DW"
 * }
 * ----
 */
fun --(source: Object, keys: Array<String>) =
  keys reduce (key, obj = source) -> (obj - key)

/**
 * Removes the properties from the source that are present the given list of keys.
 *
 * .Transform
 * [source,DataWeave,linenums]
 * ----
 * %dw 2.0
 * output application/json
 * ---
 * {
 *    hello: 'world',
 *    name: "DW"
 *  } -- ['hello' as Key]
 * ----
 *
 * .Output
 * [source,JSON,linenums]
 * ----
 * {
 *    "name": "DW"
 * }
 * ----
 */
fun --(source: Object, keys: Array<Key>) =
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
* [source,JSON,linenums]
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
*
* .Output
* [source,JSON,linenums]
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
* [source,JSON,linenums]
* ----
* {
*   "a": [[0,0,2,3]],
*   "b": [0,1],
*   "c": [2,6]
* }
* ----
*
**/
fun find(lhs: String, rhs: String): Array<Number> = native("system::StringFindStringFunctionValue")

/**
* Returns only unique values from an array that may have duplicates.
* The lambda is invoked with two parameters: `value` and `index`.
* If these parameters are not defined, the index is defined by default as `&#36;&#36;` and the value as `&#36;`.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* {
*
*     book : {
*       title : payload.title,
*       year: payload.year,
*       authors: payload.author distinctBy $
*     }
* }
* ----
*
* .Input
* [source,JSON,linenums]
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
* [source,JSON,linenums]
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
fun distinctBy <T>(items: Array<T>, rhs: (item: T, index: Number) -> Any): Array<T> = native("system::ArrayDistinctFunctionValue")

/**
* Returns an object with unlike key value pairs.
*
* The function (a lambda) is invoked with two parameters: `value` and `key`.
* If these parameters are not defined, the index is defined by default as `&#36;&#36;`
* and the value as `&#36;``.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/xml
* ---
* {
*
*      book : {
*         title : payload.book.title,
*         authors: payload.book.&author distinctBy $
*      }
* }
* ----
*
* .Input
* [source,XML,linenums]
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
* [source,XML,linenums]
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
fun distinctBy <K, V>(object: {(K)?: V}, rhs: (value: V, key: K) -> Any): Object = native("system::ObjectDistinctFunctionValue")

/**
* Returns a range within the specified boundaries. The upper boundary is inclusive.
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
* [source,JSON,linenums]
* ----
* {
*     "myRange": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
* }
* ----
*/
fun to(from: Number , to: Number): Range = native("system::ToRangeFunctionValue")

//CONTAINS
// sduke: tweak description and conform to expected exp editor format
/**
* Indicates whether an array contains a given value. Returns `true` or `false`.
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
* [source,XML,linenums]
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
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   "ContainsRequestedItem": true
* }
* ----
**/
fun contains <T>(lhs: Array<T> , rhs: Any): Boolean = native("system::ArrayContainsFunctionValue")

// sduke: tweak description and conform to expected exp editor format
/**
* Indicates whether a string contains a given substring. Returns `true` or `false`.
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
* [source,XML,linenums]
* ----
* <?xml version="1.0" encoding="UTF-8"?>
* <root>
*   <mystring>some string</mystring>
* </root>
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   "ContainsString": true
* }
* ----
**/
fun contains(lhs: String , rhs: String): Boolean = native("system::StringStringContainsFunctionValue")

// sduke: tweak description and conform to expected exp editor format, and example
/**
* Indicates whether a string contains a match to a given regular expression. Returns `true` or `false`.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* ContainsString: payload.root.mystring contains /s[t|p]rin/
* ----
*
* .Input
* [source,XML,linenums]
* ----
* <?xml version="1.0" encoding="UTF-8"?>
* <root>
*   <mystring>A very long string</mystring>
* </root>
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   "ContainsString": true
* }
* ----
**/
fun contains(lhs: String , rhs: Regex): Boolean = native("system::StringRegexContainsFunctionValue")

//ORDERBY
// sduke tweak: more to do later
/**
* Reorders the content of an array or object using a value returned by a
* function. The function (a lambda) can be invoked with these parameters:
* `value` and `index`.
*
* If the parameters are not named, the index is defined by default as
* `&#36;&#36;` and the value as `&#36;`.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* orderByLetter: [{ letter: "d" }, { letter: "e" }, { letter: "c" }, { letter: "a" }, { letter: "b" }] orderBy($.letter)
* ----
*
* .Output
* [source,JSON,linenums]
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
* Note that `orderBy($.letter)` above produces the same result as orderBy($[0]).
*
* The `orderBy` function does not have an option to order in descending order
* instead of ascending. In these cases, you can simply invert the order of
* the resulting array using `-`, for example:
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
* [source,JSON,linenums]
* ----
* { "orderDescending": [8,3,1] }
* ----
**/
fun orderBy <K,V,R, O <: {(K)?: V}>(lhs: O, rhs: (V, K) -> R): O = native('system::ObjectOrderByFunctionValue')

// sduke: tweak
/**
* Sorts an array using the specified criteria.
*
* .Transform
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
* [3,2,3] orderBy $
* ----
*
* .Output
* [source,JSON,linenums]
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
* [source,JSON,linenums]
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
* Returns null when the array is empty
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
* [source,JSON,linenums]
* ----
* {
*   "a": 1000,
*   "b": 3,
*   "d": 3.5
* }
* ----
**/
fun max <T <: Comparable>(rhs: Array<T>): T | Null = rhs maxBy $

/**
* Returns the lowest element in an array.
* Returns null when the array is empty
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
* [source,JSON,linenums]
* ----
* {
*   "a": 1,
*   "b": 1,
*   "d": 1.5
* }
* ----
**/
fun min <T <: Comparable>(rhs: Array<T>): T | Null = rhs minBy $

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
* [source,JSON,linenums]
* ----
* 6
* ----
*/
fun sum(rhs: Array<Number>): Number = (rhs reduce (value, acc) -> value + acc) default 0

//SIZEOF
//sduke: tweaks
/**
* Returns the number of elements in an array (or anything that can be converted
* to an array, such as a string).
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
* [source,JSON,linenums]
* ----
* {
*   "arraySize": 3
* }
* ----
**/
fun sizeOf(rhs: Array<Any>): Number = native("system::ArraySizeOfFunctionValue")

// sduke: tweak
/**
* Returns the number of elements in an object.
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
* [source,JSON,linenums]
* ----
* {
*   "objectSize": 2
* }
* ----
**/
fun sizeOf(rhs: Object): Number = native("system::ObjectSizeOfFunctionValue")

// sduke: tweak
/**
* Returns the byte length of a binary value.
*
**/
fun sizeOf(rhs: Binary): Number = native("system::BinarySizeOfFunctionValue")

// sduke: tweak
/**
* Returns the number of characters in an string.
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
* [source,JSON,linenums]
* ----
* {
*   "textSize": 8
* }
* ----
**/
fun sizeOf(rhs: String): Number = native("system::StringSizeOfFunctionValue")

/**
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
* [source,JSON,linenums]
* ----
* [
*    [3,5],
*    [9,5],
*    [154,0.3]
* ]
* ----
*
* .Output
* [source,JSON,linenums]
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

// sduke: tweak the language used
/**
* Performs the opposite function of <<zip arrays>>. That is, given a single
* array where each index contains an array with two elements, it outputs
* two separate arrays, each with the corresponding elements of each pair.
*
* This can also be scaled up: If the indexes in the provided array contain
* arrays with more than two elements, the output will contain as many arrays
* as there are elements for each index.
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
* [source,JSON,linenums]
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
* Note that even though example `b` can be considered the inverse function of
* example `b` in <<zip array>>, the result is not analogous because it returns
* an array of repeated elements instead of a single element. Also note that in
* example `c`, the number of elements in each component of the original array
* is not consistent. So the output only creates as many full arrays as it can,
* in this case just one.
**/
fun unzip<T>(items: Array<Array<T>>):Array<Array<T>> = do {
    var minSize = min(items map sizeOf($)) default 0
    ---
    ((0 to minSize - 1) as Array<Number>) map ((i)-> items map (item) -> item[i])
}
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
* [source,JSON,linenums]
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
* [source,JSON,linenums]
* ----
* {
*   "aa": "a-b-c"
* }
* ----
**/
fun joinBy(lhs: Array<Any>, rhs: String): String = native("system::ArrayJoinFunctionValue")

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
* [source,JSON,linenums]
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

// sduke: lots of changes to the description only.
/**
* Splits a string into an array of separate elements. The function takes a
* regular expression to identify some portion of that string, and if it finds
* a match, uses the match as separator. `splitBy` performs the opposite
* operation of `joinBy`.
*
* Using the regular expression `\^&#36;.|?*+()-`, the following example finds the
* the hyphen (`-`) in the input string (`a-b-c`), so it uses the hyphen as
* a seperator.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* split: "a-b-c" splitBy(/\^$.|?*+()-/)
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   "split": ["a","b","c"]
* }
* ----
**/
fun splitBy(lhs: String, rhs: Regex): Array<String> = native("system::StringSplitStringFunctionValue")

/**
* Splits a string into an array of separate elements. The function takes a
* another string to look for some portion of the input string, and if it finds
* a match, uses the match as separator. `splitBy` performs the opposite
* operation of `joinBy`.
*
* The following example uses the hyphen (`-`) as the separator, but the
* separator could be any other character in the input, such as `splitBy("b")`.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* split: "a-b-c" splitBy("-")
* ----
*
* .Output
* [source,JSON,linenums]
* ----
* {
*   "split": ["a", "b", "c"]
* }
* ----
**/
fun splitBy(lhs: String, rhs: String): Array<String> = native("system::StringSplitRegexFunctionValue")

// sduke: tweak
/**
* Returns `true` or `false` depending on whether a string starts with a provided
* substring.
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
* [source,JSON,linenums]
* ----
* {
*   "a": true,
*   "b": false
* }
* ----
**/
fun startsWith(lhs: String, rhs: String): Boolean = native("system::StringStartsWithFunctionValue")

//sduke: tweak
/**
* Matches a string against a regular expression and returns `true` or `false`.
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
* [source,JSON,linenums]
* ----
* {
*   "b": false
* }
* ----
*
* For use cases where you need to output or conditionally process the matched
* value, see link:dataweave-pattern-matching[Pattern Matching in DataWeave].
**/
fun matches(lhs: String, rhs: Regex): Boolean = native("system::StringMatchesFunctionValue")

// sduke: tweak
/**
* Matches a string against a regular expression and returns an array that contains
* the entire matching expression, followed by all of the capture groups that match
* the provided regex.
*
* It can be applied to the result of any evaluated expression and can return
* any evaluated expression. See link:dataweave-pattern-matching[Pattern Matching in DataWeave].
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* output application/json
* ---
* hello: "anniepoint@mulesoft.com" match(/([a-z]*)@([a-z]*).com/)
* ----
*
* .Output
* [source,JSON,linenums]
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
* In the example above, the regular expression describes an email address. It
* contains two capture groups, what is before and what is after the `@`. The
* result is an array of three elements: the first is the whole email address,
* the second matches one of the capture groups, the third matches the other one.
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
* [source,JSON,linenums]
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
* [source,JSON,linenums]
* ----
* {
*   "a": "my long text"
* }
* ----
**/
fun trim(rhs: String): String = native("system::StringTrimFunctionValue")

// sduke: tweak formatting
/**
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
* [source,JSON,linenums]
* ----
* {
*   "name": "MULESOFT"
* }
* ----
**/
fun upper(rhs: String): String = native("system::StringUpperFunctionValue")

// sduke: tweak formatting
/**
* Returns the result of the first number `a` to the power of the number
* following the `pow` operator.
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
* [source,JSON,linenums]
* ----
* {
*   "a": 8,
*   "b": 9,
*   "c": 343
* }
* ----
**/
fun pow(lhs: Number, rhs: Number): Number = native("system::PowNumberFunctionValue")

// sduke: tweak formatting
/**
* Returns the remainder after performing a division of the first number by the
* second one.
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
* [source,JSON,linenums]
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
* [source,JSON,linenums]
* ----
* {
*   "a": 2.0,
*   "b": 5.0,
*   "c": 10.0
* }
* ----
**/
fun sqrt(rhs: Number): Number = native("system::SqrtNumberFunctionValue")

// sduke: tweak formatting
/**
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
* [source,JSON,linenums]
* ----
* {
*   "a": 2,
*   "b": 2.5,
*   "c": 3.4,
*   "d": 3
* }
* ----
**/
fun abs(rhs: Number): Number = native("system::AbsNumberFunctionValue")

// sduke: tweak formatting
/**
* Rounds a number upwards, returning the first full number above than the
* one provided.
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
* [source,JSON,linenums]
* ----
* {
*   "a": 2,
*   "b": 3,
*   "c": 3
* }
* ----
**/
fun ceil(rhs: Number): Number = native("system::CeilNumberFunctionValue")

// sduke: tweak formatting
/**
* Rounds a number downwards, returning the first full number below
* the one provided.
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
* [source,JSON,linenums]
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
* [source,JSON,linenums]
* ----
* "String"
* ----
*/
fun typeOf <T>(rhs: T): Type<T> = native("system::TypeOfAnyFunctionValue")

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
* [source,JSON,linenums]
* ----
* {
*   "a": 1,
*   "b": 5,
*   "c": 4
* }
* ----
**/
fun round(rhs: Number): Number = native("system::RoundNumberFunctionValue")

// sduke: tweak language
/**
* Returns `true` or `false` depending on whether an array is empty.
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
* [source,JSON,linenums]
* ----
*   {
*     "empty": true,
*     "nonEmpty": false
*   }
* ----
*/
fun isEmpty(rhs: Array<Any>): Boolean = native("system::EmptyArrayFunctionValue")

// sduke: tweak language
/**
* Returns `true` or `false` depending on whether a string is empty.
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
* [source,JSON,linenums]
* ----
*   {
*     "empty": true,
*     "nonEmpty": false
*   }
* ----
*/
fun isEmpty(rhs: String): Boolean = native("system::EmptyStringFunctionValue")

// sduke: tweak language
/**
* Returns `true` or `false` depending on whether an object is empty.
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
* [source,JSON,linenums]
* ----
*   {
*     "empty": true,
*     "nonEmpty": false
*   }
* ----
*/
fun isEmpty(rhs: Object): Boolean = native("system::EmptyObjectFunctionValue")

// sduke: tweak
/**
* Returns `true` if it receives a `DateTime` for a leap year.
*/
fun isLeapYear(dateTime: DateTime): Boolean = native("system::LeapDateTimeFunctionValue")

// sduke: tweak
/**
* Returns `true` if it receives a `Date` for a leap year.
*/
fun isLeapYear(date: Date): Boolean = native("system::LeapLocalDateFunctionValue")

// sduke: tweak
/**
* Returns `true` if it receives a `LocalDateTime` for a leap year.
*/
fun isLeapYear(rhs: LocalDateTime): Boolean = native("system::LeapLocalDateTimeFunctionValue")

// sduke: tweak
/**
* Returns `true` if it receives a number that has any decimals in it.
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
* [source,JSON,linenums]
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
* [source,JSON,linenums]
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
* [source,JSON,linenums]
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
 * Return null when array is empty
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
 * [source,JSON,linenums]
 * ----
 * { "a": 1 }
 * ----
 */
fun minBy<T>(array: Array<T>, func: (item: T) -> Comparable): T | Null =
  reduce(array, (val, prev) ->
    if(func(val) < func(prev))
      val
    else
      prev
  )

/**
 * Returns the element used to get the maximum result using a function.
 * Return null when array is empty.
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
 * [source,JSON,linenums]
 * ----
 * { "a": "3" }
 * ----
 */
fun maxBy<T>(array: Array<T>, func: (item: T) -> Comparable): T | Null =
  reduce(array, (val, prev) ->
    if (func(val) > func(prev))
      val
    else
      prev
  )

/**
 * Returns the number of days between two dates.
 *
 * .Transform
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * output application/json
 * ---
 * {
 *   "days": daysBetween("2016-10-01T23:57:59-03:00", "2017-10-01T23:57:59-03:00")
 * }
 * ----
 *
 * .Output
 * [source,JSON,linenums]
 * ----
 *  {
 *    "days": 365
 *  }
 * ----
 */
fun daysBetween(from: Date, to: Date): Number = native("system::daysBetween")
