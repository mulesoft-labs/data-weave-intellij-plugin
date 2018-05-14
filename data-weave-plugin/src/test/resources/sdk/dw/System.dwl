/**
* The functions described here are packaged in the System module. The module is included with the Mule runtime, but you must import it to your DataWeave code by adding the line `import dw::System` to your header.
*
* .Example
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import dw::System
* ---
* System::envVar("SYS_PSWD")
* ----
*
* This module contains functions that allow you to interact with the underlying system.
*
*
*/

%dw 2.0

/**
* Returns all of the environment variables defined in the hosted System.
*
* .Example
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import dw::System
* output application/json
* ---
* System::envVars().SYS_PSWD
* ----
*/
fun envVars(): Dictionary<String> = native("system::env")

/**
* Returns an environment variable with the specified name, or `null` if it's not defined.
*
* .Example
* [source,DataWeave, linenums]
* ----
* %dw 2.0
* import dw::System
* output application/json
* ---
* System::envVar("SYS_PSWD")
* ----
*/
fun envVar(variableName: String): String | Null = envVars()[variableName]
