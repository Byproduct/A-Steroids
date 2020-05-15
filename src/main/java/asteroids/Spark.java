package asteroids;

import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Spark extends Entity {

    private int range;
    private int spawnDelay;
    private String scale;
    Random rnd = new Random();

    public Spark() {
    }

    public Spark(int x, int y, String scale) {
        super(new Polygon(6, -1,
                6, 1,
                -6, 1,
                -6, -1),
                x, y);
        this.scale = scale;
        this.spawnDelay = 0;
        int acceleration = 0;

        if (scale.equals("small")) {
            acceleration = 100 + rnd.nextInt(100);
            this.range = 5 + rnd.nextInt(10);
        }

        if (scale.equals("medium")) {
            acceleration = 50 + rnd.nextInt(50);
            this.range = 20 + rnd.nextInt(15);
        }

        if (scale.equals("large")) {
            acceleration = 25 + rnd.nextInt(25);
            this.range = 50 + rnd.nextInt(25);
        }
        if (scale.equals("huge")) {
            acceleration = 100 + rnd.nextInt(200);
            this.range = 10 + rnd.nextInt(40);
        }

        super.getShape().setFill(Color.rgb(rnd.nextInt(55) + 200, rnd.nextInt(105) + 100, rnd.nextInt(100) + 50));
        super.getShape().setRotate(rnd.nextInt(360));

        for (int i = 0; i < acceleration; i++) {
            super.accelerate();
        }

    }

    public void erode() {
        this.range--;
        if (this.range < 0) {
            this.setAlive(false);
        } else {
            if (this.range <= 10) {
                double newscale = this.range * 0.1;
                this.getShape().setScaleX(newscale);
                this.getShape().setScaleY(newscale);
            }
        }
    }

    public int getRange() {
        return range;
    }

    public int getSpawnDelay() {
        return spawnDelay;
    }

    public void setSpawnDelay(int spawnDelay) {
        this.spawnDelay = spawnDelay;
    }

    public void waitForDelayedSpawn() {
        this.spawnDelay--;
    }

    public Spark createClone() {
        Spark clone = new Spark((int) (this.getShape().getTranslateX()), (int) (this.getShape().getTranslateY()), this.scale);
        clone.setShape(this.getShape());             // commenting out this line makes a different effect (random rotation on new sparks)
        clone.setVelocity(this.getVelocity());
        clone.range = this.range;
        clone.spawnDelay = this.spawnDelay;
        return clone;
    }
}
