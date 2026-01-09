package lld.designpattren.composite.problem;

import java.util.ArrayList;
import java.util.List;

class Folder {
    private String name;
    private List<Object> contents = new ArrayList<>();

    public int getSize() {
        int total = 0;
        for (Object item : contents) {
            if (item instanceof File) {
                total += ((File) item).getSize();
            } else if (item instanceof Folder) {
                total += ((Folder) item).getSize();
            }
        }
        return total;
    }

    public void printStructure(String indent) {
        System.out.println(indent + name + "/");
        for (Object item : contents) {
            if (item instanceof File) {
                ((File) item).printStructure(indent + " ");
            } else if (item instanceof Folder) {
                ((Folder) item).printStructure(indent + " ");
            }
        }
    }

    public void delete() {
        for (Object item : contents) {
            if (item instanceof File) {
                ((File) item).delete();
            } else if (item instanceof Folder) {
                ((Folder) item).delete();
            }
        }
        System.out.println("Deleting folder: " + name);
    }
}
