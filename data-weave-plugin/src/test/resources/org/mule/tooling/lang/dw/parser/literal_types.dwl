%dw 2.0
output application/json


//Enum
type CardinalDirection = "North" | "East" | "South" | "West"

type OneToThree = 1 | 2 | 3

type Qubit = true | false | "Superposition" | 0 | 1

type ONE = 1

type Result<T> = {success: true, value: T } | {success: false, error: String }



//Type of parameters
fun getPort(scheme: "http" | "https"): 80 | 443 =
  scheme match {
    case "http" -> 80
    case "https" -> 443
  }


//Coercion
var c: Qubit = "0" as 0 as Qubit

var d: Qubit = "1" as Number as Qubit


//Function Dispatching on literal
fun fibo(a: 0) = 1

fun fibo(a: 1) = 1

fun fibo(n: Number): Number = fibo(n - 1) + fibo(n - 2) //TODO: fib(3) -> stackoverflow


//Somthing that throw error in TypeScript
fun iTakeFoo(foo: "foo") = {}

var test = {
  someProp: 'foo'
}

//Variable Declaration
var asd: Qubit = "Superposition"
var sdf: CardinalDirection = "East"
---
getPort("https")