package asteroids;

public class Debug {

    private long time_start_nanoseconds;
    private long time_start2_nanoseconds;
    private long time_end_nanoseconds;
    private long time_end2_nanoseconds;
    private int frame;

    public void startTime() {
        this.frame++;
        this.time_start_nanoseconds = System.nanoTime();
    }

    public void endTime() {
        this.time_end_nanoseconds = System.nanoTime();
        System.out.println("frame " + this.frame + ":   " + (this.time_end_nanoseconds - this.time_start_nanoseconds) * 1.0 / 1000000 + " ms");
    }

    public void startTime2() {
        this.time_start2_nanoseconds = System.nanoTime();
    }

    public void endTime2() {
        this.time_end2_nanoseconds = System.nanoTime();
        System.out.print("additional time: " + (this.time_end2_nanoseconds - this.time_start2_nanoseconds) * 1.0 / 1000000 + " ms    -----     ");
    }

}
