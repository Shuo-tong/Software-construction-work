class Reference extends Book {

    public Reference(String name, double price) {
        super(name, price);
    }

    @Override
    public double getFine() {
        return getPrice() * 0.005;
    }

    @Override
    public double baseFine() {
        return 1.5;
    }

    @Override
    public int getBonus() {
        return 2;
    }

    @Override
    public String toString() {
        return "参考书\"" + getName() + "\"";
    }
}
