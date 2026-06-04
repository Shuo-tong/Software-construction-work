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