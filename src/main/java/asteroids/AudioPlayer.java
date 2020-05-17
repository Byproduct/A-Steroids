package asteroids;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class AudioPlayer extends Thread {

    private HashMap<String, AudioClip> audiofiles;
    private Random rnd;
    private ArrayList<String> audioQueue;
    private boolean music;
    MediaPlayer backgroundMusic;                              // must be declared here or garbage collector will kill it

    public AudioPlayer() {
        this.audiofiles = new HashMap<>();
        this.audioQueue = new ArrayList<>();
        this.rnd = new Random();
        this.music = false;
    }

    public void loadAudioAssets() throws InterruptedException {
        this.loadFile("xplo_1", "xplo_1.mp3", 1, 5);
        this.loadFile("xplo_2", "xplo_2.mp3", 0.9, 4);
        this.loadFile("xplo_3", "xplo_3.mp3", 0.9, 3);
        this.loadFile("xplo_4", "xplo_4.mp3", 1, 2);
        this.loadFile("xplo_5", "xplo_5.mp3", 1, 1);
        this.loadFile("hit", "hit.mp3", 1, 1);
        this.loadFile("recordscratch", "recordscratch.wav", 1, 10);
        this.loadFile("wilhelmscream", "wilhelmscream.wav", 0.8, 10);
        // thrust and gun sounds are in a separate audioplayer in the Spaceship class so that they're (hopefully) in a different thread.

        Media spacethings = new Media(Paths.get("stuff/msx/spacethings.mp3").toUri().toString());  // "media" is better for longer audio
        this.backgroundMusic = new MediaPlayer(spacethings);
        backgroundMusic.setVolume(0.4);
        music = false;
    }

    public void run() { // threaded, check every 20ms
        while (true) {
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
//                System.out.println("Audio thread.sleep error?");
                return;
            }

            while (!this.audioQueue.isEmpty()) {
                try {
                    this.audiofiles.get(this.audioQueue.get(0)).play();
                    this.audioQueue.remove(0);
                } catch (Exception e) {
//                    System.out.println("Audio thread error?");
                }
            }
        }

    }

    public void loadFile(String id, String filename) {
        AudioClip clip = new AudioClip("file:stuff/sfx/" + filename);
        clip.setPriority(0);
        this.audiofiles.put(id, clip);
    }

    public void loadFile(String id, String filename, double volume, int priority) {
        AudioClip clip = new AudioClip("file:stuff/sfx/" + filename);
        clip.setVolume(volume);
        clip.setPriority(priority);
        this.audiofiles.put(id, clip);
    }

    public void playSound(String id) {
        // to-do: sound variation in a less ridiculous way
        if (id.equals("xplo_5")) {
            this.audiofiles.get(id).setRate(this.rnd.nextFloat() * 0.6 + 0.7);
        }
        if (id.equals("xplo_4")) {
            this.audiofiles.get(id).setRate(this.rnd.nextFloat() * 0.2 + 0.9);
        }
        if (id.equals("gun")) {
            this.audiofiles.get(id).setRate(this.rnd.nextFloat() * 0.6 + 0.7);
        }
        if (id.equals("hit")) {
            this.audiofiles.get(id).setRate(this.rnd.nextFloat() * 0.5 + 0.5);
        }
        this.audioQueue.add(id);
    }

    public void playIfNew(String id) {
        if (!audiofiles.get(id).isPlaying()) {
            audiofiles.get(id).play();
        }
    }

    public void stopSound(String id) {
        this.audiofiles.get(id).stop();
    }

    public void stopAll() {
        for (String id : this.audiofiles.keySet()) {
            this.audiofiles.get(id).stop();
        }
    }

    public boolean test() {
        return this.isAlive();
    }

    public void stopMusic() {
        this.backgroundMusic.stop();
    }

    public void toggleMusic() {
        if (this.music == false) {
            this.backgroundMusic.play();
            music = true;
        } else {
            this.backgroundMusic.stop();
            music = false;
        }
    }
}
