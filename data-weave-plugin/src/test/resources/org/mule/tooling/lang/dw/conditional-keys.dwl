{
  a: { a: "a", (b: "b") if true, c: "c" },
  b: { a: "a", (b: "b") if false, c: "c" },
  c: { a: "a", (b: "b") if in0."true", c: "c" },
  d: { a: "a", (b: "b") if in0."false", c: "c" },
  e: { a: "a", (b: "b") if true, c: "c" }.b,
  f: { a: "a", (b: "b") if false, c: "c" }.b,
  g: { a: "a", (b: "b") if false, c: "c" } == { a: "a", (b: "b") if true, c: "c" }
}