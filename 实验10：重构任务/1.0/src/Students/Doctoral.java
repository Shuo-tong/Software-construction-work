package Students;

public class Doctoral extends Student {

    public Doctoral(String name) {
        super(name);
    }

    @Override
    public int getFreeDays() {
        return 60;
    }
}
