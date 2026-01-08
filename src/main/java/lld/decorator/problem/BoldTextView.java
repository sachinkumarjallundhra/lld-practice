package lld.decorator.problem;

class BoldTextView implements TextView {
    @Override
    public void render() {
        System.out.print("Rendering bold text");
    }
}
