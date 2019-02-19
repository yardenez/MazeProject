package View;

import algorithms.mazeGenerators.Maze;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * the mazeDisplayer displays the current maze with graphics.
 */
public class MazeDisplayer extends ADisplayer {

    private int goalPositionRow;
    private int goalPositionColumn;

    /**
     * @param maze - the maze which was generated.
     */
    public void setMaze(Maze maze) {
        super.setMaze(maze);
        setGoalPosition(maze.getGoalPosition().getRowIndex(), maze.getGoalPosition().getColumnIndex());
        redraw();
    }

    public void setGoalPosition(int row, int column) {
        goalPositionRow = row;
        goalPositionColumn = column;
    }

    //region Properties
    private StringProperty ImageFileNameWall = new SimpleStringProperty();
    private StringProperty ImageFileNameGoalPosition = new SimpleStringProperty();
    private StringProperty ImageFileNamePass = new SimpleStringProperty();

    public String getImageFileNamePass(){return ImageFileNamePass.get();}

    public void setImageFileNamePass(String imageFileNamePass){this.ImageFileNamePass.set(imageFileNamePass);}

    public String getImageFileNameWall() {
        return ImageFileNameWall.get();
    }

    public void setImageFileNameWall(String imageFileNameWall) {
        this.ImageFileNameWall.set(imageFileNameWall);
    }

    public String getImageFileNameGoalPosition() {
        return ImageFileNameGoalPosition.get();
    }

    public void setImageFileNameGoalPosition(String ImageFileNameGoalPosition) {
        this.ImageFileNameGoalPosition.set(ImageFileNameGoalPosition);
    }
    //endregion

    public void redraw() {
        try {
            Image wallImage = new Image(new FileInputStream(ImageFileNameWall.get()));
            Image goalImage = new Image(new FileInputStream(ImageFileNameGoalPosition.get()));
            Image passImage = new Image(new FileInputStream(ImageFileNamePass.get()));
            GraphicsContext gc = getGraphicsContext2D();
            //Draw Maze
            for (int i = 0; i < maze.getNumOfRows(); i++) {
                for (int j = 0; j < maze.getNumOfColumns(); j++) {
                    if (maze.getPositionValue(i, j) == 1) {
                        gc.drawImage(wallImage, j * cellHeight, i * cellWidth, cellHeight, cellWidth);
                    }
                    else if (maze.getPositionValue(i,j) == 0){
                        gc.drawImage(passImage,j*cellHeight,i*cellWidth,cellHeight,cellWidth);
                    }
                }
            }
            //Draw goal position
            gc.drawImage(goalImage, goalPositionColumn * cellHeight, goalPositionRow * cellWidth, cellHeight, cellWidth);
        } catch (FileNotFoundException e) {
            //e.printStackTrace();
        }
    }
}


