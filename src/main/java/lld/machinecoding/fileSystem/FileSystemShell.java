package lld.machinecoding.fileSystem;

import java.util.LinkedList;
import java.util.TreeMap;

public class FileSystemShell {

    class Dir{
        String name;
        TreeMap<String, Dir> children = new TreeMap<>();
        Dir parent;
        Dir(String name , Dir parent){
            this.name  = name ;
            this.parent= parent;
        }
    }
    private Dir root = new Dir("/",null);
    private Dir cwd = root;

    public FileSystemShell() {

    }

    public String pwd() {
        if(cwd == root) return "/";
        LinkedList<String> path = new LinkedList<>();
        Dir cur = cwd;
        while(cur != root){
            path.addFirst(cur.name);
            cur = cur.parent;

        }
        return "/" + String.join("/",path);
    }

    public void mkdir(String path) {
        Dir cur = path.startsWith("/") ? root : cwd;

        for (String seg : path.split("/")) {
            if (seg.isEmpty() || seg.equals(".")) {
                continue;
            }

            if (seg.equals("..")) {
                if (cur.parent != null) {
                    cur = cur.parent;
                }
                continue;
            }

            cur.children.putIfAbsent(seg, new Dir(seg, cur));
            cur = cur.children.get(seg);
        }
    }

    public void cd(String path) {
        Dir start = path.startsWith("/") ? root: cwd ;
        Dir cur = start;
        for(String seg : path.split("/")){
            if(seg.isEmpty() || seg.equals(".")) continue;
            if(seg.equals("..")){
                if(cur.parent != null) cur = cur.parent;

            } else if(seg.equals("*")){
                //prefer lex smallest child, else stay, else go up if possible

                if (!cur.children.isEmpty()) {
                    cur = cur.children.firstEntry().getValue();
                }

                // else stay (do nothing)

            } else{
                if(!cur.children.containsKey(seg)) return; // fail , do not change cwd
                cur = cur.children.get(seg);
            }

        }
        cwd = cur;
    }
}