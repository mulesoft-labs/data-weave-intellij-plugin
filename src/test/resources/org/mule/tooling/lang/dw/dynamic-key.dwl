%dw 2.0
input payload application/xml
output application/xml
fun mapKeys(element, func) =
  obj mapObject {
      (func($$)) @( ( if($.@?) $$.@ else {} ) ): mapKeys($, func)
    }

---
mapKeys(payload, ((key) -> (lower(key))))