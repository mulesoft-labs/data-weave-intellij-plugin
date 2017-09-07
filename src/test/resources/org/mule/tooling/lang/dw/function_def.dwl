fun mergeWith(a: Object, b: Object): Object =
  a mapObject {
    ($$): (b[$$] default $)
  }
---
{}