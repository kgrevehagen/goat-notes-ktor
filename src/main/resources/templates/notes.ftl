<!DOCTYPE html>
<html lang="en">
<head>
    <title>Goat Notes</title>
    <style>
        :root {
            --primary: #4F8A8B;
            --background: #F6F6F6;
            --card-bg: #fff;
            --card-shadow: 0 2px 8px rgba(0, 0, 0, 0.07);
            --border-radius: 8px;
            --input-bg: #f0f4f8;
            --input-border: #d1d5db;
        }

        body {
            font-family: 'Segoe UI', Arial, sans-serif;
            background: var(--background);
            margin: 0;
            padding: 0;
        }

        .container {
            max-width: 600px;
            margin: 3em auto;
            background: var(--card-bg);
            box-shadow: var(--card-shadow);
            border-radius: var(--border-radius);
            padding: 2em 2.5em;
        }

        h1 {
            color: var(--primary);
            margin-bottom: 1.5em;
        }

        ul {
            padding: 0;
            margin: 0 0 2em 0;
        }

        li {
            list-style: none;
            background: var(--input-bg);
            margin-top: 16px;
            margin-bottom: 16px;
            padding: 16px 20px;
            border-radius: var(--border-radius);
            box-shadow: 0 1px 4px rgba(0, 0, 0, 0.04);
            display: flex;
            align-items: center;
            justify-content: space-between;
        }

        .note-text {
            flex: 1;
            margin-left: 1em;
            color: #222;
            font-size: 1.08em;
        }

        form {
            margin: 0;
        }

        .create-note-form {
            margin-top: 2em;
            display: flex;
            flex-direction: column;
            gap: 1em;
        }

        textarea[name="noteText"] {
            padding: 12px;
            min-height: 80px;
            font-size: 1.08em;
            border-radius: var(--border-radius);
            border: 1px solid var(--input-border);
            background: var(--input-bg);
            resize: vertical;
        }

        button {
            background: var(--primary);
            color: #fff;
            border: none;
            border-radius: var(--border-radius);
            padding: 10px 22px;
            font-size: 1em;
            cursor: pointer;
            transition: background 0.2s;
        }

        button:hover {
            background: #357376;
        }

        .logout-form {
            margin-top: 2em;
            text-align: right;
        }

        @media (max-width: 700px) {
            .container {
                padding: 1em;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <h1>Notes for ${name}</h1>
    <form class="create-note-form" method="post" action="/ui/notes">
        <input type="hidden" name="action" value="create"/>
        <textarea name="noteText" placeholder="Enter your note" required></textarea>
        <button type="submit">Create Note</button>
    </form>
    <hr style="margin: 2em 0; border: none; border-top: 2px solid #e0e0e0;"/>
    <ul>
        <#list notes as note>
            <li style="flex-direction: column; align-items: flex-start;">
                <span class="note-text">${note.noteText}</span>
                <div style="display: flex; align-items: center; font-size:0.95em; color:#888; margin-top:10px; width: 100%; justify-content: flex-end; gap: 0.7em;">
                    ${note.createdAt?number_to_datetime}
                    <span style="font-size:1.5em; color:#bbb;">&bull;</span>
                    <form method="post" action="/ui/notes" style="display:inline; margin:0;">
                        <input type="hidden" name="action" value="delete"/>
                        <input type="hidden" name="noteId" value="${note.noteId}"/>
                        <button type="submit"
                                style="color:#e53935; background:none; border:none; font-weight:bold; font-size:1em; padding:0 8px; cursor:pointer;">
                            delete
                        </button>
                    </form>
                </div>
            </li>
        </#list>
    </ul>
    <form class="logout-form" method="post" action="/logout">
        <button type="submit">Logout</button>
    </form>
</div>
</body>
</html>
