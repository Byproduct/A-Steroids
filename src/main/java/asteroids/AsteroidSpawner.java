package asteroids;

import java.util.List;
import java.util.Random;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import static asteroids.MainProgram.screenHeight;
import static asteroids.MainProgram.screenWidth;

class AsteroidSpawner {

    Spaceship ship = Globals.getInstance().getShip();
    Random rnd = new Random();

    public AsteroidSpawner() {
    }

    public void spawn(int x, int y, String scale, Color givenColor) {
        List<Asteroid> queuedAsteroids = Globals.getInstance().getQueuedAsteroids();
        Polygon asteroidPolygon = createPolygon(scale);
        Asteroid newAsteroid = new Asteroid(x, y, asteroidPolygon, scale, Color.rgb(0, 0, 0));

        // if given color is 0, randomize a new color
        if (givenColor.equals(Color.rgb(0, 0, 0))) {
            int brightness = rnd.nextInt(100);
            int red = 255 - brightness - 10;
            int green = 255 - brightness - 20;
            int blue = 255 - brightness - 30;
            Color newColor = Color.rgb(red, green, blue);
            newAsteroid.getShape().setFill(newColor);
            newAsteroid.setOriginalColor(Color.rgb(red, green, blue));
        }

        // if given color wasn't 0, inherit the color (big roid spawns smaller roids of same color)
        if (!(givenColor.equals(Color.rgb(0, 0, 0)))) {
            newAsteroid.getShape().setFill(givenColor);
            newAsteroid.setOriginalColor(givenColor);
        }

        // to-do: convert for-loops into accelerate(int) - just need to figure out the right values
        if (scale.equals("mega")) {     // not used yet
            newAsteroid.setHealth(200);
            newAsteroid.accelerate(rnd.nextInt(2) + 1);
            newAsteroid.setRotation(0.5 - rnd.nextDouble());
            newAsteroid.setRotation(newAsteroid.getRotation() * 0.5);
        }
        if (scale.equals("huge")) {
            newAsteroid.setHealth(150);
            newAsteroid.accelerate(rnd.nextInt(4) + 1);
            newAsteroid.setRotation(0.5 - rnd.nextDouble());
            newAsteroid.setRotation(newAsteroid.getRotation() * 0.8);
        }
        if (scale.equals("large")) {
            newAsteroid.setHealth(25);
            newAsteroid.accelerate(rnd.nextInt(8) + 2);
            newAsteroid.setRotation(0.5 - rnd.nextDouble());
        }
        if (scale.equals("medium")) {
            newAsteroid.setHealth(7);
            newAsteroid.accelerate(rnd.nextInt(15) + 5);
            newAsteroid.setRotation(0.5 - rnd.nextDouble());
            newAsteroid.setRotation(newAsteroid.getRotation() * 10);
        }
        if (scale.equals("small")) {
            newAsteroid.setHealth(1);
            newAsteroid.accelerate(rnd.nextInt(20) + 20);
            newAsteroid.setRotation(0.5 - rnd.nextDouble());
            newAsteroid.setRotation(newAsteroid.getRotation() * 30);
        }

        queuedAsteroids.add(newAsteroid);
    }

    public void spawnStartingRoids(int n, String scale, int distanceMin, int distanceMax) {
        int shipLocationX = screenWidth / 2;
        int shipLocationY = screenHeight / 2;

        for (int i = 0; i < n; i++) {
            int randomDistance = rnd.nextInt(distanceMax - distanceMin) + distanceMin;
            int randomAngle = rnd.nextInt(360);
            double dX = Math.cos(Math.toRadians(randomAngle));
            double dY = Math.sin(Math.toRadians(randomAngle));
            int spawnLocationX = (int) (shipLocationX + dX * randomDistance);
            int spawnLocationY = (int) (shipLocationY + dY * randomDistance);

            this.spawn(spawnLocationX, spawnLocationY, scale, Color.rgb(0, 0, 0));
        }
    }

    public Polygon createPolygon(String scale) {
        Random rnd = new Random();
        double size = 1;
        if (scale.equals("small")) {
            size = 10 + rnd.nextInt(5);
        }
        if (scale.equals("medium")) {
            size = 20 + rnd.nextInt(10);
        }
        if (scale.equals("large")) {
            size = 50 + rnd.nextInt(20);
        }
        if (scale.equals("huge")) {
            size = 200 + rnd.nextInt(20);
        }
        if (scale.equals("mega")) {
            // size = ??? + rnd.nextInt(???);      // not used yet
        }

        Polygon asteroidPolygon = new Polygon();   // points of a pentagon (c) internet
        double c1 = Math.cos(Math.PI * 2 / 5);
        double c2 = Math.cos(Math.PI / 5);
        double s1 = Math.sin(Math.PI * 2 / 5);
        double s2 = Math.sin(Math.PI * 4 / 5);

        asteroidPolygon.getPoints().addAll(
                size, 0.0,
                size * c1, -1 * size * s1,
                -1 * size * c2, -1 * size * s2,
                -1 * size * c2, size * s2,
                size * c1, size * s1);

        // move the points around randomly so as to make the roids less identical. Too much variance makes ugly roids though.
        for (int i = 0; i < asteroidPolygon.getPoints().size(); i++) {
            double variance = rnd.nextInt((int) (size / 3)) - size / 6;
            asteroidPolygon.getPoints().set(i, asteroidPolygon.getPoints().get(i) + variance);
        }

        return asteroidPolygon;
    }

}
