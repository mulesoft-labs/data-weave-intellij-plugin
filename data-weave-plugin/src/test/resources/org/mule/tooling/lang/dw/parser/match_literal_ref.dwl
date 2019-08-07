fun asExpressionString(path: Path): String =
    path reduce ((item, accumulator = "") -> do {
        var selectorExp = item.kind match {
            case ATTRIBUTE_TYPE -> ".@$"
            case ARRAY_TYPE -> "[$]"
            case OBJECT_TYPE -> ".$"
        }
        ---
        if(isEmpty(accumulator))
            selectorExp
        else
           accumulator ++ selectorExp
     })
