// Author: https://soundcloud.com/byproduct
// newbie alert: if any of this code is weird, it's because this is my first Java program beyond course exercises.

// Pull requests? Please be beginner-friendly (e.g. comments, readability). This is a learning project – I must understand the code if I'm to add it.

package asteroids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class MainProgram extends Application {

    // to-do: fix fullscreen on > fullhd monitors
    public static int screenWidth = 1920;
    public static int screenHeight = 1080;
    public static int bottomDivider = 1030;
    public boolean music;

    @Override
    public void start(Stage stage) throws Exception {

        // Load assets & initialize 
        AudioPlayer audioplayer = Globals.getInstance().getAudioplayer();
        audioplayer.loadAudioAssets();
        audioplayer.start();

        Random rnd = new Random();

        // audioplayer.playMusic();                                   // key M toggles music
        KeyboardHandler keyboardhandler = new KeyboardHandler();
        Pane pane = Globals.getInstance().getPane();                  // all graphics go here

        pane.setPrefSize(screenWidth, screenHeight);

        Image background = new Image("file:stuff/gfx/space.jpg");
        pane.getChildren().add(new ImageView(background));

        Polygon statusBar = new Polygon(0, bottomDivider, 0, bottomDivider + 50, screenWidth, bottomDivider + 50, screenWidth, bottomDivider);
        statusBar.setFill(Color.rgb(200, 200, 200));
        pane.getChildren().add(statusBar);

        StatusTextGenerator statustext = new StatusTextGenerator();
        Text statusbarText = new Text(10, screenHeight - 20, "Status bar text goes here.");
        pane.getChildren().add(statusbarText);
        statusbarText.setFont(Font.font("Verdana", FontWeight.BOLD, 15));
        PointsCounter points = new PointsCounter();

        Spaceship ship = Globals.getInstance().getShip();
        ship.startAudioThread();
        pane.getChildren().add(ship.getShape());

        List<Asteroid> asteroids = Globals.getInstance().getAsteroids();
        List<Asteroid> QueuedAsteroids = Globals.getInstance().getQueuedAsteroids();
        List<Spark> sparks = Globals.getInstance().getSparks();
        List<Spark> queuedSparks = Globals.getInstance().getQueuedSparks();
        List<ShipChunk> shipChunks = new ArrayList<>();
        AsteroidDamage asteroidDamageFX = new AsteroidDamage();

        // spawn these asteroids at game start. Number, size (small/medium/large/huge/mega), minimum and maximum distance from player              
        AsteroidSpawner asteroidspawner = new AsteroidSpawner();
        asteroidspawner.spawnStartingRoids(100, "medium", 500, 800);
        asteroidspawner.spawnStartingRoids(300, "large", 1000, 1050);
        asteroidspawner.spawnStartingRoids(50, "huge", 2000, 4000);
        asteroids.forEach(asteroid -> pane.getChildren().add(asteroid.getShape()));

        Scene scene = new Scene(pane);
        stage.setTitle("A Steroids");
        stage.setScene(scene);
        stage.show();

        scene.setOnKeyPressed(event -> {
            keyboardhandler.keyPress(event.getCode());
        });

        scene.setOnKeyReleased(event -> {
            keyboardhandler.keyRelease(event.getCode());
        });

        new AnimationTimer() {

            int frame = 0;
            long frameTime = 0;
            long prevFrameTime = 0;
            int doLessOften = 0;

            @Override
            public void handle(long now) {

                statusbarText.setText(statustext.generate(frameTime, prevFrameTime));
                statusBar.toFront();
                statusbarText.toFront();

                if (ship.isAlive()) {
                    keyboardhandler.handle();
                }

                ship.move();
                ship.cooldowns();

                asteroids.forEach(asteroid -> asteroid.move());
                removeDead(asteroids);

                asteroidDamageFX.process(asteroidspawner);              // things to do when roids get hit
                asteroidDamageFX.flashDamaged(asteroids);

                // check for game over       
                // to-do: a separate method
                // to-do: remove all sparks, trails and shots before pause
                // to-do: add "It was at this moment, player knew..."
                if (ship.isAlive()) {
                    asteroids.forEach(asteroid -> {
                        if (ship.collide(asteroid)) {
                            ship.setAlive(false);
                            audioplayer.stopMusic();
                            audioplayer.stopAll();
                            ship.stopAllSounds();
                            audioplayer.playSound("recordscratch");
                            pause(1700);
                            ship.setVelocity(new Point2D(0, 0));
                            audioplayer.playSound("wilhelmscream");
                            pause(500);
                            for (int i = 0; i < 20; i++) {
                                ShipChunk shipchunk = new ShipChunk(ship.getShape().getTranslateX(), ship.getShape().getTranslateY());
                                shipChunks.add(shipchunk);
                            }
                            shipChunks.forEach(shipchunk -> pane.getChildren().add(shipchunk.getShape()));
                            pane.getChildren().remove(ship.getShape());
                            audioplayer.playSound("xplo_3");
                        }
                    });
                }
                // end of game over sequence

                shipChunks.forEach(shipChunk -> shipChunk.move());

                ship.getShots().forEach(shot -> {
                    if (shot.isAlive()) {
                        shot.move();
                        shot.erode();
                    }
                });
                removeDead(ship.getShots());

                // things to do on every 10th frame instead of each one
                doLessOften++;
                switch (doLessOften) {
                    case 1:
                        removeDead(sparks);
                        break;
                    case 2:
                        removeDeadFromListOnly(queuedSparks);
                        break;
                    case 3:
                        removeDead(ship.getTrails());
                        break;
                    case 10:
                        doLessOften = 0;
                }

                ship.getTrails().forEach(trail -> {
                    trail.erode();
                    trail.move();
                });

                // When new asteroids are spawned, they are added into this queue list, then added to the game from here.
                QueuedAsteroids.forEach(queuedAsteroid -> {
                    asteroids.add(queuedAsteroid);
                    pane.getChildren().add(queuedAsteroid.getShape());
                });
                QueuedAsteroids.clear();

                // New sparks are added in the same way, but with a maximum number added per frame.
                // to-do: this is still slow. For a better method try: .filter spawndelay <= 0, and use some built-in copying shenanigans (Collections.copy, stream?) instead of manual copying. Must actually clone items (not make double reference).
                
                for (Spark queuedSpark : queuedSparks) {
                    if (queuedSpark.isAlive()) {
                        queuedSpark.waitForDelayedSpawn();                   // New sparks can be added with a specified delay. This subtracts the number by 1 each tick. Spawns when 0.
                        if (sparks.size() < 5000) {                         // Max simultaneous sparks
                            if (queuedSpark.getSpawnDelay() < 0) {
                                Spark clone = new Spark();
                                clone = queuedSpark.createClone();
                                sparks.add(clone);
                                pane.getChildren().add(clone.getShape());
                                queuedSpark.setAlive(false);
                            }
                        }
                        if (queuedSpark.getSpawnDelay() < -60) {            // if a spark has stayed in the queue for more than 60 ticks (~1sec) without spawning, just erase it. It'd look weird.
                            queuedSpark.setAlive(false);
                        }
                    }
                }

                sparks.forEach(spark -> {
                    spark.erode();
                    spark.move();
                });

                // All objects move in relation to the ship, which is always at the center coordinates (screenWidth / 2 , screenHeight / 2)  :|
                // Not great, for several reasons (e.g. multiplayer). Couldn't figure out how to display only specific X/Y area of a Pane, though. If it's possible, just remove these lines.
                moveRelative(ship);
                moveRelative(asteroids);
                moveRelative(ship.getShots());
                moveRelative(ship.getTrails());
                moveRelative(sparks);
                moveRelative(queuedSparks);

                // calculate the time it took to process one frame (60fps = 17ms)
                prevFrameTime = frameTime;
                frameTime = System.currentTimeMillis();

                /*
                if ([condition]) {                         //    if the PC is struggling (e.g. low FPS)   
                    queuedSparks.clear();                  //    -> remove all sparks from queue

                    if (sparks.size() > 500) {             //    -> ..and 500 from screen
                        for (int i = 0; i < 500; i++) {    //                   
                            sparks.get(i).setAlive(false);               //to-do: implement good detection, one-frame detection sux
                        }
                    }
                    removeDead(sparks);
                }
                 */
                
                 if (Globals.getInstance().esc() == true) {
                     System.out.println("\n\n\n\n\nYou have fondled the esc button, GG.\n\n\n\n\n");
                     Platform.exit();
                     System.exit(0);
                 }
            }
        }
                .start();
    }

    public void moveRelative(List<? extends Entity> lista) {
        Spaceship ship = Globals.getInstance().getShip();
        for (Entity entity : lista) {
            entity.getShape().setTranslateX(entity.getShape().getTranslateX() - ship.getVelocity().getX());
            entity.getShape().setTranslateY(entity.getShape().getTranslateY() - ship.getVelocity().getY());
        }
    }

    public void moveRelative(Entity entity) {
        Spaceship ship = Globals.getInstance().getShip();
        entity.getShape().setTranslateX(entity.getShape().getTranslateX() - ship.getVelocity().getX());
        entity.getShape().setTranslateY(entity.getShape().getTranslateY() - ship.getVelocity().getY());
    }

    public void removeDead(List<? extends Entity> list) {                       // removes objects with alive == false 
        Pane pane = Globals.getInstance().getPane();
        list.stream()
                .filter(entity -> !entity.isAlive())
                .forEach(entity -> pane.getChildren().remove(entity.getShape()));

        list.removeAll(list.stream()
                .filter(entity -> !entity.isAlive())
                .collect(Collectors.toList()));
    }

    public void removeDeadFromListOnly(List<? extends Entity> list) {          // removes queued objects that are only in a list and not in the main pane
        list.removeAll(list.stream()
                .filter(entity -> !entity.isAlive())
                .collect(Collectors.toList()));
    }

    public void pause(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException ex) {
            Logger.getLogger(MainProgram.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
