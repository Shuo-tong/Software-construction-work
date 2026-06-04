import java.util.ArrayList;

class Student{

    private String name;

    private int bonus=0;

    private ArrayList<Rental> rentals = new ArrayList<Rental>(10);

    public Student(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }

    public int getBonus(){
        return bonus;
    }

    public void addBonus(int delta){
        this.bonus += delta;
    }

    public void addRental(Rental rental){
        rentals.add(rental);
    }

    public String returnedMessage(){

        double totalAmount = 0;

        String message = new String();

        for (Rental aRental : rentals){

            totalAmount += calculateFineAndBonus(aRental);

            while (getBonus() >= 7 && totalAmount > 1){
                addBonus(-7);
                totalAmount--;
            }

            message += aRental + "\n";
        }
        message += String.format("缴纳罚金:%.2f元.\n", totalAmount);
        message += "还书奖励:" + getBonus() + "点.\n";
        return message;

    }

    /**
     * 计算单次借阅的罚金，并把提前归还的奖励积分累加到学生上。
     * @param aRental 一次借阅记录
     * @return 本次借阅产生的罚金
     */
    private double calculateFineAndBonus(Rental aRental){

        double finedAmount = 0;

        switch (aRental.getBook().getCategory()){
            case Book.TEXT_BOOK:
                if (aRental.getDaysRented() > 30){
                    finedAmount += (aRental.getDaysRented() - 30) * aRental.getBook().getPrice() * 0.001;
                    finedAmount += 1;
                } else {
                    addBonus(1);
                }
                break;
            case Book.REFERENCE:
                if (aRental.getDaysRented() > 30){
                    finedAmount += (aRental.getDaysRented() - 30) * aRental.getBook().getPrice() * 0.005;
                    finedAmount += 1.5;
                } else {
                    addBonus(2);
                }
                break;
            case Book.NEW_BOOK:
                if (aRental.getDaysRented() > 30){
                    finedAmount += (aRental.getDaysRented() - 30) * aRental.getBook().getPrice() * 0.01;
                    finedAmount += 3;
                } else {
                    addBonus(3);
                }
                break;
        }

        return finedAmount;
    }
}