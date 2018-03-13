%dw 2.0
---
{
  a: using (groupedByFirstName = in0 map $.firstName) groupedByFirstName
}