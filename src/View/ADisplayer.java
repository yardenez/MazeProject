package View;

import algorithms.mazeGenerators.Maze;
import javafx.scene.canvas.Canvas;

/**
 * an abstract class which represent a certain display
 */
public abstract class ADisplayer extends Canvas {

    protected Maze maze;
    protected double cellHeight, cellWidth;

    /**
     * clear the canvas from it's current drawings.
     */
    public void clear() {getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());}

    /**
     * sets a new given maze.
     * @param maze - the maze we wish to display.
     */
    public void setMaze(Maze maze){
        if(maze!=null) {
            this.maze = maze;
            cellHeight = getHeight()/maze.getNumOfRows();
            cellWidth = getWidth()/maze.getNumOfColumns();
        }
    }


}
