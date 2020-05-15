package asteroids;

import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Asteroid extends Entity {

    private String scale;
    private Color color;
    private Color originalColor;
    private double rotation;
    private Random rnd = new Random();
    private int health;
    private boolean flashing;
    private int flashFrame;
    private boolean targeted;

    // todo: move lots of this to AsteroidSpawner
    public Asteroid(int x, int y, Polygon asteroidPolygon, String scale, Color newColor) {
        super(new Polygon(), x, y);
        super.setShape(asteroidPolygon);
        this.scale = scale;

        super.getShape().setTranslateX(x);
        super.getShape().setTranslateY(y);

        super.getShape().setRotate(rnd.nextInt(360));

        this.flashing = false;
        this.flashFrame = 0;
        this.targeted = false;
    }

    @Override
    public void move() {
        super.move();
        super.getShape().setRotate(super.getShape().getRotate() + rotation);
    }

    public String getScale() {
        return scale;
    }

    public Color getColor() {
        return this.color;
    }

    public void setColor(Color color) {
        this.color = color;
        super.getShape().setFill(this.color);
    }

        public Color getOriginalColor() {
        return this.originalColor;
    }

    public void setOriginalColor(Color originalColor) {
        this.originalColor = originalColor;
    }
    
        public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }

    public boolean isFlashing() {
        return flashing;
    }

    public void setFlashing(boolean flashing) {
        this.flashing = flashing;
    }

    public int getFlashFrame() {
        return flashFrame;
    }

    public void setFlashFrame(int flashFrame) {
        this.flashFrame = flashFrame;
    }

    public void increaseFlashFrame() {
        this.flashFrame++;
    }

    public void takeDamage() {
        this.health--;
    }

    public void takeDamage(int damage) {
        this.health -= damage;
    }

    public boolean isTargeted() {
        return targeted;
    }

    public void setTargeted(boolean targeted) {
        this.targeted = targeted;
    }

    public double getRotation() {
        return rotation;
    }

    public void setRotation(double rotation) {
        this.rotation = rotation;
    }
    
}
