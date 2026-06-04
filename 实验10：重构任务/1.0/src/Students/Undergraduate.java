package Students;

public class Undergraduate extends Student {

    public Undergraduate(String name) {
        super(name);
    }

    @Override
    public int getFreeDays() {
        return 30;
    }
}
