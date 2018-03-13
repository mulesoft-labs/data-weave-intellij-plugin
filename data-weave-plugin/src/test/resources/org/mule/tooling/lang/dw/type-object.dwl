type StringValueObject = {_ : String}
type Dictionary<T> = {_ : T}
type WithAttributes = {_ @(_ : String) : String}
fun a(object: {_: String | Number}) = ???
---
{}
