package asteroids;

import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;

public class Laser_homing extends Laser {
    // homing shots acquire a target and keep it until the target or the shot is destroyed (no re-scanning continuously)

    private Asteroid target;
    private Point2D targetPoint;

    public Laser_homing(int x, int y) {
        super(x, y);
        this.getShape().setFill(Color.rgb(0, 255, 150));
        this.setRange(99999999);  // or 55
    }

    @Override
    public void move() {
        if ((this.target.isAlive() == false) && (MainProgram.seekerChoke < 10)) {   // seek a new target if the previous target died - however this can cause a spike when targeted roids die - so don't re-acquire new targets for more than 10 shots per frame
            boolean targetFound = this.acquireNewTarget();                      // to-do: crashes with null pointer if no remaining asteroids are found
            MainProgram.seekerChoke++;
        }
        if (this.target != null) {
            this.targetPoint = new Point2D(this.target.getShape().getTranslateX(), this.target.getShape().getTranslateY());
        } else {
            this.targetPoint = new Point2D(0, 0);
        }
        Point2D laserPoint = new Point2D(this.getShape().getTranslateX(), this.getShape().getTranslateY());

        double laserAngle = this.getShape().getRotate() % 360;    // to-do: move the %360 to entity turn methods
        if (laserAngle < 0) {
            laserAngle += 360;
        }
        //
        //        270          degree angles
        // 180 <-  ^  -> 0
        //        90
        //
        double targetAngle = getAngle(targetPoint, laserPoint);
        if (laserAngle < targetAngle) {
            if (Math.abs(laserAngle - targetAngle) < 180) {
                this.turnRight(1);
                if (Math.abs(laserAngle - targetAngle) > 20) {
                    this.turnRight(1);                           // additional turn if the target angle is far - remove for larger turn radius (less effective seeking especially at close range)
                }
            } else {
                this.turnLeft(1);
            }
        }
        if (laserAngle > targetAngle) {
            if (Math.abs(laserAngle - targetAngle) < 180) {
                this.turnLeft(1);
                if (Math.abs(laserAngle - targetAngle) > 20) {
                    this.turnLeft(1);                           // same as above
                }
            } else {
                this.turnRight(1);
            }
        }
        this.setVelocity(new Point2D(0, 0));                    //reset velocity for each frame  (or could just keep accelerating for less effective seeking)
        this.accelerate(250);                  //default 250
        this.getShape().setTranslateX(this.getShape().getTranslateX() + this.getVelocity().getX());
        this.getShape().setTranslateY(this.getShape().getTranslateY() + this.getVelocity().getY());
    }

    public float getAngle(Point2D source, Point2D target) {
        return (float) (Math.toDegrees(Math.atan2(target.getY() - source.getY(), target.getX() - source.getX())) + 180) % 360;     // clockwise angle (atan2 arguments reversed)
    }

    public boolean acquireNewTarget() {               // to-do: crashes if no targetable roids are present
        List<Asteroid> asteroids = Globals.getInstance().getAsteroids();
        Point2D laserPoint = new Point2D(this.getShape().getTranslateX(), this.getShape().getTranslateY());

        double nearestDistance = 9000000;
        double nearestX = 0;
        double nearestY = 0;
        Asteroid nearestAsteroid = null;
        for (Asteroid asteroid : asteroids) {
            double etaisyys = laserPoint.distance(asteroid.getShape().getTranslateX(), asteroid.getShape().getTranslateY());
            if (etaisyys < nearestDistance && asteroid.isTargeted() == false) {
                nearestDistance = etaisyys;
                nearestAsteroid = asteroid;
            }
        }
        this.target = nearestAsteroid;               // to-do: null = crash?
        if (nearestAsteroid != null) {
            if (nearestAsteroid.getScale().equals("small") || nearestAsteroid.getScale().equals("medium")) {     // only one shot locked to small or medium roids, infinite targeting with others
                nearestAsteroid.setTargeted(true);
            }
            return true;
        } else {
            return false;      // return false if there are no asteroids to be murdered
        }
    }

    @Override
    public void erode() {
        super.erode();
        if (this.getRange() < 0) {
            this.target.setTargeted(false);
        }
    }

    public Asteroid getTarget() {
        return target;
    }

    public void setTarget(Asteroid target) {
        this.target = target;
    }

}
