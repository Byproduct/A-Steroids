package asteroids;

import java.util.Random;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Polygon;

public class Spark extends Entity {

    private int range;
    private int spawnDelay;
    private String scale;
    Random rnd = new Random();
    Pane pane = Globals.getInstance().getPane();
    
    public Spark(Polygon polygon, int x, int y) {
        super(polygon, x, y);
    }

    public void erode() {
        this.range--;
        if (this.range < 0) {
            this.setAlive(false);
            pane.getChildren().remove(this.getShape());
        } else {
            if (this.range <= 10) {
                double newscale = this.range * 0.1;
                this.getShape().setScaleX(newscale);
                this.getShape().setScaleY(newscale);
            }
        }
    }

    public String getScale() {
        return scale;
    }

    public void setScale(String scale) {
        this.scale = scale;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
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
}
