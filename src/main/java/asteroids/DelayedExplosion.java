package asteroids;

import java.util.Random;
import java.util.stream.Stream;

public class DelayedExplosion {

    private double x;
    private double y;
    private String type;
    private int delay; // in frames
    private boolean alive;
    private SparkSpawner sparkspawner;
    private Random rnd = new Random();

    public DelayedExplosion(double x, double y, String type, int delay) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.delay = delay;
        this.alive = true;
    }

    public void moveRelative() {
        Spaceship ship = Globals.getInstance().getShip();
        this.x = this.x - ship.getVelocity().getX();
        this.y = this.y - ship.getVelocity().getY();
    }

    public void erode() {
        this.delay--;
        if (this.delay <= 0) {
            this.alive = false;

            if (this.type.equals("hugeRoidSplit1")) {
                this.sparkspawner = Globals.getInstance().getSparkspawner();
                for (int n = 0; n < 300; n++) {
                    sparkspawner.spawn((int) x + rnd.nextInt(300) - 150, (int) y + rnd.nextInt(300) - 150, "huge");
                }
            }
            if (this.type.equals("hugeRoidSplit2")) {
                this.sparkspawner = Globals.getInstance().getSparkspawner();
                for (int n = 0; n < 500; n++) {
                    sparkspawner.spawn((int) x, (int) y, "huge");
                }
            }
            if (this.type.equals("largeRoidSplit")) {
                this.sparkspawner = Globals.getInstance().getSparkspawner();
                for (int n = 0; n < 25; n++) {
                    sparkspawner.spawn((int) x, (int) y, "large");
                }
            }
        }
    }

    public boolean isAlive() {
        return alive;
    }

}
