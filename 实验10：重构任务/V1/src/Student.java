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
        double finedAmount=0;

        int bonus = 0;

        String message=new String();

        for (Rental aRental:rentals){

            bonus = 0;
            finedAmount=0;
            switch(aRental.getBook().getCategory()){
                case Book.TEXT_BOOK:
                    if (aRental.getDaysRented()> 30){
                        finedAmount += (aRental.getDaysRented()-30)*aRental.getBook().getPrice()*0.001;
                        finedAmount += 1;
                    } else {
                        bonus = 1;
                    }
                    break;
                case Book.REFERENCE:
                    if (aRental.getDaysRented()> 30){
                        finedAmount += (aRental.getDaysRented()-30)*aRental.getBook().getPrice()*0.005;
                        finedAmount += 1.5;
                    } else {
                        bonus = 2;
                    }
                    break;

                case Book.NEW_BOOK:

                    if (aRental.getDaysRented()> 30){
                        finedAmount+= (aRental.getDaysRented()-30)*aRental.getBook().
                                getPrice()*0.01;
                        finedAmount += 3;
                    }else {
                        bonus = 3;

                    }
                    break;
            }
            addBonus(bonus);

            totalAmount += finedAmount;

            while (getBonus()>=7 && totalAmount > 1){


                addBonus (-7);


                totalAmount--;

            }

            message += aRental+"\n";
        }
        message += String.format("缴纳罚金:%.2f元.\n",totalAmount);
        message += "还书奖励:"+getBonus()+"点.\n";
        return message;

    }
}