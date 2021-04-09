%dw 2.0
ns ns0 urn:ns0
ns ns1 urn:ns1
ns ns2 urn:ns2
---
{
    a @(a: "a"): "a",
    b @(b: "b"): [
        0, 1,
        { "2" @(c: 2): {
            i: "i",
            ii: "ii",
            iii @(d: "e", f: "g"): [ 0, 1, 2],
            iv: { "0": 0, "1": 1 }
        } },
        3
    ],
    test @( attribute : "a") : 123,
    a: in0.a.b.@c,
    b: in0.a.b.@c,
    c: in0.a.b.d,
    d: in0.user.@dni,
    e: in0.user[@dni],
    f: in0.user.@ns0#dni,
    g: in0.user[@ns0#dni],
}