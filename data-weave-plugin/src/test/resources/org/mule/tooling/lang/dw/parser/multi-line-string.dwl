%dw 2.0
output application/json
---
{
 doubleQuote: "bar
 ",
 singleQuote: 'bar
  '
  ,
   backTick:`bar
    `
 }
