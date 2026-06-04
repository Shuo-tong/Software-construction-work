import Books.Book;
import Books.NewBook;
import Books.Reference;
import Books.TextBook;
import Rentals.Rental;
import Students.Graduate;
import Students.Student;
import Students.Undergraduate;

public class Main {
    public static void main(String[] args) {

        Student tom = new Undergraduate("Tom");


        Book[] books = {
                new NewBook  ("Python进阶", 65),
                new TextBook ("Java导论",   42),
                new Reference("C#秘笈",     40),
                new Reference("软件构造",   50)
        };
        int[] daysRented = { 12, 45, 38, 28 };

        for (int i = 0; i < books.length; i++) {
            tom.addRental(new Rental(books[i], daysRented[i], tom));
        }

        System.out.println(tom.returnedMessage());

        Student lisa = new Graduate("Lisa");
        for (int i = 0; i < books.length; i++) {
            lisa.addRental(new Rental(books[i], daysRented[i], lisa));
        }
        System.out.println("--- 研究生 Lisa 的还书结果 ---");
        System.out.println(lisa.returnedMessage());
    }
}
