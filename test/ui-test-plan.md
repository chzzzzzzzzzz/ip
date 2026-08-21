# UI Test Plan

Each test case starts with an empty in-memory task list. Expected output omits
the startup banner, indentation, blank lines, and underscore separators because
the runner normalizes those presentation details before comparison.

## TC1 — Add and list all task types

**Aim:** Verify that todo, deadline, and event commands create the correct task types and that list preserves their order and details.

### Inputs

```text
todo borrow book
deadline return book /by Sunday
event project meeting /from Mon 2pm /to 4pm
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] borrow book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: Sunday)
Now you have 2 tasks in the list.
Got it. I've added this task:
[E][ ] project meeting (from: Mon 2pm to: 4pm)
Now you have 3 tasks in the list.
Here are the tasks in your list:
1.[T][ ] borrow book
2.[D][ ] return book (by: Sunday)
3.[E][ ] project meeting (from: Mon 2pm to: 4pm)
Bye. Hope to see you again soon!
```

## TC2 — Mark and unmark typed tasks

**Aim:** Verify that mark and unmark update task status without losing type-specific information.

### Inputs

```text
todo read book
deadline return book /by Friday
mark 1
mark 2
unmark 2
list
bye
```

### Expected output

```text
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
Got it. I've added this task:
[D][ ] return book (by: Friday)
Now you have 2 tasks in the list.
Nice! I've marked this task as done:
[T][X] read book
Nice! I've marked this task as done:
[D][X] return book (by: Friday)
OK, I've marked this task as not done yet:
[D][ ] return book (by: Friday)
Here are the tasks in your list:
1.[T][X] read book
2.[D][ ] return book (by: Friday)
Bye. Hope to see you again soon!
```

## TC3 — Preserve arbitrary deadline text

**Aim:** Verify that deadline date and time information is stored as unrestricted text.

### Inputs

```text
deadline do homework /by no idea :-p
bye
```

### Expected output

```text
Got it. I've added this task:
[D][ ] do homework (by: no idea :-p)
Now you have 1 tasks in the list.
Bye. Hope to see you again soon!
```
