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

            totalAmount += aRental.calculateFineAndBonus();

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
}