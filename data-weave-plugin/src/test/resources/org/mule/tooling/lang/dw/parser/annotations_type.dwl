%dw 2.0

type A = @User(name=123) String

type Z = @Test(v="123") {
    @Test(a=123) a @(@Test(v="123") name:"123"): @Test(a=123) String
}

fun test(a: @Test(name=123) String) = ???
---
{}