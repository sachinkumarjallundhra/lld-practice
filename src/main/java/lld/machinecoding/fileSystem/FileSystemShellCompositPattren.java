package lld.machinecoding.fileSystem;

import java.util.LinkedList;
import java.util.TreeMap;

public class FileSystemShellCompositPattren {
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

        boolean hasChildren() {
            return !children.isEmpty();
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

    private DirectoryNode root = new DirectoryNode("/", null);
    private DirectoryNode cwd = root;

    public FileSystemShellCompositPattren() {}

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
