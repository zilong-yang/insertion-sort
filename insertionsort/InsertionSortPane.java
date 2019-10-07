package insertionsort;

import javafx.animation.Animation;
import javafx.animation.ParallelTransition;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;

import java.util.Random;

/**
 * Created by Z on 12.06.
 * Introduction to Java Programming, 10th Edition
 * Chapter  23: Sorting
 */
public class InsertionSortPane extends Pane {

    protected int[] data;
    protected Rectangle[] columns;
    private int max;

    private double colWidth = 20;
    public final double MAX_COL_HEIGHT = 250;

    public InsertionSortPane() {
        data = new int[20];
        for (int i = 1; i <= data.length; i++)
            data[i - 1] = i;

        shuffle();
        initializeColumns();
        draw();
    }

    public InsertionSortPane(int... data) {
        if (data.length == 0)
            throw new IllegalArgumentException("empty data");

        this.data = data;

        initializeColumns();
        draw();
    }

    public int dataSize() {
        return data.length;
    }

    public double getColWidth() {
        return colWidth;
    }

    public void setColWidth(double colWidth) {
        this.colWidth = colWidth;
        draw();
    }

    public void reset() {
        currentIndex = 0;
        shuffle();
        initializeColumns();
        draw();
    }

    public void shuffle() {
        Random r = new Random();
        for (int i = data.length; i > 0; i--)
            swap(i - 1, r.nextInt(i));
    }

    public boolean isSorted() {
        for (int i = 1; i < data.length; i++)
            if (data[i] < data[i - 1])
                return false;
        return true;
    }

    private SequentialTransition animation = new SequentialTransition();
    private double animationTime = 250;

    public double getAnimationTime() {
        return animationTime;
    }

    public void setAnimationTime(double animationTime) {
        this.animationTime = animationTime;
    }

    public boolean isPlaying() {
        return animation.getStatus() == Animation.Status.RUNNING;
    }

    public boolean isPaused() {
        return animation.getStatus() == Animation.Status.PAUSED;
    }

    public boolean isStopped() {
        return animation.getStatus() == Animation.Status.STOPPED;
    }

    private int currentIndex;

    public void sort() {
        animation.getChildren().clear();
        while (currentIndex < data.length) {
            sortOnce(currentIndex);
            currentIndex++;
        }
        animation.play();
        draw();
    }

    public void nextStep() {
        if (currentIndex < data.length) {
            columns[currentIndex].setFill(Color.GRAY);
            animation.getChildren().clear();
            sortOnce(currentIndex);
            animation.play();

            draw();
            currentIndex++;
        }
    }

    private int sortOnce(int index) {
        ParallelTransition para = new ParallelTransition();

        final int n = data[index];
        int k = index - 1;
        SequentialTransition st = new SequentialTransition();
        Rectangle current, target;

        for (; k >= 0 && data[k] > n; k--) {
            // swap the data
            int temp = data[k + 1];
            data[k + 1] = data[k];
            data[k] = temp;

            // animation for shifting the columns to the right by one
            current = columns[k];
            target = columns[k + 1];
            st.getChildren().add(moveTo(current, target, animationTime));
        }

        // gets the final position for columns[index] after sorted once
        int finalIndex = (++k == index) ? index : k;

        // ensures that finalIndex != index because
        // PathTransition wouldn't work if the node doesn't move
        if (finalIndex == index) // if finalIndex == index
            return index;

        double duration = animationTime * (index - finalIndex);
        double partDuration = duration / (index - finalIndex);
        for (Animation pt : st.getChildren())
            ((PathTransition) pt).setDuration(
                    Duration.millis(partDuration));

        para.getChildren().add(st);

        // animation for moving columns[index] to columns[finalIndex]
        current = columns[index];
        target = columns[finalIndex];
        para.getChildren().add(moveTo(current, target, duration));
        animation.getChildren().add(para);

        for (int i = currentIndex; i > finalIndex; i--)
            swapColumns(i, i - 1);

        return finalIndex;
    }

    private Animation moveTo(Rectangle from, Rectangle to, double time) {
        Line path = new Line(
                from.getX() + from.getWidth() / 2,
                from.getY() + from.getHeight() / 2,
                to.getX() + to.getWidth() / 2,
                from.getY() + from.getHeight() / 2
        );
        return new PathTransition(Duration.millis(time), path, from);
    }

    private void draw() {
        updateMax();
        getChildren().clear();

        for (int i = data.length - 1; i >= 0; i--) {
            Rectangle col = columns[i];
            double height = (double) data[i] / max * MAX_COL_HEIGHT;

            col.setWidth(colWidth);
            col.setHeight(height);
            col.setX(i * colWidth);
            col.setY(MAX_COL_HEIGHT - height);

            Text value = new Text(Integer.toString(data[i]));
            value.xProperty().bind(col.xProperty().add(5));
            value.yProperty().bind(col.yProperty().add(col.getHeight() / 2 + 4));
            value.setFont(Font.font(10));
            value.setOpacity(0);
            value.setFill(Color.BLACK);
            value.setTextAlignment(TextAlignment.CENTER);

            col.setOnMouseEntered(e -> {
//                value.setFill(Color.BLACK);
                value.setOpacity(1);
//                value.setHeadX(e.getHeadX());
//                value.setHeadY(e.getHeadY());
//                System.out.println(((int) e.getHeadX()) + " " + ((int) e.getHeadY()));
            });
            col.setOnMouseExited(e -> value.setOpacity(0));

            getChildren().addAll(col, value);
        }

        setWidth((data.length + 1) * colWidth);
        setHeight(MAX_COL_HEIGHT);
    }

    private void initializeColumns() {
        columns = new Rectangle[data.length];
        for (int i = 0; i < columns.length; i++) {
            columns[i] = new Rectangle();
            columns[i].setFill(Color.WHITE);
            columns[i].setStroke(Color.BLACK);
        }
    }

    private void updateMax() {
        max = data[0];
        for (int n : data)
            if (n > max)
                max = n;
    }

    private void swap(int i, int j) {
        int temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    private void swapColumns(int i, int j) {
        Rectangle temp = columns[i];
        columns[i] = columns[j];
        columns[j] = temp;
    }
}
