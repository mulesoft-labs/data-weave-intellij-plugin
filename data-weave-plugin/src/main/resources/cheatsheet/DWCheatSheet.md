# DW Cheat Sheet

## Values

* Regex: `/[0-9]+/`
* Number: `123`
* Boolean: `true`
* String: `"Foo"`
* Temporals: `|1990-12-12T12:00:00Z|` `|12:00:00|`
* Array: `[123]`
* Range: `1 to 100`
* Object: `{ name @(att: 123): true}`
* Namespace: `ns foo http://foo*com`
* Functions: `fun test(a: String): String = upper(a)` 
* Lambdas: `(a: String): String ->  upper(a)`
   

## Type System (Structural)

* Simple Type: `String Number Boolean Date...`
* Object Type: `{a: String}`
* Array Type: `Array<String>`
* Union Type: `String | Number`
* Intersection Type: `{a: Boolean} & {c: Number}` 
* Any Type: `Any`
* Nothing Type: `Nothing`
* Type Parameters (Generics): `<T>`
* Null Type: `Null`

## Selectors

* Field: `payload.foo`
* Attribute: `payload.@foo`
* MultiSelector: `payload.*foo`
* Index: `payload[0]` | `payload[-1]`
* Range: `payload[1 to 10]`
* Namespace: `payload.#`
* All Attributes: `payload.#`
* Dynamic: `payload[foo]`
* Filter: `payload[? ($.@name == "foo")]`

## Operators

* Logical: `< > and or ! != =>`
* Pattern Matching:
    * Type: `case is String`
    * Literal: `case "foo"`
    * Regex: `case match /foo/`
    * Object Array deconstruct:  `case [x ~ xs]`
* Object/Array reconstruct: `case [x ~ xs]`
* If/Else: `if(true) 123 else 456`
* Blocks: `do { var a = "mylocalvar" --- mylocalvar}`

## Sugar syntax things

* Conditional elements:
    * In Object: `{ (a: 123) if (true), name: "Foo"}`
    * In Array: `[(123) if(false)]`
    * In Attributes: `{ a @((c: 123) if (true)): 123}`

* Dynamic elements:
    * Object: `{ (1 to 10 map {a: $})}`








