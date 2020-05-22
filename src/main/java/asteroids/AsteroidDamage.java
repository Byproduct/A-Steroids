package asteroids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.scene.paint.Color;

//to-do: spark creation is still slow - "maybe" should create them in a batch instead of one at a time
//to-do: try threading: run a few "spark creator" threads, wait for all to complete, collect the lists and copy to sparkQueue?
//to-do: add a global setting for number of particles (e.g. low/medium/high)
public class AsteroidDamage {

    public AsteroidDamage() {
    }

    public void process() {
        Spaceship ship = Globals.getInstance().getShip();             // give these to class instead of initializing every frame? Not sure if matters
        AsteroidSpawner asteroidspawner = new AsteroidSpawner();
        List<Asteroid> asteroids = Globals.getInstance().getAsteroids();
        List<Asteroid> asteroidAddQueue = Globals.getInstance().getQueuedAsteroids();
        AudioPlayer audioplayer = Globals.getInstance().getAudioplayer();
        List<DelayedExplosion> delayedExplosions = Globals.getInstance().getDelayedExplosions();
        PointsCounter points = Globals.getInstance().getPoints();
        SparkSpawner sparkspawner = Globals.getInstance().getSparkspawner();

        Random rnd = new Random();

        // multithreading for collision checker.
        CountDownLatch latch = new CountDownLatch(ship.getShots().size());
        ArrayList<Thread> threads = new ArrayList<>();
        ArrayList<CollisionChecker> collisionCheckers = new ArrayList<>();

        for (int i = 0; i < ship.getShots().size(); i++) {
            CollisionChecker CC = new CollisionChecker(latch, ship.getShots().get(i));
            collisionCheckers.add(CC);
        }
        for (CollisionChecker CC : collisionCheckers) {
            Thread thread = new Thread(CC);
            threads.add(thread);
        }
        for (Thread thread : threads) {
            thread.start();
        }
        try {
            latch.await();
        } catch (InterruptedException ex) {
            Logger.getLogger(AsteroidDamage.class.getName()).log(Level.SEVERE, null, ex);
        }
        // end  (any collided shots now have a collided == true value, and are then checked with the old singlecore method)

        ship.getShots().forEach(shot -> {
            if (shot.isCollided()) {
                shot.setCollided(false);
                Asteroid asteroid = shot.getCollidedTarget();
                if (asteroid.isAlive()) {
                    if (shot.collideFine(shot.getCollidedTarget())) {                    // to-do: (only) if it doesn't hit, check all other roids.
                        shot.setAlive(false);
                        if (shot instanceof Laser_homing) {
                            shot.getTarget().setTargeted(false);
                        }
                        asteroid.takeDamage();

                        if (asteroid.getHealth() <= 0) {
                            asteroid.setTargeted(false);
                            asteroid.setAlive(false);

                            if (asteroid.getScale().equals("huge")) {
                                audioplayer.playSound("xplo_2");
                                points.addPoints(0, 0, 0, 1, 0);
                                ArrayList<Integer> newChunksX = new ArrayList<>();
                                ArrayList<Integer> newChunksY = new ArrayList<>();

                                for (int i = 0; i < 20; i++) {
                                    int newChunkX = (int) (asteroid.getShape().getTranslateX() + rnd.nextInt(300) - 150);   // large and medium split roids spawn around the ex-roid area, not just the center
                                    int newChunkY = (int) (asteroid.getShape().getTranslateY() + rnd.nextInt(300) - 150);
                                    asteroidspawner.spawn(newChunkX, newChunkY, "large", asteroid.getOriginalColor());
                                    newChunksX.add(newChunkX);                        // for large roids, sparks are created at each split new roid instead of only impact location
                                    newChunksY.add(newChunkY);                        // collecting the split roid locations in a list
                                }
                                for (int k = 0; k < newChunksX.size(); k++) {
                                    delayedExplosions.add(new DelayedExplosion(newChunksX.get(k), newChunksY.get(k), "largeRoidSplit", k * 3));
                                    delayedExplosions.add(new DelayedExplosion(newChunksX.get(k), newChunksY.get(k), "largeRoidSplit", k * 3 + 10));
                                }
                                delayedExplosions.add(new DelayedExplosion(asteroid.getShape().getTranslateX(), asteroid.getShape().getTranslateY(), "hugeRoidSplit1", 20));   // additional delayed sparkplosion inside the asteroid area. (+random)
                                delayedExplosions.add(new DelayedExplosion(asteroid.getShape().getTranslateX(), asteroid.getShape().getTranslateY(), "hugeRoidSplit2", 5));    // additional sparkplosion at the heart of the ex-asteroid.
                                delayedExplosions.add(new DelayedExplosion(shot.getShape().getTranslateX(), shot.getShape().getTranslateY(), "hugeRoidSplit2", 3));            // additional sparkplosion at the impact location.
                                delayedExplosions.add(new DelayedExplosion(shot.getShape().getTranslateX(), shot.getShape().getTranslateY(), "hugeRoidSplit2", 10));           // additional delayed sparkplosion at the impact location.
                            }
                            if (asteroid.getScale().equals("large")) {
                                audioplayer.playSound("xplo_3");
                                points.addPoints(0, 0, 1, 0, 0);
                                for (int i = 0; i < 8; i++) {
                                    int newChunkX = (int) (asteroid.getShape().getTranslateX() + rnd.nextInt(100) - 50);   // large and medium roids spawn around the ex-roid area, not just the center
                                    int newChunkY = (int) (asteroid.getShape().getTranslateY() + rnd.nextInt(100) - 50);
                                    asteroidspawner.spawn(newChunkX, newChunkY, "medium", asteroid.getOriginalColor());
                                }
                                for (int i = 0; i < 6; i++) {
                                    delayedExplosions.add(new DelayedExplosion(shot.getShape().getTranslateX(), shot.getShape().getTranslateY(), "largeRoidSplit", 5 * i));                                                       // five sequential sets of sparks at the bullet point
                                    delayedExplosions.add(new DelayedExplosion(asteroid.getShape().getTranslateX() + rnd.nextInt(80) - 40, asteroid.getShape().getTranslateY() + rnd.nextInt(80) - 40, "largeRoidSplit", i * 5));     // and another five around the ex-roid area
                                }
                            }
                            if (asteroid.getScale().equals("medium")) {
                                audioplayer.playSound("xplo_4");
                                points.addPoints(0, 1, 0, 0, 0);
                                for (int i = 0; i < 10; i++) {
                                    asteroidspawner.spawn((int) asteroid.getShape().getTranslateX(), (int) asteroid.getShape().getTranslateY(), "small", asteroid.getOriginalColor());
                                }
                                for (int i = 0; i < 50; i++) {
                                    sparkspawner.spawn((int) (shot.getShape().getTranslateX()), (int) (shot.getShape().getTranslateY()), "medium");
                                }
                            }
                            if (asteroid.getScale().equals("small")) {
                                audioplayer.playSound("xplo_5");
                                points.addPoints(1, 0, 0, 0, 0);
                                for (int i = 0; i < 30; i++) {
                                    sparkspawner.spawn((int) (shot.getShape().getTranslateX()), (int) (shot.getShape().getTranslateY()), "small");
                                }
                            }
                        }  // destroying part over

                        //       if asteroid is hit but still lives
                        if (asteroid.getHealth() >= 1) {
                            audioplayer.playSound("hit");
                            asteroid.setFlashing(true);
                            asteroid.setFlashFrame(0);

                            for (int i = 0; i < 15; i++) {
                                sparkspawner.spawn((int) (shot.getShape().getTranslateX()), (int) (shot.getShape().getTranslateY()), "small");
                            }
                        }
                    }
                }
            }
        });
    }

    public void flashDamaged(List<Asteroid> asteroids) {
        asteroids.stream().filter((asteroid) -> (asteroid.isFlashing())).forEach((asteroid) -> {
            asteroid.increaseFlashFrame();
            if (asteroid.getFlashFrame() == 3) {
                asteroid.setFlashFrame(6);
            }
            double originalRed = asteroid.getOriginalColor().getRed();
            double originalGreen = asteroid.getOriginalColor().getGreen();
            double originalBlue = asteroid.getOriginalColor().getBlue();
            double flashingRed = 1 - ((1 - originalRed) * 0.05 * asteroid.getFlashFrame());
            double flashingGreen = 0 + originalGreen * 0.05 * asteroid.getFlashFrame();
            double flashingBlue = 0 + originalBlue * 0.05 * asteroid.getFlashFrame();
            if (asteroid.getFlashFrame() <= 20) {
                asteroid.setColor(Color.color(flashingRed, flashingGreen, flashingBlue));
            }
            if (asteroid.getFlashFrame() > 20) {
                asteroid.setColor(asteroid.getOriginalColor());
                asteroid.setFlashing(false);
            }
        });
    }
}
