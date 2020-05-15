package asteroids;

import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class ShipChunk extends Entity {

    private double Rotation;

    public ShipChunk(double x, double y) {
        super(new Polygon(-3, -3, 3, -3, 0, 12), (int) x, (int) y);
        Random rnd = new Random();
        super.getShape().setFill(Color.rgb(0, 100, 255));
        super.getShape().setRotate(rnd.nextInt(360));
        super.getShape().setScaleX(1.4);
        super.getShape().setScaleY(1.4);

        int Acceleration = 50 + rnd.nextInt(50);
        for (int i = 0; i < Acceleration; i++) {
            accelerate();
        }
        this.Rotation = 0.5 - rnd.nextDouble();
        this.Rotation = this.Rotation * 50;
    }

    public void move() {
        super.move();
        super.getShape().setRotate(super.getShape().getRotate() + Rotation);
    }
}
