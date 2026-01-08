package lld.composite;

interface FileSystemItem {
    int getSize();
    void printStructure(String indent);
    void delete();
}
