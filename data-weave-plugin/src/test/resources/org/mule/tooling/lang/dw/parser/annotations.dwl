%dw 2.0

annotation Memorize ()

annotation Stream(force: Boolean)

annotation DataFormat()

@Memorize
fun myFun(a: Number, b: Number) = a + b

@DataFormat()
var a = {}

fun myFunc(@Stream a : String) =  a

fun myFunc(@Stream(force = true) a : String) =  a
---
{}