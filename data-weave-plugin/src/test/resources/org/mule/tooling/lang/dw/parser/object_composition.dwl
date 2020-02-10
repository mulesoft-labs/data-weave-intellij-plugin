
fun takeWhile<T>(obj: Object, condition: (value: Any, key: Key) -> Boolean): Object = do {
  obj match {
    case {} -> obj
    case {k:v ~ tail} ->
        {(k): v ~ takeWhile(tail, condition)}

  }
}