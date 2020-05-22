package asteroids;

import static java.lang.Math.abs;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

public abstract class Entity {

    private Polygon shape;
    private Point2D velocity;
    private boolean alive;
    private int collisionSize;

    public Entity() {
    }

    public Entity(Polygon polygon, int x, int y) {
        this.shape = polygon;
        this.shape.setTranslateX(x);
        this.shape.setTranslateY(y);
        this.alive = true;
        this.velocity = new Point2D(0, 0);

        // should be identical with calculateCollisionSize();
        List<Double> coordinates = this.shape.getPoints();
        this.collisionSize = 0;     // default collision box size. Possible to increase this if correct collisionSize fails to generate somehow.
        try {
            this.collisionSize = (int) (Collections.max(coordinates) * 1.1);   // collision box slightly larger than object 
        } catch (Exception e) {
        }
    }

    public void calculateCollisionSize() {
        // should be copied to constructor

        List<Double> coordinates = this.shape.getPoints();
        this.collisionSize = 0;     // default collision box size. Possible to increase this if correct collisionSize fails to generate somehow.
        try {
            this.collisionSize = (int) (Collections.max(coordinates) * 1.1);   // collision box slightly larger than object 
        } catch (Exception e) {
        }
    }

//    collision check 1st version
//    public boolean collide(Entity anotherEntity) {
//        int distanceX = abs((int) (this.shape.getTranslateX() - anotherEntity.shape.getTranslateX()));
//        int distanceY = abs((int) (this.shape.getTranslateY() - anotherEntity.shape.getTranslateY()));
//
//        int combinedCollisionSize = this.collisionSize + anotherEntity.getCollisionSize();
//
//        if (distanceX < combinedCollisionSize && distanceY < combinedCollisionSize) {
//            Shape collisionArea = Shape.intersect(this.shape, anotherEntity.getShape());
//            return collisionArea.getBoundsInLocal().getWidth() != -1;
//        }
//        return false;
//    }
//    collision check (hopefully) faster 2nd version 
    public boolean collide(Entity anotherEntity) {
        int distanceX = abs((int) (this.shape.getTranslateX() - anotherEntity.shape.getTranslateX()));
        if (distanceX < 500) {
            int distanceY = abs((int) (this.shape.getTranslateY() - anotherEntity.shape.getTranslateY()));
            if (distanceY < 500) {
                int combinedCollisionSize = this.collisionSize + anotherEntity.getCollisionSize();
                if (distanceX < combinedCollisionSize) {
                    if (distanceY < combinedCollisionSize) {
                        Shape collisionArea = Shape.intersect(this.shape, anotherEntity.getShape());
                        return collisionArea.getBoundsInLocal().getWidth() != -1;
                    }
                }
            }
        }
        return false;
    }

    // shot vs asteroid collision now separated into coarse (multicore) and fine (singlecore) checks
    public boolean collideCoarse(Entity anotherEntity) {
        int distanceX = abs((int) (this.shape.getTranslateX() - anotherEntity.shape.getTranslateX()));
        if (distanceX < 500) {
            int distanceY = abs((int) (this.shape.getTranslateY() - anotherEntity.shape.getTranslateY()));
            if (distanceY < 500) {
                int combinedCollisionSize = this.collisionSize + anotherEntity.getCollisionSize();
                if (distanceX < combinedCollisionSize) {
                    if (distanceY < combinedCollisionSize) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean collideFine(Entity anotherEntity) {
        Shape collisionArea = Shape.intersect(this.shape, anotherEntity.getShape());
        return collisionArea.getBoundsInLocal().getWidth() != -1;
    }

    public int getCollisionSize() {
        return collisionSize;
    }

    public void setCollisionSize(int collisionSize) {
        this.collisionSize = collisionSize;
    }

    public void accelerate() {
        double vX = Math.cos(Math.toRadians(this.shape.getRotate()));
        double vY = Math.sin(Math.toRadians(this.shape.getRotate()));

        vX *= 0.05;
        vY *= 0.05;

        this.velocity = this.velocity.add(vX, vY);
    }

    public void accelerate(int times) {
        double vX = Math.cos(Math.toRadians(this.shape.getRotate()));
        double vY = Math.sin(Math.toRadians(this.shape.getRotate()));

        vX *= 0.05 * times;
        vY *= 0.05 * times;

        this.velocity = this.velocity.add(vX, vY);
    }

    public void move() {
        this.shape.setTranslateX(this.shape.getTranslateX() + this.velocity.getX());
        this.shape.setTranslateY(this.shape.getTranslateY() + this.velocity.getY());
    }

    // to-do: turn methods increase angle beyond +-360 degrees. Fix with %360 or something, it's faster to do here than e.g. for every frame for homing shots.
    public void turnLeft() {
        this.shape.setRotate(this.shape.getRotate() - 5);
    }

    public void turnLeft(int times) {
        this.shape.setRotate(this.shape.getRotate() - 5 * times);
    }

    public void turnRight() {
        this.shape.setRotate(this.shape.getRotate() + 5);
    }

    public void turnRight(int times) {
        this.shape.setRotate(this.shape.getRotate() + 5 * times);
    }

    public boolean isAlive() {
        return this.alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Polygon getShape() {
        return this.shape;
    }

    public void setShape(Polygon shape) {
        this.shape = shape;
        calculateCollisionSize();
    }

    public Point2D getVelocity() {
        return this.velocity;
    }

    public void setVelocity(Point2D vel) {
        this.velocity = vel;
    }

}
