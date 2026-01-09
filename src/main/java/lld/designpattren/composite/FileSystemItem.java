package lld.designpattren.composite;

interface FileSystemItem {
    int getSize();
    void printStructure(String indent);
    void delete();
}
