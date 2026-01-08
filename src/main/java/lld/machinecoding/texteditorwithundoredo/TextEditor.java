package lld.machinecoding.texteditorwithundoredo;

import java.util.*;

public class TextEditor {

    // ================= Document =================
    private final List<StringBuilder> rows = new ArrayList<>();

    // ================= Undo / Redo =================
    private final Deque<Command> undoStack = new ArrayDeque<>();
    private final Deque<Command> redoStack = new ArrayDeque<>();

    // ================= Public APIs =================

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

    // ================= Helpers =================

    private void ensureRowExists(int row) {
        while (rows.size() <= row) {
            rows.add(new StringBuilder());
        }
    }

    // ================= Command Pattern =================

    private interface Command {
        void execute();
        void undo();
    }

    // ---------- Add Command ----------
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

    // ---------- Delete Command ----------
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

