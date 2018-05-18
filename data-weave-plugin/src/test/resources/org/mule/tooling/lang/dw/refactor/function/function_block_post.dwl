%dw 2.0
fun test(arg) = do {
fun myFunction() = arg
---
myFunction()
 }