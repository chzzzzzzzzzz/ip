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

## TC4 — Reject empty descriptions and unknown commands

**Aim:** Verify that missing task descriptions and unsupported commands produce errors without terminating the chatbot.

### Inputs

```text
todo
deadline
event
blah
bye
```

### Expected output

```text
OOPS!!! The description of a todo cannot be empty.
OOPS!!! The description of a deadline cannot be empty.
OOPS!!! The description of an event cannot be empty.
OOPS!!! I don't know what "blah" means.
Bye. Hope to see you again soon!
```

## TC5 — Reject malformed date and time commands

**Aim:** Verify that deadline and event commands explain which required marker or date/time value is missing.

### Inputs

```text
deadline do 2103 /from now /to now
deadline do homework /by
event meeting /to 4pm
event meeting /from Mon
event meeting /from /to 4pm
event meeting /from Mon /to
bye
```

### Expected output

```text
OOPS!!! A deadline must include /by followed by its date or time.
OOPS!!! The date or time of a deadline cannot be empty.
OOPS!!! An event must include /from followed by its start time.
OOPS!!! An event must include /to followed by its end time.
OOPS!!! The start time of an event cannot be empty.
OOPS!!! The end time of an event cannot be empty.
Bye. Hope to see you again soon!
```

## TC6 — Reject invalid mark and unmark task numbers

**Aim:** Verify that missing, nonnumeric, empty-list, and out-of-range task numbers produce clear errors and that later valid commands still work.

### Inputs

```text
mark
mark two
mark 1
todo read book
mark 2
unmark 0
mark 1
list
bye
```

### Expected output

```text
OOPS!!! Tell me which task number to mark.
OOPS!!! The task number must be a whole number.
OOPS!!! The task list is empty.
Got it. I've added this task:
[T][ ] read book
Now you have 1 tasks in the list.
OOPS!!! Task number 2 does not exist. Choose a number from 1 to 1.
OOPS!!! Task number 0 does not exist. Choose a number from 1 to 1.
Nice! I've marked this task as done:
[T][X] read book
Here are the tasks in your list:
1.[T][X] read book
Bye. Hope to see you again soon!
```

## TC7 — Reject invalid command structure

**Aim:** Verify that blank commands and extra information supplied to argument-free commands are explained without terminating the chatbot.

### Inputs

```text
list extra information

bye later
bye
```

### Expected output

```text
OOPS!!! The list command does not take extra information.
OOPS!!! Please enter a command.
OOPS!!! Use bye without any extra words.
Bye. Hope to see you again soon!
```
