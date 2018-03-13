{
  a: [ "a", ("b") if true, "c" ],
  b: [ "a", ("b") if false, "c" ],
  c: [ "a", ("b") if in0."true", "c" ],
  d: [ "a", ("b") if in0."false", "c" ],
  e: [ "a", ("b") if true, "c" ].b,
  f: [ "a", ("b") if false, "c" ].b,
  g: [ "a", ("b") if false, "c" ] == [ "a", ("b") if true, "c" ],
  h: ([ "a", ("b") if false, "c" ] map $).b,
  i: sizeOf([ "a", ("b") if false, "c" ]),
  j: [ "a", ("b") if true, "c" ] filter $ != "c",
  k: [ "a", ("b") if false, "c" ] filter $ != "c"
}