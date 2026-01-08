package lld.composite.problem;

class File {
    private String name;
    private int size;

    public int getSize() {
        return size;
    }

    public void printStructure(String indent) {
        System.out.println(indent + name);
    }

    public void delete() {
        System.out.println("Deleting file: " + name);
    }
}
