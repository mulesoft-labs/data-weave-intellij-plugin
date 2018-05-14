/**
*
* The functions described here are packaged in the Runtime module. The module is included with Mule Runtime, but you must import it to your DataWeave code by adding the line `import dw::Runtime` to your header.
*
* This module contains functions that allow you to interact with the DataWeave engine.
*
*/
%dw 2.0

/**
 *
 * Throws an exception with the specified message.
 *
 * .Example
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * import dw::Runtime
 * ---
 * Runtime::fail("Error")
 * ----
 *
 * .Output
 * ----
 * Error
 * ----
 *
 */
fun fail (message: String = 'Error'): Nothing = native("system::fail")

/**
 *
 * Throws an exception with the specified message if the expression in the evaluator returns `true`. If not, return the value.
 *
 * .Example
 * [source,Dataweave, linenums]
 * ----
 * %dw 2.0
 * import failIf from dw::Runtime
 * output application/json
 * ---
 * { "a" : "b" } failIf ("b" is String)
 * ----
 *
 * .Output
 * ----
 * Failed
 * ----
 *
 */
fun failIf <T>(value: T, evaluator: (value: T) -> Boolean, message: String = 'Failed'): T =
    if(evaluator(value)) fail(message) else value

/**
 *
 * Stops the execution for the specified timeout (in milliseconds).
 *
 * .Example
 * [source,Dataweave, linenums]
 * ----
 * %dw 2.0
 * import * from dw::Runtime
 * output application/json
 * ---
 * {user: 1} wait 2000
 * ----
 *
 * .Output
 * ----
 * {
 *   "user": 1
 * }
 * ----
 *
 */
fun wait <T>(value: T, timeout: Number): T = native("system::wait")


/**
 *
 * Object with a result or error message. If `success` is `false`, it contains the `error`. If `true`, it provides the `result`.
 *
 */
type TryResult<T> = {
  success: Boolean,
  result?: T,
  error?: {
    kind: String,
    message: String,
    stack?: Array<String>,
    location?: String
  }
}

/**
 * Evaluates the delegate (a lambda without inputs) and returns an object with the result or an error message.
 *
 * .Example
 * [source,Dataweave, linenums]
 * ----
 * %dw 2.0
 * import try, fail from dw::Runtime
 * output application/json
 * ---
 * try(fail)
 * ----
 *
 * .Output
 * ----
 * {
 *    "success": false,
 *    "error": {
 *      "kind": "UserException",
 *      "message": "Error",
 *      "location": "Unknown location",
 *      "stack": [
 *
 *      ]
 *    }
 * }
 * ----
 */
fun try<T>(delegate: () -> T): TryResult<T> = native("system::try")


/**
* Returns the location string of a given value
*/
fun locationString(value:Any): String = native("system::locationString")


/**
* Returns all the properties configured for the underlying runtime
*/
fun props(): Dictionary<String> = native("system::props")

/**
* Returns the value of the property with the specified name or null if not defined
*/
fun prop(propertyName: String): String | Null = props()[propertyName]