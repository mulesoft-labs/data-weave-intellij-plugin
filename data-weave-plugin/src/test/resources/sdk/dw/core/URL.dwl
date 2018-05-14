%dw 2.0


/**
 * The encodeURI() function encodes a Uniform Resource Identifier (URI) by replacing each instance of certain characters by
 * one, two, three, or four escape sequences representing the UTF-8 encoding of the character
 * (will only be four escape sequences for characters composed of two "surrogate" characters).
 *
 * Assumes that the URI is a complete URI, so does not encode reserved characters that have special meaning in the URI.
 *
 * encodeURI replaces all characters except the following with the appropriate UTF-8 escape sequences:

 * [%header%autowidth.spread]
 * |===
 * | Type                 | Includes
 * | Reserved characters  | ; , / ? : @ & = $
 * | Unescaped characters | alphabetic, decimal digits, - _ . ! ~ * ' ( )
 * | Number sign          | #
 * |===
 */
fun encodeURI(rhs: String): String = native("system::StringUrlEncodeFunctionValue")

/**
 * The decodeURI() function decodes a Uniform Resource Identifier (URI) previously created by encodeURI or by a similar routine.
 * Replaces each escape sequence in the encoded URI with the character that it represents,
 * but does not decode escape sequences that could not have been introduced by encodeURI.
 * The character `#` is not decoded from escape sequences.
 *
 * .Transform
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * import * from dw::core::URL
 * output application/json
 * ---
 * {
 *   a: decodeURI('http://asd/%20text%20to%20decode%20/text')
 * }
 * ----
 *
 * .Output
 * [source,JSON, linenums]
 * ----
 * {
 *   "a": "http://asd/ text to decode /text"
 * }
 * ----
 */
fun decodeURI(rhs: String): String = native("system::StringUrlDecodeFunctionValue")

/**
 * The encodeURIComponent() function encodes a Uniform Resource Identifier (URI) component by replacing each instance of certain characters by
 * one, two, three, or four escape sequences representing the UTF-8 encoding of the character
 * (will only be four escape sequences for characters composed of two "surrogate" characters).
 *
 * encodeURIComponent escapes all characters except the following: alphabetic, decimal digits, - _ . ! ~ * ' ( )
 * encodeURIComponent differs from encodeURI in that it encodes reserved characters and the Number sign # of encodeURI:
 *
 * [%header%autowidth.spread]
 * |===
 * | Type                 | Includes
 * | Reserved characters  |
 * | Unescaped characters | alphabetic, decimal digits, - _ . ! ~ * ' ( )
 * | Number sign          |
 * |===
 *
 * .Transform
 *  [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * import * from dw::core::URL
 * output application/json
 * ---
 * {
 *   "comparing_encode_functions_output" : {
 *   	"encodeURIComponent" : encodeURI(" PATH/ TO /ENCODE "),
 *   	"encodeURI" : encodeURI(" PATH/ TO /ENCODE "),
 *   	"encodeURIComponent_to_hex" : encodeURIComponent(";,/?:@&="),
 *   	"encodeURI_not_to_hex" : encodeURI(";,/?:@&="),
 *   	"encodeURIComponent_not_encoded" : encodeURIComponent("-_.!~*'()"),
 *   	"encodeURI_not_encoded" : encodeURI("-_.!~*'()")
 *   },
 *   "comparing_decode_function_output": {
 *   	"decodeURIComponent" : decodeURIComponent("%20PATH/%20TO%20/DECODE%20"),
 *   	"decodeURI" : decodeURI("%20PATH/%20TO%20/DECODE%20"),
 *   	"decodeURIComponent_from_hex" : decodeURIComponent("%3B%2C%2F%3F%3A%40%26%3D"),
 *   	"decodeURI_from_hex" : decodeURI("%3B%2C%2F%3F%3A%40%26%3D"),
 *   	"decodeURIComponent_from_hex" : decodeURIComponent("%2D%5F%2E%21%7E%2A%27%28%29%24"),
 *   	"decodeURI_from_hex" : decodeURI("%2D%5F%2E%21%7E%2A%27%28%29%24")
 *   }
 * }
 * ----
 *
 * .Output
 * [source,JSON, linenums]
 * ----
 * {
 *   "comparing_encode_functions_output": {
 *     "encodeURIComponent": "%20PATH/%20TO%20/ENCODE%20",
 *     "encodeURI": "%20PATH/%20TO%20/ENCODE%20",
 *     "encodeURIComponent_to_hex": "%3B%2C%2F%3F%3A%40%26%3D",
 *     "encodeURI_not_to_hex": ";,/?:@&=",
 *     "encodeURIComponent_not_encoded": "-_.!~*'()",
 *     "encodeURI_not_encoded": "-_.!~*'()"
 *   },
 *   "comparing_decode_function_output": {
 *     "decodeURIComponent": " PATH/ TO /DECODE ",
 *     "decodeURI": " PATH/ TO /DECODE ",
 *     "decodeURIComponent_from_hex": ";,/?:@&=",
 *     "decodeURI_from_hex": ";,/?:@&=",
 *     "decodeURIComponent_from_hex": "-_.!~*'()$",
 *     "decodeURI_from_hex": "-_.!~*'()$"
 *   }
 * }
 * ----
 */
fun encodeURIComponent(rhs: String): String = native("system::StringUrlEncodeComponentFunctionValue")

/**
 * The decodeURIComponent() function decodes a Uniform Resource Identifier (URI) component previously created by
 * encodeURIComponent or by a similar routine.
 *
 * For an example, see `encodeURIComponent`.
 */
fun decodeURIComponent(rhs: String): String = native("system::StringUrlDecodeComponentFunctionValue")

type URI = {
  isValid: Boolean,
  host?: String,
  authority?: String,
  fragment?: String,
  path?: String,
  port?: Number,
  query?: String,
  scheme?: String,
  user?: String,
  isAbsolute?: Boolean,
  isOpaque?: Boolean
}

/**
 * Parses an URL and returns an URI object.
 * The `isValid: Boolean` property dennotes if the parse was succeed.
 * Every field in the URI object is optional, and it will be present only if it was present in the original URL
 *
 * .Transform
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * import * from dw::core::URL
 * output application/json
 * ---
 * {
 *   'composition': parseURI('https://en.wikipedia.org/wiki/Uniform_Resource_Identifier#footer')
 * }
 * ----
 *
 * .Output
 * [source,JSON, linenums]
 * ----
 * {
 *   "composition": {
 *     "isValid": true,
 *     "raw": "https://en.wikipedia.org/wiki/Uniform_Resource_Identifier#footer",
 *     "host": "en.wikipedia.org",
 *     "authority": "en.wikipedia.org",
 *     "fragment": "footer",
 *     "path": "/wiki/Uniform_Resource_Identifier",
 *     "scheme": "https",
 *     "isAbsolute": true,
 *     "isOpaque": false
 *   }
 * }
 */
fun parseURI(uri: String): URI = native("system::StringParseUrlFunctionValue")


/**
 * Compose is a custom interpolator used to replace URL components by the encodeURIComponent result of it.
 *
 * .Transform
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * import * from dw::core::URL
 * output application/json
 * ---
 * { 'composition': compose `encoding http://asd/$(' text to encode ')/text now` }
 * ----
 *
 * .Output
 * [source,JSON, linenums]
 * ----
 * {
 *   "composition": "encoding http://asd/%20text%20to%20encode%20/text now"
 * }
 */
fun compose(parts: Array<String>, interpolation: Array<String>): String =
  parts[0] ++ (interpolation map (encodeURIComponent($) ++ parts[($$ + 1)]) joinBy '')
