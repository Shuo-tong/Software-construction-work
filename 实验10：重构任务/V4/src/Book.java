abstract class Book {

    private String name;

    private double price;

    public Book(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    //  每超期一天的罚金（与书价、类型有关）。
    public abstract double getFine();


    //超期后的基础罚金
    public abstract double baseFine();

    //提前归还时一次性奖励的积分。
    public abstract int getBonus();

    //类别前缀 + 书名（带双引号），如 教材"Java导论"
    @Override
    public abstract String toString();
}
