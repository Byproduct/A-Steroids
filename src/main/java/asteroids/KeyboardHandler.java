package asteroids;

import java.util.HashMap;
import java.util.Map;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;

public class KeyboardHandler {

    private Map<KeyCode, Boolean> pressedKeys;
    AudioPlayer audioplayer;
    Spaceship ship;

    public KeyboardHandler() {
        this.pressedKeys = new HashMap<>();
        this.audioplayer = Globals.getInstance().getAudioplayer();
        this.ship = Globals.getInstance().getShip();
    }

    public void keyPress(KeyCode keycode) {
        this.pressedKeys.put(keycode, Boolean.TRUE);
    }

    public void keyRelease(KeyCode keycode) {
        this.pressedKeys.put(keycode, Boolean.FALSE);
    }

    // to-do: Fix toggle buttons (now toggle every frame -> random result)
    public void handle() {

        if (this.pressedKeys.getOrDefault(KeyCode.LEFT, false)) {
            ship.turnLeft();
        }
        if (this.pressedKeys.getOrDefault(KeyCode.RIGHT, false)) {
            ship.turnRight();
        }
        if (this.pressedKeys.getOrDefault(KeyCode.UP, false)) {
            ship.accelerate();
            ship.pressThrust();
        }
        if (this.pressedKeys.getOrDefault(KeyCode.UP, false) == false) {
            ship.releaseThrust();
        }
        if (this.pressedKeys.getOrDefault(KeyCode.SPACE, false)) {
            ship.shoot();
        }
        if (this.pressedKeys.getOrDefault(KeyCode.DIGIT1, false)) {
            ship.toggleGun1();
        }
        if (this.pressedKeys.getOrDefault(KeyCode.DIGIT2, false)) {
            ship.toggleGun2();
        }
        if (this.pressedKeys.getOrDefault(KeyCode.DIGIT8, false)) {
            ship.toggleGun3();
        }
        if (this.pressedKeys.getOrDefault(KeyCode.DIGIT9, false)) {
            ship.toggleGun4();
        }
        if (this.pressedKeys.getOrDefault(KeyCode.M, false)) {
            audioplayer.toggleMusic();
        }
        if (this.pressedKeys.getOrDefault(KeyCode.R, false)) {   // kludge: (R)estart the audio thread if it stops. I don't know why it does that.
            if (audioplayer.test() == false) {
                audioplayer.start();
            }
        }
        if (this.pressedKeys.getOrDefault(KeyCode.ESCAPE, false)) {
            Globals.getInstance().setEsc();
        }

    }
}
