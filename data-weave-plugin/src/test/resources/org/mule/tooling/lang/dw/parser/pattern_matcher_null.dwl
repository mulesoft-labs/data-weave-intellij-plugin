fun println(message: String = '', pass: Boolean | Null | String = null): String =
  pass match {
    case true  -> log('\u001b[32m ✓ ', getPadding() ++ message)
    case false -> log('\u001b[31m ✗ ', getPadding() ++ message)
    case null  -> log('\u001b[0m   ', getPadding() ++ message)
    else       -> log('\u001b[0m   ', getPadding() ++ message)
  }

---
println("Foo")