%dw 2.0

/**
 * Converts the specified binary into the hexadecimal String representation
 *
 * .Transform
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * import * from dw::core::Binaries
 * output application/json
 * ---
 * { "hex" : toHex('Mule') }
 * ----
 *
 * .Output
 * ----
 * {
 *   "hex": "4D756C65"
 * }
 * ----
 */
fun toHex(content: Binary): String = native("system::BinaryToHexFunctionValue")

/**
 * Converts an hexadecimal string representation into a binary
 *
 * .Transform
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * import * from dw::core::Binaries
 * output application/json
 * ---
 * { "binary": fromHex('4D756C65')}
 * ----
 *
 * .Output
 * ----
 * {
 *   "binary": "Mule"
 * }
 * ----
 */
fun fromHex(hexString: String): Binary = native("system::HexToBinaryFunctionValue")

/**
 * Transforms the specified binary into the base64 string representation
 *
 * .Transform
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * import * from dw::core::Binaries
 * output application/json
 * ---
 *  toBase64(fromBase64(12463730))
 *  ----
 *
 *  .Output
 *  ----
 *  12463730
 *  ----
 */
fun toBase64(content: Binary): String = native("system::BinaryToBase64FunctionValue")

/**
 * Converts a base64 string representation into a binary
 *
 * For an example, see `toBase64`.
 */
fun fromBase64(base64String: String): Binary = native("system::Base64ToBinaryFunctionValue")
