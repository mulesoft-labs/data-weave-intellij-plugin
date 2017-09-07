%dw 2.0
fun traverse(value,func) = func(value)
---
traverse(in0,(v) -> sizeOf(v))

