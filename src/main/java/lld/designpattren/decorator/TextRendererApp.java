package lld.designpattren.decorator;

public class TextRendererApp {
    public static void main(String[] args) {
        TextView text = new PlainTextView("Hello, World!");

        System.out.print("Plain: ");
        text.render();
        System.out.println();

        System.out.print("Bold: ");
        TextView boldText = new BoldDecorator(text);
        boldText.render();
        System.out.println();

        System.out.print("Italic + Underline: ");
        TextView italicUnderline = new UnderlineDecorator(new ItalicDecorator(text));
        italicUnderline.render();
        System.out.println();

        System.out.print("Bold + Italic + Underline: ");
        TextView allStyles = new UnderlineDecorator(new ItalicDecorator(new BoldDecorator(text)));
        allStyles.render();
        System.out.println();
    }
}
