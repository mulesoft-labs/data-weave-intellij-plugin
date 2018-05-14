%dw 2.0

/**
 * Returns true if some element of the list matches the condition.
 * It stops the iterations after the first match.
 *
 * For an example, see `every`.
 */
fun some<T>(list: Array<T>, condition: (T) -> Boolean): Boolean =
  list match {
    case [] -> false
    case [head ~ tail] ->
      if(condition(head))
        true
      else
        some(tail, condition)
  }

/**
 * Returns true if every element of the list matches the condition.
 * It stops the iterations after the first negative evaluation.
 * .Transform
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * import * from dw::core::Arrays
 * var arr0: Array<Number> = []
 * output application/json
 * ---
 * {
 *   ok: [
 *     [1,2,3] some (($ mod 2) == 0),
 *     [1,2,3] some (($ mod 2) == 1),
 *     [1,2,3,4,5,6,7,8] some (log('should stop at 2 ==', $) == 2),
 *     [1,2,3] some ($ == 1),
 *     [1,1,1] every ($ == 1),
 *     [1,1,1] some ($ == 1),
 *     [1] every ($ == 1),
 *     [1] some ($ == 1),
 *   ],
 *   err: [
 *     [1,2,3] every ((log('should stop at 2 ==', $) mod 2) == 1),
 *     [1,2,3] some ($ == 100),
 *     [1,1,0] every ($ == 1),
 *     [0,1,1,0] every (log('should stop at 0 ==', $) == 1),
 *     [1] some ($ == 2),
 *     [1,2,3] every ($ == 1),
 *     arr0 every true,
 *     arr0 some true,
 *     arr0 some ($ is Number)
 *   ]
 * }
 * ----
 *
 * .Output
 * [source,json,linenums]
 * ----
 * {
 *   "ok": [
 *     true,
 *     true,
 *     true,
 *     true,
 *     true,
 *     true,
 *     true,
 *     true
 *   ],
 *   "err": [
 *     false,
 *     false,
 *     false,
 *     false,
 *     false,
 *     false,
 *     false,
 *     false,
 *     false
 *   ]
 * }
 * ----
 */
fun every<T>(list: Array<T>, condition: (T) -> Boolean): Boolean = do {
  fun private_every<T>(list: Array<T>, condition: (T) -> Boolean): Boolean = do {
    list match {
      case [] -> true
      case [head ~ tail] ->
        if(condition(head))
          private_every(tail, condition)
        else
          false
    }
  }
  ---
  list match {
    case [] -> false
    else -> private_every(list, condition)
  }
}


/**
 * Counts the matching elements using a matchingFunction(T)
 *
 * .Transform
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * output application/json
 * ---
 * [1, 2, 3] countBy (($ mod 2) == 0)
 * ----
 *
 * .Output
 * [source,json,linenums]
 * ----
 * 1
 * ----
 */
fun countBy<T>(array: Array<T>, matchingFunction: (T) -> Boolean): Number =
  (array reduce (item: T, carry: Number = 0) ->
    if(matchingFunction(item))
      carry + 1
    else
      carry) default 0

/**
 * Adds the values returned by the right hand side function
 *
 * .Transform
 * [source,DataWeave, linenums]
 * ----
 * %dw 2.0
 * output application/json
 * ---
 * sumBy([ { a: 1 }, { a: 2 }, { a: 3 } ], (item) -> item.a)
 * // same as [ { a: 1 }, { a: 2 }, { a: 3 } ] sumBy $.a
 * ----
 *
 * .Output
 * [source,json,linenums]
 * ----
 * 6
 * ----
 */
fun sumBy<T>(array: Array<T>, numberSelector: (T) -> Number): Number =
  (array reduce (item: T, carry: Number = 0) ->
    numberSelector(item) + carry) default 0

/**
* Divides an Array of items into sub arrays.
* .Transform
* [source,DataWeave, linenums]
* ----
* output application/json
* ---
* [1, 2, 3, 4, 5] divideBy 2
* ----
*
* .Output
*
* [source,json,linenums]
* ----
* [
*    [
*      1,
*      2
*    ],
*    [
*      4,
*      5
*    ]
*  ]
* ----
*/
fun divideBy<T>(items: Array<T>, amount: Number): Array<Array<T>> = do {
    fun internalDivideBy<T>(items: Array<T>, amount: Number, carry:Array<T> ): Array<Array<T>> =
      items match {
          case [x ~ xs] ->
            if(sizeOf(carry) == amount - 1)
                [carry << x ~ internalDivideBy(xs, amount, [])]
            else
               internalDivideBy(xs, amount, carry << x )
          else ->
            if(isEmpty(carry))
             []
            else
             [carry]
      }
    ---
    internalDivideBy(items, amount, [])
}
