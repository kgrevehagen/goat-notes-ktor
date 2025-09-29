<!DOCTYPE html>
<html lang="en">
<head>
    <title>Goat Notes</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 2em; }
        h1 { color: #333; }
        ul { padding: 0; }
        li { list-style: none; background: #f4f4f4; margin-bottom: 8px; padding: 8px; border-radius: 4px; }
        form { margin-top: 2em; }
        input[type="text"] { padding: 8px; width: 300px; }
        button { padding: 8px 16px; }
    </style>
</head>
<body>
    <h1>Notes for ${name}</h1>
    <ul>
    <#list notes as note>
        <li>
            <form method="post" action="/ui/notes" style="display:inline;">
                <input type="hidden" name="action" value="delete" />
                <input type="hidden" name="noteId" value="${note.noteId}" />
                <button type="submit">Delete</button>
            </form>
            ${note.noteText}
        </li>
    </#list>
    </ul>
    <h2>Create note</h2>
    <form method="post" action="/ui/notes">
        <input type="hidden" name="action" value="create" />
        <input type="text" name="noteText" placeholder="Enter your note" required />
        <button type="submit">Add Note</button>
    </form>
    <form method="post" action="/logout">
        <button type="submit">Logout</button>
    </form>
</body>
</html>
