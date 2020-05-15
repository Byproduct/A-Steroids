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
        this.frameLengths = new long[60];
    }

    public String generate(long frameTime, long prevFrameTime) {
        this.framecounter++;
        if (this.framecounter == 60) {
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
            this.fps = (int) (60 / seconds);
            this.fpsString = String.format("%02d", (int) (fps));
        }
        Spaceship ship = Globals.getInstance().getShip();
        List<Asteroid> asteroids = Globals.getInstance().getAsteroids();
        List<Trail> trails = Globals.getInstance().getShip().getTrails();
        List<Spark> sparks = Globals.getInstance().getSparks();
        List<Spark> queuedSparks = Globals.getInstance().getQueuedSparks();
        PointsCounter points = Globals.getInstance().getPoints();

        String lasers_formatted = String.format("%02d", ship.getShots().size());
        String sparks_formatted = String.format("%04d", sparks.size());


        String text = "fps: " + fpsString + "         " + "  Roids alive: " + asteroids.size() + " " + roidList(asteroids) + "         " + "Roids murdered: " + " " + points.toString() + "         " + "Lasers: " + lasers_formatted + "         " + "Sparks: " + sparks_formatted;
        if (queuedSparks.size() > 0) {
            text = text + "(" + queuedSparks.size() + ")";
        }
        text = text + "         " + "X: " + (int) (ship.getWorldX()) + "  Y: " + (int) (ship.getWorldY()) + "         " + "Thrust: ";
        
//        Identical to above but readable
//        String text = "fps: " + fpsString;
//        text = text + "         ";
//        text = text + "  Roids alive: " + asteroids.size() + " ";
//        text = text + roidList(asteroids);
//        text = text + "         ";
//        text = text + "Roids murdered: " + " " + points.toString();
//        text = text + "         ";
//        text = text + "Lasers: " + lasers_formatted;
//        text = text + "         ";
//        text = text + "Sparks: " + sparks_formatted;
//        if (queuedSparks.size() > 0) {
//            text = text + "(" + queuedSparks.size() + ")";
//        }
//        text = text + "         ";
//        text = text + "X: ";
//        text = text + (int) (ship.getWorldX());
//        text = text + "  Y: ";
//        text = text + (int) (ship.getWorldY());
//        text = text + "         ";
//        text = text + "Thrust: ";
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
