class Book{

    public final static int TEXT_BOOK=1;

    public final static int REFERENCE=3;

    public final static int NEW_BOOK=5;

    private String name;

    private double price;

    private int category;

    public Book(String name, double price, int category){
        this.name = name;
        this.price = price;
        this.category = category;
    }

    public String getName(){
        return name;
    }

    public double getPrice(){
        return price;
    }

    public int getCategory(){
        return category;
    }

    public String toString(){
        return "\"" + name + "\"";
    }
}
