package Rentals;

import Books.Book;
import Students.Student;

public class Rental {

    private Book book;

    private int daysRented = 30;

    private Student borrower;

    public Rental(Book book, int daysRented, Student borrower) {
        this.book = book;
        this.daysRented = daysRented;
        this.borrower = borrower;
    }

    public Book getBook() {
        return book;
    }

    public int getDaysRented() {
        return daysRented;
    }

    public Student getBorrower() {
        return borrower;
    }

    /**
     * 计算本次借阅产生的罚金，并在提前归还时向借阅者发送 addBonus 消息记录奖励积分。
     * @return 本次借阅产生的罚金
     */
    public double calculateFineAndBonus() {

        double finedAmount = 0;
        int freeDays = getBorrower().getFreeDays();

        if (daysRented > freeDays) {
            finedAmount += (daysRented - freeDays) * book.getFine();
            finedAmount += book.baseFine();
        } else {
            getBorrower().addBonus(book.getBonus());
        }

        return finedAmount;
    }



    public String toString() {
        return book + "借阅了" + daysRented + "天.";
    }
}
