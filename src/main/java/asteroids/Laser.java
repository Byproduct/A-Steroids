package asteroids;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Laser extends Entity {

    private int range;

    public Laser(int x, int y) {
        super(new Polygon(15, -2,
                15, 2,
                -15, 2,
                -15, -2),
                x, y);

        this.range = 35;
        this.getShape().setFill(Color.rgb(200, 00, 255));
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

    public void setRange(int range) {
        this.range = range;
    }
    
    public Asteroid getTarget() {     // subclass Laser_homing has a target
        return null;
    }
}
