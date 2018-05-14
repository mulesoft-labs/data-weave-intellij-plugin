/**
* The functions described here are packaged in the Strings module. The module is included with the Mule runtime, but you must import it to your DataWeave code by adding the line `import dw::core::Strings` to your header.
*
* Example
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import dw::core::Strings
* ---
* Strings::pluralize("box")
* ----
*
*/
%dw 2.0

/**
* Returns a Number, representing the unicode of the first character of the specified String.
* This functions fails if the String is empty
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import * from dw::core::Strings
* output application/json
* ---
* {
*   a: charCode("b")
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": 98
* }
* ----
*
*/
fun charCode(content: String): Number = native("system::CharCodeFunctionValue")

/**
* Returns a Number, representing the unicode of the character at the specified index.
* This functions if the index is invalid
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import * from dw::core::Strings
* output application/json
* ---
* {
*   a: charCodeAt("baby", 2)
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": 98
* }
* ----
*
*/
fun charCodeAt(content: String, position: Number): Number =
  charCode(content[position]!)

/**
* Returns the String of the specified Number code.
*/
fun fromCharCode(charCode: Number): String = native("system::FromCharCodeFunctionValue")

/**
* Returns the provided string transformed into its plural form.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import * from dw::core::Strings
* output application/json
* ---
* {
*   a: pluralize("box"),
*   b: pluralize("wife"),
*   c: pluralize("foot")
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "boxes",
*   "b": "wives",
*   "c": "feet"
* }
* ----
**/
fun pluralize(rhs: String): String = native("system::StringPluralizeOperator")

/**
* Returns the provided string transformed into its singular form.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import * from dw::core::Strings
* output application/json
* ---
* {
*   a: singularize("boxes"),
*   b: singularize("wives"),
*   c: singularize("feet")
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "box",
*   "b": "wife",
*   "c": "foot"
* }
* ----
**/
fun singularize(rhs: String): String = native("system::StringSingularizeFunctionValue")

/**
*
* Returns the provided string in camel case.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import * from dw::core::Strings
* output application/json
* ---
* {
*   a: camelize("customer"),
*   b: camelize("customer_first_name"),
*   c: camelize("customer name")
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "customer",
*   "b": "customerFirstName",
*   "c": "customer name"
* }
* ----
*
**/
fun camelize(rhs: String): String = native("system::StringCamelizeFunctionValue")

/**
*
* Returns the provided string with every word starting with a capital letter and no underscores. It also replaces underscores with spaces and puts a space before each capitalized word.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import * from dw::core::Strings
* output application/json
* ---
* {
*   a: capitalize("customer"),
*   b: capitalize("customer_first_name"),
*   c: capitalize("customer NAME"),
*   d: capitalize("customerName")
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "Customer",
*   "b": "Customer First Name",
*   "c": "Customer Name",
*   "d": "Customer Name"
* }
* ----
**/
fun capitalize(rhs: String): String = native("system::StringCapitalizeFunctionValue")

/**
* Returns the provided numbers set as ordinals.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import * from dw::core::Strings
* output application/json
* ---
* {
*   a: ordinalize(1),
*   b: ordinalize(8),
*   c: ordinalize(103)
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "1st",
*   "b": "8th",
*   "c": "103rd"
* }
* ----
**/
fun ordinalize(rhs: Number): String = native("system::NumberOrdinalizeFunctionValue")

/**
* Returns the provided string with every word separated by an underscore.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import * from dw::core::Strings
* output application/json
* ---
* {
*   a: underscore("customer"),
*   b: underscore("customer-first-name"),
*   c: underscore("customer NAME")
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "customer",
*   "b": "customer_first_name",
*   "c": "customer_NAME"
* }
* ----
**/
fun underscore(rhs: String): String = native("system::StringUnderscoreFunctionValue")

/**
*
* Returns the provided string with every word separated by a dash.
*
* .Transform
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import * from dw::core::Strings
* output application/json
* ---
* {
*   a: dasherize("customer"),
*   b: dasherize("customer_first_name"),
*   c: dasherize("customer NAME")
* }
* ----
*
* .Output
* [source,json,linenums]
* ----
* {
*   "a": "customer",
*   "b": "customer-first-name",
*   "c": "customer-name"
* }
* ----
**/
fun dasherize(rhs: String): String = native("system::StringDasherizeFunctionValue")
