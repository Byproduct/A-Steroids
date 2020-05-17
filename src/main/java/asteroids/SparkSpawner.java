package asteroids;

import java.util.List;
import java.util.Random;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class SparkSpawner {

    Random rnd = new Random();
    List<Spark> sparks;
    private int unusedSparkIndex;

    public SparkSpawner() {
        this.unusedSparkIndex = 0;
    }

    public void spawn(int x, int y, String scale) {

        if (this.foundFreeSlot() == true) {                  // add a new spark only if there are unused slots in the list
            Polygon sparkShape = new Polygon(
                    6, -1,
                    6, 1,
                    -6, 1,
                    -6, -1);
            this.sparks = Globals.getInstance().getSparks();
            Pane pane = Globals.getInstance().getPane();
            Spark spark = new Spark(sparkShape, x, y);
            spark.setScale(scale);
//        Spark.setSpawnDelay(0);
            int acceleration = 0;

            if (scale.equals("small")) {
                acceleration = 100 + rnd.nextInt(100);
                spark.setRange(5 + rnd.nextInt(10));
            }

            if (scale.equals("medium")) {
                acceleration = 50 + rnd.nextInt(50);
                spark.setRange(20 + rnd.nextInt(15));
            }

            if (scale.equals("large")) {
                acceleration = 25 + rnd.nextInt(25);
                spark.setRange(50 + rnd.nextInt(25));
            }
            if (scale.equals("huge")) {
                acceleration = 100 + rnd.nextInt(200);
                spark.setRange(10 + rnd.nextInt(40));
            }
            spark.getShape().setFill(Color.rgb(rnd.nextInt(55) + 200, rnd.nextInt(105) + 100, rnd.nextInt(100) + 50));
            spark.getShape().setRotate(rnd.nextInt(360));

            for (int i = 0; i < acceleration; i++) {
                spark.accelerate();
            }
            this.sparks.set(this.unusedSparkIndex, spark);
            pane.getChildren().add(spark.getShape());
        }
    }

    public void initialize() {
        this.sparks = Globals.getInstance().getSparks();
        Polygon sparkShape = new Polygon(
                6, -1,
                6, 1,
                -6, 1,
                -6, -1);
        for (int i = 0; i < 9000; i++) {
            Spark spark = new Spark(sparkShape, 0, 0);
            spark.setAlive(false);
            this.sparks.add(spark);
        }
    }

    public boolean foundFreeSlot() {
        this.sparks = Globals.getInstance().getSparks();
        boolean found = false;
        int i = 0;
        for (Spark spark : this.sparks) {
            if (!spark.isAlive()) {
                this.unusedSparkIndex = i;
                found = true;
                break;
            }
            i++;                      // spark in this index still in use (alive == true) -> trying next index
        }
        return found;
    }
}
