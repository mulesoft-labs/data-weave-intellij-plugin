type TypeA = { A: Number }
type TypeB = { B: Number }
type TypeAnB = TypeA & TypeB // { A: Number, B: Number }

type TypeT<T> = { key: T }

type Intersection = TypeT<TypeA & TypeB> & TypeAnB

type IntersectionUnion = TypeT<TypeA & TypeB> & TypeAnB | Number | TypeA & TypeB
---
1