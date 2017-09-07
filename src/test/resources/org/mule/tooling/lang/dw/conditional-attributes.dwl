users: {
    user @((name: "Mariano") if (in0.male), age: 31) : "Achaval",
    country: { c @((loc: "Arg") if true) : {}}.c.@loc
}