package asteroids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;

//to-do: spark creation is still slow - "maybe" should create them in a batch instead of one at a time
//to-do: try threading: run a few "spark creator" threads, wait for all to complete, collect the lists and copy to sparkQueue?
//to-do: add a global setting for number of particles (e.g. low/medium/high)
public class AsteroidDamage {

    public AsteroidDamage() {
    }

    public void process(AsteroidSpawner asteroidspawner) {
        Spaceship ship = Globals.getInstance().getShip();
        List<Asteroid> asteroids = Globals.getInstance().getAsteroids();
        List<Asteroid> asteroidAddQueue = Globals.getInstance().getQueuedAsteroids();
        AudioPlayer audioplayer = Globals.getInstance().getAudioplayer();
        PointsCounter points = Globals.getInstance().getPoints();
        SparkSpawner sparkspawner = Globals.getInstance().getSparkspawner();

        Random rnd = new Random();

        ship.getShots().forEach(shot -> {
            asteroids.forEach(asteroid -> {
                if ((asteroid.isAlive()) && (shot.isAlive())) {
                    if (shot.collide(asteroid)) {
                        shot.setAlive(false);
                        if (shot instanceof Laser_homing) {
                            shot.getTarget().setTargeted(false);
                        }
                        asteroid.setTargeted(false);
                        asteroid.takeDamage();

                        if (asteroid.getHealth() <= 0) {
                            asteroid.setAlive(false);

                            if (asteroid.getScale().equals("huge")) {
                                audioplayer.playSound("xplo_2");
                                points.addPoints(0, 0, 0, 1, 0);
                                ArrayList<Integer> newChunksX = new ArrayList<>();
                                ArrayList<Integer> newChunksY = new ArrayList<>();

                                for (int i = 0; i < 20; i++) {
                                    int newChunkX = (int) (asteroid.getShape().getTranslateX() + rnd.nextInt(300) - 150);   // large and medium roids spawn around the ex-roid area, not just the center
                                    int newChunkY = (int) (asteroid.getShape().getTranslateY() + rnd.nextInt(300) - 150);

                                    asteroidspawner.spawn(newChunkX, newChunkY, "large", asteroid.getOriginalColor());

                                    newChunksX.add(newChunkX);                        // for large roids, sparks are created at each split new roid instead of only impact location
                                    newChunksY.add(newChunkY);                        // collecting the split roid locations in a list
                                }
                                for (int k = 0; k < newChunksX.size(); k++) {
                                    for (int j = 0; j < 50; j++) {
                                        sparkspawner.spawn(newChunksX.get(k) + rnd.nextInt(100) - 50, newChunksY.get(k) + rnd.nextInt(100) - 50, "large");
//                                    spark.setSpawnDelay(k * 3);
                                    }
                                }
                                for (int n = 0; n < 300; n++) {                   // additional sparkplosions inside the asteroid area.
                                    sparkspawner.spawn((int) (asteroid.getShape().getTranslateX()) + rnd.nextInt(300) - 150, (int) (asteroid.getShape().getTranslateY()) + rnd.nextInt(300) - 150, "huge");
//                                spark.setSpawnDelay(2 + rnd.nextInt(15));
                                }
                                for (int n = 0; n < 600; n++) {                  // additional sparkplosion at the heart of the ex-asteroid.
                                    sparkspawner.spawn((int) (asteroid.getShape().getTranslateX()), (int) (asteroid.getShape().getTranslateY()), "huge");
//                                spark.setSpawnDelay(2 + rnd.nextInt(15));
                                }
                                for (int n = 0; n < 900; n++) {                  // additional sparkplosion at the impact location.
                                    sparkspawner.spawn((int) (shot.getShape().getTranslateX()), (int) (shot.getShape().getTranslateY()), "huge");
//                                spark.setSpawnDelay(2 + rnd.nextInt(15));
                                }
                            }
                            if (asteroid.getScale().equals("large")) {
                                audioplayer.playSound("xplo_3");
                                points.addPoints(0, 0, 1, 0, 0);
                                for (int i = 0; i < 8; i++) {
                                    int newChunkX = (int) (asteroid.getShape().getTranslateX() + rnd.nextInt(100) - 50);   // large and medium roids spawn around the ex-roid area, not just the center
                                    int newChunkY = (int) (asteroid.getShape().getTranslateY() + rnd.nextInt(100) - 50);
                                    asteroidspawner.spawn(newChunkX, newChunkY, "medium", asteroid.getOriginalColor());
                                }
                                for (int k = 0; k < 4; k++) {
                                    int sparkPointX = (int) (asteroid.getShape().getTranslateX() + rnd.nextInt(80) - 40);
                                    int sparkPointY = (int) (asteroid.getShape().getTranslateY() + rnd.nextInt(80) - 40);
                                    for (int i = 0; i < 25; i++) {
                                        sparkspawner.spawn((int) (shot.getShape().getTranslateX()), (int) (shot.getShape().getTranslateY()), "large");
                                        sparkspawner.spawn(sparkPointX, sparkPointY, "large");
                                        // spawnDelay still not implemented
//                                    Spark spark = new Spark((int) (shot.getShape().getTranslateX()), (int) (shot.getShape().getTranslateY()), "large");   // four sequential sets of sparks at the bullet point
//                                    spark.setSpawnDelay(10 * k);
//                                    Spark spark2 = new Spark(sparkPointX, sparkPointY, "large");                                                            // and another four around the ex-roid area
//                                    spark2.setSpawnDelay(10 * k);
                                    }
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
                }}
            );
        }
        );
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
