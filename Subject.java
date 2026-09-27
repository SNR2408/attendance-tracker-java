// Model class: one subject and its attendance numbers.
public class Subject {

    private int id;
    private String name;
    private int totalClasses;
    private int attended;

    public Subject(int id, String name, int totalClasses, int attended) {
        this.id = id;
        this.name = name;
        this.totalClasses = totalClasses;
        this.attended = attended;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public int getAttended() {
        return attended;
    }

    public double getPercentage() {
        if (totalClasses == 0) {
            return 0;
        }
        return attended * 100.0 / totalClasses;
    }
}
