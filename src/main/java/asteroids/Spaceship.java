package asteroids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javafx.geometry.Point2D;
import javafx.scene.layout.Pane;
import javafx.scene.media.AudioClip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class Spaceship extends Entity {

    private List<Laser> shots = new ArrayList<>();
    private List<Trail> trails = new ArrayList<>();
    private AudioClip thrust;
    private double worldX;
    private double worldY;
    private boolean thrustSound;
    private boolean weapon1;
    private boolean weapon2;
    private boolean weapon8;
    private boolean weapon9;
    private float cooldown1;
    private float cooldown2;
    private float cooldown8;
    private float cooldown9;
    public AudioPlayer audioplayer = new AudioPlayer();
    Random rnd = new Random();

    public Spaceship(int x, int y) {
        super(new Polygon(-10, -10, 20, 0, -10, 10), x, y);
        super.getShape().setFill(Color.rgb(0, 100, 255));
        this.cooldown1 = 0;
        this.cooldown2 = 0;
        this.cooldown8 = 0;
        this.cooldown9 = 0;
        this.worldX = 0;
        this.worldY = 0;
        this.weapon1 = true;
        this.weapon2 = true;
        this.weapon8 = false;
        this.weapon9 = false;

        audioplayer.loadFile("thrust", "thrust.wav", 1, 7);
        audioplayer.loadFile("gun3", "gun3.wav", 0.20, 6);
        audioplayer.loadFile("gun4", "gun4.wav", 0.15, 6);
    }

    public void startAudioThread() {
        audioplayer.start();
    }

    public void stopAllSounds() {
        this.audioplayer.stopAll();
    }

    public void shoot() {
        if (this.weapon1 == true) {      // normal laser
            shootWeapon1();
        }
        if (this.weapon2 == true) {      // homing ...laser?
            shootWeapon2();
        }
        if (this.weapon8 == true) {      // normal laser (admin mode)
            shootWeapon8();
        }
        if (this.weapon9 == true) {      // homing laser (admin mode)
            shootWeapon9();
        }
    }

    public void shootWeapon1() {     //normal laser
        Pane pane = Globals.getInstance().getPane();   //to-do: can I give this pane reference to the entire class instead of every method individually?
        if (this.cooldown1 <= 0) {
            audioplayer.playSound("gun4");
            for (int i = 0; i < 1; i++) {         // number of lasers fired per burst (default 1)
                Laser laser = new Laser((int) this.getShape().getTranslateX() + rnd.nextInt(20) - 10, (int) this.getShape().getTranslateY() + rnd.nextInt(20) - 10);
                setNewLaserPosition(laser);
                laser.accelerate(500);

                this.shots.add(laser);
                pane.getChildren().add(laser.getShape());

                this.cooldown1 += 2.5;           // cooldown (here per laser, could also be per burst)
            }
        }
    }

    public void shootWeapon2() {     // homing ...laser?
        Pane pane = Globals.getInstance().getPane();
        if (this.cooldown2 <= 0) {
            audioplayer.playSound("gun3");
            for (int i = 0; i < 50; i++) {         // default 1
                Laser_homing laser = new Laser_homing((int) this.getShape().getTranslateX() + rnd.nextInt(20) - 10, (int) this.getShape().getTranslateY() + rnd.nextInt(20) - 10);
                setNewLaserPosition(laser);
                laser.accelerate(400);

                laser.acquireNewTarget();         // to-do: is target acquisition expensive? Spawn 5 seekers per frame and queue the rest?
                this.shots.add(laser);

                pane.getChildren().add(laser.getShape());

                this.cooldown2 += 3.0;
            }
        }
    }

    public void shootWeapon8() {     // admin laser
        Pane pane = Globals.getInstance().getPane();
        if (this.cooldown1 <= 0) {
            audioplayer.playSound("gun4");
            for (int i = 0; i < 10; i++) {
                Laser laser = new Laser((int) this.getShape().getTranslateX() + rnd.nextInt(20) - 10, (int) this.getShape().getTranslateY() + rnd.nextInt(20) - 10);
                setNewLaserPosition(laser);
                laser.accelerate(750);

                this.shots.add(laser);
                pane.getChildren().add(laser.getShape());
                this.cooldown1 += 0.3;
            }
        }
    }

    public void shootWeapon9() {     // admin seeker
        Pane pane = Globals.getInstance().getPane();
        if (this.cooldown2 <= 0) {
            audioplayer.playSound("gun3");
            for (int i = 0; i < 25; i++) {
                Laser_homing laser = new Laser_homing((int) this.getShape().getTranslateX() + rnd.nextInt(20) - 10, (int) this.getShape().getTranslateY() + rnd.nextInt(20) - 10);
                setNewLaserPosition(laser);
                laser.accelerate(400);

                laser.acquireNewTarget();
                this.shots.add(laser);

                pane.getChildren().add(laser.getShape());

                this.cooldown2 += 0.3;
            }
        }
    }

    public void setNewLaserPosition(Laser laser) {
        double moveX = Math.cos(Math.toRadians(this.getShape().getRotate()));
        double moveY = Math.sin(Math.toRadians(this.getShape().getRotate()));
        laser.getShape().setTranslateX(laser.getShape().getTranslateX() + moveX * 10 + 5);
        laser.getShape().setTranslateY(laser.getShape().getTranslateY() + moveY * 12);
        laser.getShape().setRotate(this.getShape().getRotate() + rnd.nextInt(20) - 10);
    }

    public void pressThrust() {
        Pane pane = Globals.getInstance().getPane();
        audioplayer.playIfNew("thrust");

        for (int i = 0; i < 3; i++) {
            Trail trail = new Trail((int) this.getShape().getTranslateX(), (int) this.getShape().getTranslateY());
            double moveX = Math.cos(Math.toRadians(this.getShape().getRotate() + 180));
            double moveY = Math.sin(Math.toRadians(this.getShape().getRotate() + 180));
            trail.getShape().setTranslateX(trail.getShape().getTranslateX() + moveX * 25 + 5);
            trail.getShape().setTranslateY(trail.getShape().getTranslateY() + moveY * 25);
            trail.getShape().setRotate(this.getShape().getRotate() + 180 + rnd.nextInt(40) - 20);
            this.trails.add(trail);
            trail.accelerate();
            trail.setVelocity(trail.getVelocity().normalize().multiply(5));
            pane.getChildren().add(trail.getShape());
        }
    }

    public void releaseThrust() {
        audioplayer.stopSound("thrust");
    }

    @Override
    public void move() {
        super.move();
        this.getVelocity();
        this.worldX += this.getVelocity().getX();
        this.worldY += this.getVelocity().getY();
    }

    public void cooldowns() {
        if (this.cooldown1 > 0) {
            this.cooldown1--;
        }
        if (this.cooldown2 > 0) {
            this.cooldown2--;
        }
        if (this.cooldown8 > 0) {
            this.cooldown8--;
        }
        if (this.cooldown9 > 0) {
            this.cooldown9--;
        }
    }

    public List<Laser> getShots() {
        return shots;
    }

    public List<Trail> getTrails() {
        return trails;
    }

    public double getWorldX() {
        return worldX;
    }

    public double getWorldY() {
        return worldY;
    }

    public void toggleGun1() {
        if (this.weapon1 == true) {
            this.weapon1 = false;
        } else {
            this.weapon1 = true;
            this.weapon8 = false;
        }
    }

    public void toggleGun2() {
        if (this.weapon2 == true) {
            this.weapon2 = false;
        } else {
            this.weapon2 = true;
            this.weapon9 = false;
        }
    }

    public void toggleGun3() {
        if (this.weapon8 == true) {
            this.weapon8 = false;
        } else {
            this.weapon8 = true;
            this.weapon1 = false;
        }
    }

    public void toggleGun4() {
        if (this.weapon9 == true) {
            this.weapon9 = false;
        } else {
            this.weapon9 = true;
            this.weapon2 = false;
        }
    }
}
