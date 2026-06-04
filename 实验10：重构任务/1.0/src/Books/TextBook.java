package Books;

public class TextBook extends Book {

    public TextBook(String name, double price) {
        super(name, price);
    }

    @Override
    public double getFine() {
        return getPrice() * 0.001;
    }

    @Override
    public double baseFine() {
        return 1;
    }

    @Override
    public int getBonus() {
        return 1;
    }

    @Override
    public String toString() {
        return "教材\"" + getName() + "\"";
    }
}
