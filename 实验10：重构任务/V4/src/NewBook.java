class NewBook extends Book {

    public NewBook(String name, double price) {
        super(name, price);
    }

    @Override
    public double getFine() {
        return getPrice() * 0.01;
    }

    @Override
    public double baseFine() {
        return 3;
    }

    @Override
    public int getBonus() {
        return 3;
    }

    @Override
    public String toString() {
        return "新书\"" + getName() + "\"";
    }
}
