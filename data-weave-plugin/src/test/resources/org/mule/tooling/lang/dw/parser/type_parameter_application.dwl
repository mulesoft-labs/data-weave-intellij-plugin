%dw 2.5
output application/json

fun generic<T <: {a : String}>(t: T) = t

---
generic<{a: String, b: Number}>({a: "2", b: 2})