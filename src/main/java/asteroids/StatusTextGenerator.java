package asteroids;

import java.util.List;

public class StatusTextGenerator {
// to-do: some of this stuff doesn't need updating every frame

    double fps;
    String fpsString;
    int framecounter;
    long[] frameLengths;

    public StatusTextGenerator() {
        this.fps = 0;
        this.fpsString = "00";
        this.framecounter = 0;
        this.frameLengths = new long[20];             //60
    }

    public String generate(long frameTime, long prevFrameTime) {
        this.framecounter++;
        if (this.framecounter == 20) {               //60
            framecounter = 0;
        }
        long frameLength = frameTime - prevFrameTime;
        frameLengths[framecounter] = frameLength;

        long totalTime = 0;
        for (long frame : frameLengths) {
            totalTime += frame;
        }
        if (totalTime != 0) {
            double seconds = totalTime * 1.0 / 1000;
            this.fps = (int) (20 / seconds);
            this.fpsString = String.format("%02d", (int) (fps));
        }
        Spaceship ship = Globals.getInstance().getShip();
        
        if (this.fps > 52) {
            ship.shoot();
        }
        
        List<Asteroid> asteroids = Globals.getInstance().getAsteroids();
        List<Trail> trails = Globals.getInstance().getShip().getTrails();
        List<Spark> sparks = Globals.getInstance().getSparks();
        PointsCounter points = Globals.getInstance().getPoints();

        String lasers_formatted = String.format("%02d", ship.getShots().size());

        int sparksAlive = 0;
        for (Spark spark : sparks) {
            if (spark.isAlive()) {
                sparksAlive++;
            }
        }
        String sparks_alive_formatted = String.format("%04d", sparksAlive);
        String sparks_total_formatted = String.format("%04d", sparks.size());

        String text = "fps: " + fpsString + "         " + "  Roids alive: " + asteroids.size() + " " + roidList(asteroids) + "         " + "Roids murdered: " + " " + points.toString() + "         " + "Lasers: " + lasers_formatted + "         "
                + "Sparks: " + sparks_alive_formatted + "/" + sparks_total_formatted + "         " + "X: " + (int) (ship.getWorldX()) + "  Y: " + (int) (ship.getWorldY()) + "         " + "Thrust: ";

        for (int i = 0; i < trails.size(); i += 10) {
            text = text + "|";
        }
        return text;
    }

    public String roidList(List<Asteroid> asteroids) {
        int small = 0;
        int medium = 0;
        int large = 0;
        int huge = 0;
        int mega = 0;

        for (int i = 0; i < asteroids.size(); i++) {
            if (asteroids.get(i).getScale().equals("small")) {
                small++;
            }
            if (asteroids.get(i).getScale().equals("medium")) {
                medium++;
            }
            if (asteroids.get(i).getScale().equals("large")) {
                large++;
            }
            if (asteroids.get(i).getScale().equals("huge")) {
                huge++;
            }
            if (asteroids.get(i).getScale().equals("mega")) {
                mega++;
            }
        }
        return "(" + small + "/" + medium + "/" + large + "/" + huge + "/" + mega + ")";
    }
}
