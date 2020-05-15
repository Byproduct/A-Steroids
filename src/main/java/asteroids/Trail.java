package asteroids;

import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Trail extends Entity {

    private int range;

    public Trail(int x, int y) {
        super(new Polygon(
                -6, -4,
                -6, 4,
                6, 4,
                6, -4),
                x, y);

        Random rnd = new Random();
        this.range = 25;
        this.getShape().setFill(Color.rgb(255, rnd.nextInt(200), rnd.nextInt(50)));
    }

    public void erode() {
        this.range--;
        if (this.range < 0) {
            this.setAlive(false);
        } else {
            double newscale = this.range * 0.1;
            if (newscale > 1.7) {
                newscale = 1.7;
            }
            this.getShape().setScaleX(newscale);
            this.getShape().setScaleY(newscale);
        }
    }
}
