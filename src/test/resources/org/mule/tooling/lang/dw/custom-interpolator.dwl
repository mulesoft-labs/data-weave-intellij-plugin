[
  SQL `SELECT * FROM table WHERE id = $(1) AND name = $('a')`,
  SQL`SELECT * FROM table WHERE id = $(1) AND name = $('a')`,
  SQL`$('p')`,
  SQL `$('p')`,
  SQL `$('a')$('b')`,
  SQL `$('a')---$('b')`,
  SQL `---$('a')---$('b')---`,
  SQL `$('p')bbb`,
  SQL `aaa$('p')`,
  SQL `aaa$('p')bbb`,
  a map SQL`asdasd`
]