ns ns0 http://mulesoft.com/dw
ns ns1 http://mulesoft.com/mule
type XmlUser = {
                  ns0#root:
                      {
                        ns0#name: String,
                        ns0#lastName: String
                        }
                  }

type close = {| name: String, age: Number |}
type ordered = {- name: String, age: Number -}
type orderedClose = {-| name: String, age: Number |-}
type repeatedFields = {name: String, age: Number, friend *: String }
type repeatedFieldsOptional = {name: String, age: Number, friend*: String , brother *?: String }
type optionalField = {name?: String, age: Number}

type Book = {name: String, authors: Array<User>, price: Number}
type User = {name: String,lastName: String, "key with spaces": Number}
type Account = {email: String, id: String}
type MyUser = Object {class: "com.mulesoft.MyUser"}

type FieldDiff = {|
  literal?: "SIMILAR" {"typeId": "org.mule.extension.salesforce.api.utility.DifferenceType"},
  union?: ("DIFFERENT" | "NULL" | "SAME" | "SIMILAR") {"typeId": "org.mule.extension.salesforce.api.utility.DifferenceType"},
  name?: String
|} {"typeAlias": "FieldDiff",
"class": "org.mule.extension.salesforce.api.utility.FieldDiff",
"typeId": "org.mule.extension.salesforce.api.utility.FieldDiff"}

fun test(value:Null):Null = null

fun test2(value:Null, callback: (Nothing , Nothing) -> Any):Null = null

fun toAccount(user: User) : Account = {
   email: "$(user.name).$(user.lastName)@mulesoft.com",
   id: user.name
}
---
toAccount({name: "Mariano", lastName: "Achaval"})