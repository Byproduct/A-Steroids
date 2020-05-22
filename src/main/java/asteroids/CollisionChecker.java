package asteroids;

import java.util.List;
import java.util.concurrent.CountDownLatch;

public class CollisionChecker implements Runnable {

    List<Asteroid> asteroids = Globals.getInstance().getAsteroids();
    private Laser laser;
    CountDownLatch latch;

    public CollisionChecker(CountDownLatch countdownlatch, Laser laser) {
        this.laser = laser;
        this.latch = countdownlatch;
    }

    @Override
    public void run() {

        this.asteroids.forEach(asteroid -> {
            if ((asteroid.isAlive()) && (this.laser.isAlive())) {
                if (this.laser.collideCoarse(asteroid)) {
                    this.laser.setCollided(true);
                    this.laser.setCollidedTarget(asteroid);
                }
            }
        });
        this.latch.countDown();
    }
}
