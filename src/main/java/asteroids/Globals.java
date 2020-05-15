// Singleton class containing stuff that is accessed all over the place
package asteroids;

import static asteroids.MainProgram.screenHeight;
import static asteroids.MainProgram.screenWidth;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.layout.Pane;

public class Globals {

    private static Globals instance;

    private AudioPlayer audioplayer = new AudioPlayer();
    private List<Asteroid> asteroids = new ArrayList<>();
    private List<Asteroid> queuedAsteroids = new ArrayList<>();
    private Pane pane = new Pane();
    private PointsCounter points = new PointsCounter(); 
    private Spaceship ship;
    private List<Spark> sparks = new ArrayList<>();
    private List<Spark> queuedSparks = new ArrayList<>();
    private boolean escapePressed;

    private Globals() {
        this.ship = new Spaceship(screenWidth / 2, screenHeight / 2);
        this.escapePressed = false;
    }

    public static Globals getInstance() {
        if (instance == null) {
            instance = new Globals();
        }
        return instance;
    }
    
    public void setEsc() {
        this.escapePressed = true;
    }
    
    public boolean esc() {
        return escapePressed;
    }

    public List<Asteroid> getAsteroids() {
        return asteroids;
    }

    public List<Asteroid> getQueuedAsteroids() {
        return queuedAsteroids;
    }

    public AudioPlayer getAudioplayer() {
        return audioplayer;
    }

    public Pane getPane() {
        return pane;
    }

    public PointsCounter getPoints() {
        return points;
    }
    
    public Spaceship getShip() {
        return ship;
    }

    public List<Spark> getSparks() {
        return sparks;
    }

    public List<Spark> getQueuedSparks() {
        return queuedSparks;
    }

}
