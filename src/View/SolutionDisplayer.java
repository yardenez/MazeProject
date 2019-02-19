package View;

import algorithms.mazeGenerators.Maze;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import javafx.scene.canvas.Canvas;

/**
 * solutionDisplayer displays the solution upon the maze.
 */
public class SolutionDisplayer extends ADisplayer {

    private Solution sol;

    //region property
    private StringProperty ImageFileNameSolution = new SimpleStringProperty();

    public String getImageFileNameSolution() {
        return ImageFileNameSolution.get();
    }

    public void setImageFileNameSolution(String imageFileNameSolutionl) {
        this.ImageFileNameSolution.set(imageFileNameSolutionl);
    }
    //end of region

    public void setSolution(Solution solution){
        if(solution!=null && maze!=null) {
            this.sol = solution;
            redraw();
        }
        else
            System.out.println("Solution is not valid!");
    }

    public void redraw(){
        try{
            Image solutionImage = new Image (new FileInputStream(ImageFileNameSolution.get()));
            GraphicsContext gc = getGraphicsContext2D();
            gc.clearRect(0, 0, getWidth(), getHeight()); //????
            //draw solution
            ArrayList<AState> mazeSolutionSteps = sol.getSolutionPath();
            for (int i = 0; i < mazeSolutionSteps.size()-1; i++) {
                MazeState mazeState = (MazeState)mazeSolutionSteps.get(i);
                //consider drawing non diagonal too!!!
//                if(i>0){
//                    MazeState previosMazeState = (MazeState)mazeSolutionSteps.get(i-1);
//                    int curMazeStateRow=
//                    if(mazeState.getPosition().getColumnIndex()!=previosMazeState.getPosition().getColumnIndex() &&
//                            mazeState.getPosition().getRowIndex()!=previosMazeState.getPosition().getRowIndex())
//
//                }
                gc.drawImage(solutionImage, mazeState.getPosition().getColumnIndex()* cellHeight, mazeState.getPosition().getRowIndex()* cellWidth, cellHeight, cellWidth);
            }
        }
        catch (FileNotFoundException e){
            //e.printStackTrace();
        }

    }
}
