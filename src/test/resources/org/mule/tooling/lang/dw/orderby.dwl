{
  a: in0 orderBy $.firstName,
  b: in1 orderBy ((value) -> value.firstName),
  c: in1 map $.firstName filter $.lastName == "Emiliano" orderBy $.salary,
  d: (in1)map($.firstName),
  e: (in1)map($.firstName) filter($.lastName == "Emiliano") orderBy($.salary),
  f: (in1)map($.firstName)filter($.lastName == "Emiliano")orderBy($.salary)
}