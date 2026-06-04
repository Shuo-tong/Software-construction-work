class Rental{

    private Book book;

    private int daysRented=30;

    private Student borrower;

    public Rental(Book book, int daysRented, Student borrower){
        this.book = book;
        this.daysRented = daysRented;
        this.borrower = borrower;
    }

    public Book getBook(){
        return book;
    }

    public int getDaysRented(){
        return daysRented;
    }

    public Student getBorrower(){
        return borrower;
    }

    /**
     * 计算本次借阅产生的罚金，并在提前归还时向借阅者发送 addBonus 消息记录奖励积分。
     * @return 本次借阅产生的罚金
     */
    public double calculateFineAndBonus(){

        double finedAmount = 0;

        switch (book.getCategory()){
            case Book.TEXT_BOOK:
                if (daysRented > 30){
                    finedAmount += (daysRented - 30) * book.getPrice() * 0.001;
                    finedAmount += 1;
                } else {
                    getBorrower().addBonus(1);
                }
                break;
            case Book.REFERENCE:
                if (daysRented > 30){
                    finedAmount += (daysRented - 30) * book.getPrice() * 0.005;
                    finedAmount += 1.5;
                } else {
                    getBorrower().addBonus(2);
                }
                break;
            case Book.NEW_BOOK:
                if (daysRented > 30){
                    finedAmount += (daysRented - 30) * book.getPrice() * 0.01;
                    finedAmount += 3;
                } else {
                    getBorrower().addBonus(3);
                }
                break;
        }

        return finedAmount;
    }

    public String toString(){


        String type=new String();


        switch(book.getCategory()){

            case Book.TEXT_BOOK: type = new String("教材"); break;
            
            case Book.REFERENCE: type = new String("参考书"); break;

            case Book.NEW_BOOK: type = new String("新书"); break;


        }

        return type+book+"借阅了"+daysRented+"天.";

    }
} 