type Name<T> =  {name : T}
fun toUser(name:String) : Name<String> = {name: name}
---
toUser("Shoki")