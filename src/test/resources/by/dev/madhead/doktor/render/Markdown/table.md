---
title: Tables
---

Markdown does not have special markup for tables.
So, you can create tables by simply inlining HTML markup:

<table>
	<thead>
		<tr>
			<th>Character</th>
			<th>Lightsaber color</th>
		</tr>
	</thead>
	<tbody>
		<tr>
			<td>Darth Vader</td>
			<td>red</td>
		</tr>
		<tr>
			<td>Mace Windu</td>
			<td>purple</td>
		</tr>
	</tbody>
</table>

But, there are Markdown flavours & extensions!
For example, [this one](https://help.github.com/articles/organizing-information-with-tables/) allows you to create tables with pipes `|` and hyphens `-`.
Hyphens are used to create each column's header, while pipes separate each column.
You must include a blank line before your table in order for it to correctly render.

|   Character   |   Lightsaber color  |
|---------------|---------------------|
|  Darth Vader  |  red                |
|  Mace Windu   |  purple             |

Aligns are supported as well:

| Left-aligned | Center-aligned | Right-aligned |
| :---         |     :---:      |          ---: |
| git status   | git status     | git status    |
| git diff     | git diff       | git diff      |

Pipes in tables:

| Name     | Character |
| ---      | ---       |
| Backtick | `         |
| Pipe     | \|        |
