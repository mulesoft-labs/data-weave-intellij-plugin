/**
 *  The functions described here are packaged in the Crypto module. The module is included with Mule runtime, but you must import it to your DataWeave code by adding the line `import dw::Crypto` to your header.
 *
 *
 *  Example
 *  [source,DataWeave, linenums]
 *  ----
 *  %dw 2.0
 *  import dw::Crypto
 *  ---
 *  Crypto::MD5("asd" as Binary)
 *  ----
 *
 *  This module contains encrypting functions that follow common algorithms such as MD5, SHA1, etc.
 *
 */
%dw 2.0

/**
* Computes a Hash-based Message Authentication Code (HMAC) using the SHA1 hash function.
*
* .Transform
* ----
* %dw 2.0
* import dw::Crypto
* output application/json
* ---
* { "HMAC": Crypto::HMACBinary(("aa" as Binary), ("aa" as Binary)) }
* ----
*
* .Output
* ----
* {
*   "HMAC":  "\u0007\ufffd\ufffd\ufffd]\ufffd\ufffd\u0006\ufffd\u0006\ufffdsv:\ufffd\u000b\u0016\ufffd\ufffd\ufffd"
* }
* ----
*/
fun HMACBinary(content: Binary, secret: Binary): Binary = native("system::HMACFunctionValue")

/**
* Computes the hash of the specified content with the given algorithm name and returns the binary content.
*
* Algorithm name can be
*
* [%header%autowidth.spread]
* |=======
* |Name |Description
* |MD2 |The MD2 message digest algorithm as defined in RFC 1319[http://www.ietf.org/rfc/rfc1319.txt].
* |MD5 |The MD5 message digest algorithm as defined in RFC 1321[http://www.ietf.org/rfc/rfc1321.txt].
* |SHA-1 SHA-256 SHA-384 SHA-512 | Hash algorithms defined in the FIPS PUB 180-2 [http://csrc.nist.gov/publications/fips/index.html]. SHA-256 is a 256-bit hash function intended to provide 128 bits of security against collision attacks, while SHA-512 is a 512-bit hash function intended to provide 256 bits of security. A 384-bit hash may be obtained by truncating the SHA-512 output.
* |=======
*
*/
fun hashWith(content: Binary, algorithm: String = "SHA-1"): Binary = native("system::HashFunctionValue")

/**
* Computes the HMAC hash and transforms and transforms the binary result into a hexadecimal lower case string.
*/
fun HMACWith(content: Binary, secret: Binary): String =
  lower(
    dw::core::Binaries::toHex(
      HMACBinary(content, secret)
    )
  )

/**
* Computes the MD5 hash and transforms the binary result into a hexadecimal lower case string.
*
* .Transform
* ----
* %dw 2.0
* import dw::Crypto
* output application/json
* ---
* Crypto::MD5("asd" as Binary)
* ----
*
* .Output
* ----
* "7815696ecbf1c96e6894b779456d330e"
* ----
*/
fun MD5(content: Binary): String =
  lower(
    dw::core::Binaries::toHex(content hashWith "MD5")
   )

/**
* Computes the SHA1 hash and transforms and transforms the binary result into a hexadecimal lower case string.
*
* .Transform
* ----
* %dw 2.0
* import dw::Crypto
* output application/json
* ---
* Crypto::SHA1("dsasd" as Binary)
* ----
*
* .Output
* ----
* "2fa183839c954e6366c206367c9be5864e4f4a65"
* ----
*/
fun SHA1(content: Binary): String =
  lower(
    dw::core::Binaries::toHex(
      content hashWith "SHA1"
    )
   )
