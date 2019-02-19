package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import com.sun.org.apache.xpath.internal.SourceTree;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.File;
import java.util.Observable;
import java.util.Observer;
import java.util.Optional;

import static Server.Server.Configurations.getMazeGeneratorAlgorithm;
import static Server.Server.Configurations.getSearchAlgorithm;

public class MyViewController implements Observer,IView {

    @FXML
    private MyViewModel viewModel;
    private Stage primaryStage;
    public MazeDisplayer mazeDisplayer;
    public SolutionDisplayer solutionDisplayer;
    public CharacterDisplayer characterDisplayer;
    public javafx.scene.control.TextField txtfld_rowsNum;
    public javafx.scene.control.TextField txtfld_columnsNum;
    public javafx.scene.control.Label lbl_rowsNum;
    public javafx.scene.control.Label lbl_columnsNum;
    public javafx.scene.control.Button btn_generateMaze;
    public javafx.scene.control.Button btn_solveMaze;
    private MediaPlayer mediaPlayer;
    public javafx.scene.control.ChoiceBox choiseBoxGenerator;
    public javafx.scene.control.ChoiceBox choiseBoxSolver;
    ObservableList<String> generatorChoises= FXCollections.observableArrayList("SimpleMazeGenerator","MyComplexMazeGenerator","MyMazeGenerator");
    ObservableList<String> solvedChoises= FXCollections.observableArrayList("BestFirstSearch","BreadthFirsrSearch","DepthFirstSearch");


    public void setViewModel(MyViewModel viewModel) {
        this.viewModel = viewModel;
        bindProperties(viewModel);
    }

    public void setPrimaryStage(Stage primaryStage){
        this.primaryStage = primaryStage;
    }

    private void bindProperties(MyViewModel viewModel) {
        lbl_rowsNum.textProperty().bind(viewModel.characterPositionRow);
        lbl_columnsNum.textProperty().bind(viewModel.characterPositionColumn);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == viewModel) {
            if(arg instanceof String && arg.equals("Solved maze!")){
                solutionDisplayer.setMaze(viewModel.getMaze());
                solutionDisplayer.setSolution(viewModel.getSolution());
                characterDisplayer.redraw();
                btn_solveMaze.setDisable(false);
            }
            else if (arg instanceof String && (arg.equals("New maze was generated!") || arg.equals("New maze was loaded!")))
            {
                clearBoard();
                mazeDisplayer.setMaze(viewModel.getMaze());
                characterDisplayer.setMaze(viewModel.getMaze());
                displayCharcther();
                btn_solveMaze.setDisable(false);
                playSound("WindSound.mp3");

            }
            else if (arg instanceof String && arg.equals("Character moved!")){
                characterDisplayer.clear();
                displayCharcther();
            }
            else if (arg instanceof String && arg.equals("Character reached to end")) {
                mediaPlayer.stop();
                playSound("letItGo.mp3");
                ReachedSolution();

            }
            else if (arg instanceof String && arg.equals("Could not open maze!\nFile with unknown extension was chosen.")) {
                showAlert((String) arg);
            }
        }
    }

    /**
     * clear all displayes
     */
    public void clearBoard(){
        if(characterDisplayer!=null)
            characterDisplayer.clear();
        if(solutionDisplayer!=null)
            solutionDisplayer.clear();
        if(mazeDisplayer!=null)
            mazeDisplayer.clear();
    }

    public void displayCharcther(){
        int characterPositionRow = viewModel.getCharacterPositionRow();
        int characterPositionColumn = viewModel.getCharacterPositionColumn();
        characterDisplayer.setCharacterPosition(characterPositionRow, characterPositionColumn);
        //binding
        this.characterPositionRow.set(characterPositionRow + "");
        this.characterPositionColumn.set(characterPositionColumn + "");
    }

    public void generateMaze() {
        try{
            int heigth = Integer.parseInt(txtfld_rowsNum.getText());
            int width = Integer.parseInt(txtfld_columnsNum.getText());
            if (heigth < 3 || width < 3)
                throw new NumberFormatException();
            btn_generateMaze.setDisable(true);
            btn_solveMaze.setDisable(true);
            txtfld_columnsNum.setDisable(true);
            txtfld_rowsNum.setDisable(true);
            viewModel.generateMaze(width, heigth);
        }catch (NumberFormatException e){
            showAlert("Maze dimensions are not valid!\nPlease enter only numbers bigger then 2.");
        }
    }

    public void solveMaze(ActionEvent actionEvent) {
        if(btn_solveMaze.getText().equals("Solve Maze")) {
            btn_solveMaze.setDisable(true);
            viewModel.solveMaze();
            btn_solveMaze.setText("Hide Solution");
        }
        else if(btn_solveMaze.getText().equals("Hide Solution")){
            solutionDisplayer.clear();
            btn_solveMaze.setText("Solve Maze");

        }
    }

    private void showAlert(String alertMessage) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(alertMessage);
        alert.show();
    }

    public void New(){
        clearBoard();
        btn_generateMaze.setDisable(false);
        btn_solveMaze.setDisable(true);
        txtfld_columnsNum.setDisable(false);
        txtfld_rowsNum.setDisable(false);
        if(btn_solveMaze.getText().equals("Hide Solution"))
            btn_solveMaze.setText("Solve Maze");
    }

    public void KeyPressed(KeyEvent keyEvent) {
        viewModel.moveCharacter(keyEvent.getCode());
        keyEvent.consume();
    }

    //region String Property for Binding
    public StringProperty characterPositionRow = new SimpleStringProperty();

    public StringProperty characterPositionColumn = new SimpleStringProperty();

    public String getCharacterPositionRow() {
        return characterPositionRow.get();
    }

    public StringProperty characterPositionRowProperty() {
        return characterPositionRow;
    }

    public String getCharacterPositionColumn() {
        return characterPositionColumn.get();
    }

    public StringProperty characterPositionColumnProperty() {
        return characterPositionColumn;
    }
    //endregion

    public void setResizeEvent(Scene scene) {
        long width = 0;
        long height = 0;
        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                System.out.println("Width: " + newSceneWidth);
            }
        });
        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                System.out.println("Height: " + newSceneHeight);
            }
        });
    }

    public void About(ActionEvent actionEvent) {
        try {
            Stage stage = new Stage();
            stage.setTitle("AboutController");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("About.fxml").openStream());
            Scene scene = new Scene(root, 600, 400);
            stage.setScene(scene);
            scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    public void Help(ActionEvent actionEvent){
        try {
            Stage stage = new Stage();
            stage.setTitle("Help");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Help.fxml").openStream());
            Scene scene = new Scene(root, 600, 400);
            scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();
        } catch (Exception e) {

        }
    }

    public void ReachedSolution(){
        try {
            Stage stage = new Stage();
            stage.setTitle("Maze Solved!");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("ReachedSolution.fxml").openStream());
            Scene scene = new Scene(root, 340, 380);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent windowEvent) {
                    mediaPlayer.stop();
                    New();
                }
            });
            stage.show();
        } catch (Exception e) {
            //e.printStackTrace();
        }
    }

    public void Save(ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze");
        fileChooser.getExtensionFilters().add(extFilter);

        File file = fileChooser.showSaveDialog(primaryStage);

        if(file != null){
            viewModel.save(file);
        }
    }

    public void Load (ActionEvent actionEvent){
        FileChooser fileChooser = new FileChooser();
        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Maze files (*.maze)", "*.maze");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);

        if(file != null){
            viewModel.load(file);
        }
    }

    public void Exit() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure you want to exit?");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            viewModel.exit();
            primaryStage.close();
        }
    }

    public void Properties(){
        try {
            Stage stage = new Stage();
            stage.setTitle("Properties");
            FXMLLoader fxmlLoader = new FXMLLoader();
            Parent root = fxmlLoader.load(getClass().getResource("Properties.fxml").openStream());
            Scene scene = new Scene(root, 400, 200);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL); //Lock the window until it closes
            stage.show();

        } catch (Exception e) {

        }
    }

    public String getMazeGeneratorAlgo(){
        return getMazeGeneratorAlgorithm();
    }

    public String getSearchAlgo(){
        return getSearchAlgorithm();
    }


    public void playSound(String song){
        if(mediaPlayer!=null)
            mediaPlayer.stop();
        String songPath= "Resources/Music/"+song;
        Media nowPlaying = new Media(new File(songPath).toURI().toString());
        mediaPlayer = new MediaPlayer(nowPlaying);
        mediaPlayer.play();
    }






}
