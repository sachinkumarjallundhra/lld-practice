# 🗂️ Design a File System (cd with `*` Wildcard)

## 📜 Problem Statement

Design and implement an **in-memory Unix filesystem shell** that supports the following commands:

* `mkdir <path>`
* `pwd`
* `cd <path>` (supports wildcard `*`)

### Environment & Rules

* System starts at root directory `/`
* Paths can be:

    * **Absolute** (start with `/`)
    * **Relative** (start from current directory)
* Path separator is `/`
* Multiple consecutive `/` are treated as one
* Filesystem exists **only in memory**

---

## 📂 Supported Path Segments

| Segment | Meaning                                         |
| ------- | ----------------------------------------------- |
| `.`     | Current directory                               |
| `..`    | Parent directory (root’s parent is root itself) |
| `*`     | Wildcard matching **exactly one segment**       |

---

## 🧠 Wildcard (`*`) Resolution Rules

When `*` is encountered during `cd` traversal:

1. Prefer **child directories**
2. If multiple children exist → pick **lexicographically smallest**
3. If no child directories exist → fallback to `.`
4. If still applicable → fallback to `..`

This ensures:

* Deterministic behavior
* No ambiguity
* No branching paths

---

## 🏗️ Design Overview

### Core Abstractions

#### 1. FileSystemNode (Abstract)

* Common base for files and directories
* Stores name and parent reference

#### 2. DirectoryNode

* Contains child nodes
* Uses `TreeMap` to maintain lexicographic order
* Supports wildcard resolution

#### 3. FileNode

* Placeholder (not used heavily but keeps design extensible)

---

## 🧩 Data Structures Used

```text
DirectoryNode
 ├── name
 ├── parent
 └── TreeMap<String, FileSystemNode> children
```

* `TreeMap` ensures **sorted traversal**
* Parent pointers allow fast `pwd` and `..` resolution

---

## 💻 Java Implementation

```java
import java.util.*;

public class FileSystemShell {

    abstract class FileSystemNode {
        String name;
        DirectoryNode parent;

        FileSystemNode(String name, DirectoryNode parent) {
            this.name = name;
            this.parent = parent;
        }

        abstract boolean isDirectory();
    }

    class FileNode extends FileSystemNode {
        FileNode(String name, DirectoryNode parent) {
            super(name, parent);
        }

        @Override
        boolean isDirectory() {
            return false;
        }
    }

    class DirectoryNode extends FileSystemNode {
        TreeMap<String, FileSystemNode> children = new TreeMap<>();

        DirectoryNode(String name, DirectoryNode parent) {
            super(name, parent);
        }

        @Override
        boolean isDirectory() {
            return true;
        }

        FileSystemNode getChild(String name) {
            return children.get(name);
        }

        void addChild(FileSystemNode node) {
            children.putIfAbsent(node.name, node);
        }

        DirectoryNode smallestChildDir() {
            for (FileSystemNode node : children.values()) {
                if (node.isDirectory()) {
                    return (DirectoryNode) node;
                }
            }
            return null;
        }
    }

    private final DirectoryNode root = new DirectoryNode("/", null);
    private DirectoryNode cwd = root;

    public FileSystemShell() {}

    // ================= pwd =================
    public String pwd() {
        if (cwd == root) return "/";
        LinkedList<String> path = new LinkedList<>();
        FileSystemNode cur = cwd;

        while (cur != root) {
            path.addFirst(cur.name);
            cur = cur.parent;
        }
        return "/" + String.join("/", path);
    }

    // ================= mkdir =================
    public void mkdir(String path) {
        DirectoryNode cur = path.startsWith("/") ? root : cwd;

        for (String seg : path.split("/")) {
            if (seg.isEmpty() || seg.equals(".")) continue;

            if (seg.equals("..")) {
                if (cur.parent != null) cur = cur.parent;
                continue;
            }

            FileSystemNode child = cur.getChild(seg);
            if (child == null) {
                DirectoryNode newDir = new DirectoryNode(seg, cur);
                cur.addChild(newDir);
                cur = newDir;
            } else if (child.isDirectory()) {
                cur = (DirectoryNode) child;
            } else {
                return; // cannot mkdir inside file
            }
        }
    }

    // ================= cd =================
    public void cd(String path) {
        DirectoryNode start = path.startsWith("/") ? root : cwd;
        DirectoryNode cur = start;

        for (String seg : path.split("/")) {
            if (seg.isEmpty() || seg.equals(".")) continue;

            if (seg.equals("..")) {
                if (cur.parent != null) cur = cur.parent;
            }
            else if (seg.equals("*")) {
                DirectoryNode next = cur.smallestChildDir();
                if (next != null) cur = next;
            }
            else {
                FileSystemNode node = cur.getChild(seg);
                if (node == null || !node.isDirectory()) return;
                cur = (DirectoryNode) node;
            }
        }
        cwd = cur;
    }
}
```

---

## 🧪 Example Walkthrough

```text
CWD: /
pwd → /

mkdir /a/b/c
pwd → /

cd a/b
pwd → /a/b

cd *
→ moves to /a/b/c

cd ../*
→ resolves back to /a/b/c

cd /*
→ resolves to /a (lexicographically smallest child of /)
```

---

## ⚠️ Failure Case Handling

* `cd /nope/*/x` → fails
* Current directory remains unchanged
* No partial traversal applied

---

## ⏱️ Time & Space Complexity

| Operation | Complexity               |
| --------- | ------------------------ |
| pwd       | O(depth)                 |
| mkdir     | O(depth)                 |
| cd        | O(depth × log(children)) |
| Memory    | O(total directories)     |

---

## 🏆 Why This Design Works

✔ Deterministic wildcard resolution
✔ Proper Unix semantics
✔ Safe failure handling
✔ Clean object-oriented design
✔ Interview-grade clarity

---

