# Design a Text Editor with Undo & Redo

## 📜 Problem Statement
Design an in-memory text editor that:

- Stores text **row-wise (line by line)**
- Supports **insert** and **delete** operations on a single row
- Supports **Undo** and **Redo**
- Maintains edit history correctly
- Does NOT remove empty rows

Each operation modifies exactly **one row**.

---

## 🧠 Design Overview

### Core Capabilities
- Insert text at a given row and column
- Delete text from a given row and column range
- Undo the most recent change
- Redo the most recently undone change
- Read the content of a row at any time

### Key Observations
- Undo/Redo works best with **Command Pattern**
- Each edit operation should know how to **execute** and **undo**
- Redo history must be cleared after any new edit

---

## 🏗️ Design Patterns & Principles Used

- **Command Pattern** → Encapsulates edit operations
- **Stack-based Undo/Redo**
- **Single Responsibility Principle**
- **Encapsulation**

---

## 🗂️ Data Model
```mermaid
TextEditor
├── List<StringBuilder> rows
├── Stack<Command> undoStack
├── Stack<Command> redoStack

Command (interface)
├── execute()
├── undo()

AddTextCommand implements Command
├── row
├── column
├── text

DeleteTextCommand implements Command
├── row
├── startColumn
├── deletedText
```


---

## 💻 Java Implementation
```java
import java.util.*;

public class TextEditor {

    private final List<StringBuilder> rows = new ArrayList<>();
    private final Stack<Command> undoStack = new Stack<>();
    private final Stack<Command> redoStack = new Stack<>();

    /* ==================== PUBLIC APIs ==================== */

    public void addText(int row, int column, String text) {
        ensureRowExists(row);

        Command cmd = new AddTextCommand(row, column, text);
        cmd.execute();

        undoStack.push(cmd);
        redoStack.clear();
    }

    public void deleteText(int row, int startColumn, int length) {
        Command cmd = new DeleteTextCommand(row, startColumn, length);
        cmd.execute();

        undoStack.push(cmd);
        redoStack.clear();
    }

    public void undo() {
        if (undoStack.isEmpty()) return;

        Command cmd = undoStack.pop();
        cmd.undo();
        redoStack.push(cmd);
    }

    public void redo() {
        if (redoStack.isEmpty()) return;

        Command cmd = redoStack.pop();
        cmd.execute();
        undoStack.push(cmd);
    }

    public String readLine(int row) {
        return rows.get(row).toString();
    }

    /* ==================== INTERNAL HELPERS ==================== */

    private void ensureRowExists(int row) {
        while (rows.size() <= row) {
            rows.add(new StringBuilder());
        }
    }

    /* ==================== COMMAND INTERFACE ==================== */

    private interface Command {
        void execute();
        void undo();
    }

    /* ==================== ADD TEXT COMMAND ==================== */

    private class AddTextCommand implements Command {
        private final int row;
        private final int column;
        private final String text;

        AddTextCommand(int row, int column, String text) {
            this.row = row;
            this.column = column;
            this.text = text;
        }

        @Override
        public void execute() {
            rows.get(row).insert(column, text);
        }

        @Override
        public void undo() {
            rows.get(row).delete(column, column + text.length());
        }
    }

    /* ==================== DELETE TEXT COMMAND ==================== */

    private class DeleteTextCommand implements Command {
        private final int row;
        private final int startColumn;
        private final int length;
        private String deletedText;

        DeleteTextCommand(int row, int startColumn, int length) {
            this.row = row;
            this.startColumn = startColumn;
            this.length = length;
        }

        @Override
        public void execute() {
            StringBuilder sb = rows.get(row);
            deletedText = sb.substring(startColumn, startColumn + length);
            sb.delete(startColumn, startColumn + length);
        }

        @Override
        public void undo() {
            rows.get(row).insert(startColumn, deletedText);
        }
    }
}
```
