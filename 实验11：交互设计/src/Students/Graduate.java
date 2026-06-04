package Students;

public class Graduate extends Student {

    public Graduate(String name) {
        super(name);
    }

    @Override
    public int getFreeDays() {
        return 50;
    }
}
