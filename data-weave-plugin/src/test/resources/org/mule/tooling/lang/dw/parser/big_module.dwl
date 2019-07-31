%dw 2.0

type Path = Array<PathElement>

type PathElement = {|
        kind: String {options: ["Object","Attribute","Array"]},
        selector: String | Number,
        namespace: Namespace | Null
    |}


fun treeMapValue(value: Any, callback: (value: Any, path: Path) -> Any) = do {
    fun doTreeMapValue(value: Any, path: Path, callback: (value: Any, path: Path) -> Any) = do {
        value match {
            case obj is  Object -> obj mapObject ((value, key, index) -> {
                (key)
                    @((key.@ mapObject (value, key) -> doTreeMapValue(value, path << {kind: "Attribute", selector: key as String, namespace: key.#}, callback))):
                        doTreeMapValue(value, path << {kind: "Object", selector: key as String, namespace: key.#}, callback)
            })
            case arr is Array -> arr map ((item, index) -> doTreeMapValue(item, path << {kind: "Array", selector: index, namespace: null}, callback))
            else -> callback(value, path)
        }
    }
    ---
    doTreeMapValue(value, [], callback)
}



fun update(objectValue: Object, fieldName: String) =
    (newValueProvider: (oldValue: Any, index: Number) -> Any): Object -> do {
        if(objectValue[fieldName]?)
            objectValue mapObject ((value, key, index) ->
                if(key ~= fieldName)
                    {(key): newValueProvider(value, index)}
                else
                {(key): value}
            )
        else
            objectValue ++ {(fieldName): newValueProvider(null, -1)}
    }


fun update(arrayValue: Array, indexToUpdate: Number) =
    (newValueProvider: (oldValue: Any, index: Number) -> Any) : Array -> do {
        if(arrayValue[indexToUpdate]?)
            arrayValue map ((value, index) ->
                if(index == indexToUpdate)
                    newValueProvider(value, index)
                else
                    value
            )
        else
            arrayValue << newValueProvider(null, -1)
    }



fun update(value: Array | Object | Null, path: Array<String | Number>) = do {
    (newValueProvider: (oldValue: Any, index: Number) -> Any): Array | Object -> do {
        fun doRecUpdate(value: Array | Object | Null, path: Array<String | Number>): Any  =
         path match {
            case [x ~ xs] ->
                if(isEmpty(xs))
                    value update x with newValueProvider($, $$)
                else
                    value update x with doRecUpdate($, xs)
            case [] -> value
        }
        ---
        doRecUpdate(value, path)
    }
}

fun update(value: Null, toUpdate:String) =
     (newValueProvider: (oldValue: Any, index: Number) -> Any) -> do {
        {
            (toUpdate) : newValueProvider(null, -1)
        }
    }

fun update(value: Null, toUpdate:Number ) =
     (newValueProvider: (oldValue: Any, index: Number) -> Any) -> do {
        [ newValueProvider(null, -1)]
    }

fun updateAttr(value: Object, path: Array<String | Number>) = do {
    (newValueProvider: (oldValue: Any, index: Number) -> Any) -> do {
        fun doUpdateAttr(objectValue: Object, fieldName: String, attr: String) = do {
             if(objectValue[fieldName]?)
                objectValue mapObject ((value, key, index) ->
                    if(key ~= fieldName)
                        {(key) @((update(key.@, attr) with newValueProvider($,$$))): value}
                    else
                        {(key): value}
            )
        else
            objectValue ++ {(fieldName) @(attr: newValueProvider(null, -1)): null}
        }
        ---
        if(sizeOf(path) > 2)
            value update path[0 to -3] with  doUpdateAttr($, path[-2], path[-1])
        else
            doUpdateAttr(value, path[-2], path[-1])
    }
}