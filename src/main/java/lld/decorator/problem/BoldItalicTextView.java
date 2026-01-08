package lld.decorator.problem;

class BoldItalicTextView implements TextView {
    @Override
    public void render() {
        System.out.print("Rendering bold + italic text");
    }
}
