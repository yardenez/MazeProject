package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.search.Solution;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {

    private IModel model;

    private int characterPositionRowIndex;
    private int characterPositionColumnIndex;

    public StringProperty characterPositionRow = new SimpleStringProperty(); //For Binding
    public StringProperty characterPositionColumn = new SimpleStringProperty(); //For Binding

    public MyViewModel(IModel model){
        this.model = model;
    }

    @Override
    public void update(Observable o, Object arg) {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                if (o==model){
                    characterPositionRowIndex = model.getCharacterPositionRow();
                    characterPositionRow.set(characterPositionRowIndex + "");
                    characterPositionColumnIndex = model.getCharacterPositionColumn();
                    characterPositionColumn.set(characterPositionColumnIndex + "");
                    setChanged();
                    notifyObservers(arg);
                }
            }
        });
    }

    public void generateMaze(int width, int height){
        model.generateMaze(width, height);
    }

    public void solveMaze(){
        model.solveMaze();
    }

    public void moveCharacter(KeyCode movement){
        model.moveCharacter(movement);
    }

    public Maze getMaze() {
        return model.getMaze();
    }

    public Solution getSolution() {return model.getSolution();}

    public int getCharacterPositionRow() {
        return characterPositionRowIndex;
    }

    public int getCharacterPositionColumn() {
        return characterPositionColumnIndex;
    }

    public void save(File file){
        model.save(file);
    }

    public void load (File file){
        model.load(file);
    }

    public void exit(){
        model.stopServersAndThreadPool();
    }
}
