package insertionsort;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.util.Random;

/**
 * Created by Z on 12.03.
 * Introduction to Java Programming, 10th Edition
 * Chapter  23: Sorting
 *
 * Exercise 23.15 (Selection sort animation):
 * Write a program that animates the selection sort algorithm. Create
 * an array that consists of 20 distinct numbers from 1 to 20 in a
 * random order. The array elements are displayed in a histogram.
 * Clicking the Step button causes the program to perform an iteration
 * of the outer loop in the algorithm and repaints the histogram for
 * the new array. Color the last bar in the sorted subarray. When the
 * algorithm is finished, display a message to inform the user.
 * Clicking the Reset button creates a new random array for a new
 * start.
 */
public class InsertionSortRunner extends Application {

    @Override
    public void start(Stage primaryStage) {
        InsertionSortPane pane = new InsertionSortPane(getRandomIntArray(50, 20));
//        InsertionSortPane pane = new InsertionSortPane();
        pane.setPadding(new Insets(5));
        pane.setAnimationTime(200);

        Text txtNotif = new Text(100, 100, "");
        txtNotif.setFont(Font.font(12));
        txtNotif.setTextAlignment(TextAlignment.CENTER);

        Button btSort = new Button("Sort");
        Button btStep = new Button("Step");
        Button btReset = new Button("Reset");

        btSort.setOnAction(e -> {
            pane.sort();
            btStep.setDisable(true);
        });
        btStep.setOnAction(e -> {
            if (!pane.isPlaying()) {
                pane.nextStep();
                pane.requestFocus();
                if (pane.isSorted()) {
                    txtNotif.setText("Data is sorted. Click Reset to shuffle.");
                }
            }
        });
        btReset.setOnAction(e -> {
            pane.reset();
            pane.requestFocus();
            btStep.setDisable(false);
            txtNotif.setText("");
        });

        pane.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                pane.nextStep();
                if (pane.isSorted())
                    txtNotif.setText("Data is sorted. Click Reset to shuffle.");
            } else if (event.getCode() == KeyCode.SPACE)
                pane.reset();
        });

        HBox buttons = new HBox(5);
        buttons.setAlignment(Pos.CENTER);
        buttons.getChildren().addAll(btStep, btReset);

        VBox bottom = new VBox(5);
        bottom.setAlignment(Pos.CENTER);
        bottom.getChildren().addAll(txtNotif, buttons);

        BorderPane root = new BorderPane(new StackPane(pane));
        root.setPadding(new Insets(10));
        root.setBottom(bottom);

        Scene scene = new Scene(root, pane.getWidth(), pane.MAX_COL_HEIGHT + 75);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Insertion Sort");
        primaryStage.show();


        pane.requestFocus();
    }

    private static int[] getRandomIntArray(int numValues, int max) {
        int[] a = new int[numValues];
        Random r = new Random();
        for (int i = 0; i < a.length; i++)
            a[i] = r.nextInt(max) + 1;

        return a;
    }
}
