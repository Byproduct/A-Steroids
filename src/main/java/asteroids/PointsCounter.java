package asteroids;

public class PointsCounter {

    private int small;
    private int medium;
    private int large;
    private int huge;
    private int mega;

    public PointsCounter() {
        small = 0;
        medium = 0;
        large = 0;
        huge = 0;
        mega = 0;
    }

    public void addPoints(int small, int medium, int large, int huge, int mega) {
        this.small += small;
        this.medium += medium;
        this.large += large;
        this.huge += huge;
        this.mega += mega;
    }

    @Override
    public String toString() {
        return "(" + this.small + "/" + this.medium + "/" + this.large + "/" + this.huge + "/" + this.mega + ")";
    }

}
