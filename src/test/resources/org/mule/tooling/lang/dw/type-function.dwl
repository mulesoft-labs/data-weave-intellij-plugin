fun func(name: String): String = name
fun func1(name: String) = name
fun func2(name) = name
fun func3(callback: (msg: String)-> String ) = callback("The cat is under the table")
fun func4 <T>(callback: (msg: T)-> T ) = callback("The cat is under the table")
fun func5 <T>(identifier: String): T = ???

var lambda = (name: String): String -> name
var lambda1 = (name: String) -> name
fun echo(message) = message
output application/dw
---
echo("Mariano")
