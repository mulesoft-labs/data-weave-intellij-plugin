%dw 2.0

fun fib(n) = do {
  @TailRec
  fun fib_help(a, b, n) =
     if (n > 0) fib_help(b, a + b, n - 1) else a
  ---
  fib_help(0, 1, n)
}
---
""