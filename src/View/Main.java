package View;

import Model.*;
import ViewModel.MyViewModel;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
//        myModel model = new myModel();
//        model.startServers();
//        MyViewModel viewModel = new MyViewModel(model);
//        model.addObserver(viewModel);
        //--------------
        primaryStage.setTitle("The Frozen Maze");
        FXMLLoader fxmlLoader = new FXMLLoader();
//        Parent root = fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
//        FXMLLoader fxmlLoader1 = new FXMLLoader();
//        Parent logo = fxmlLoader1.load(getClass().getResource("MazeLogo.fxml").openStream());
//        FXMLLoader fxmlLoader2 = new FXMLLoader();
        Parent root = fxmlLoader.load(getClass().getResource("MazeLogo.fxml").openStream());
        Scene scene = new Scene(root, 650, 400);
//        Scene scene2 =  new Scene(logo, 800, 700);
//        Scene scene3 = new Scene(intro, 800,700);
        //scene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
        primaryStage.setScene(scene);
        //--------------
//        MyViewController view = fxmlLoader.getController();
//        view.setPrimaryStage(primaryStage);
//        view.setResizeEvent(scene);
//        view.setViewModel(viewModel);
//        viewModel.addObserver(view);
        //--------------
        //SetStageCloseEvent(primaryStage);
        primaryStage.show();
        PauseTransition delay = new PauseTransition(Duration.seconds(1));
        delay.setOnFinished(event -> {
            try{
                FXMLLoader newFxmlLoader = new FXMLLoader();
                Parent secondView =newFxmlLoader.load(getClass().getResource("Intro.fxml").openStream());
                Scene secondScene = new Scene(secondView,600,400);
                primaryStage.setScene(secondScene);
            }
            catch (IOException exception){
                //exception.printStackTrace();
            }
        });
        delay.play();
        primaryStage.show();
    }



    private void SetStageCloseEvent(Stage primaryStage) {
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent windowEvent) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.get() == ButtonType.OK){
                    // ... user chose OK
                    // Close program
                } else {
                    // ... user chose CANCEL or closed the dialog
                    windowEvent.consume();
                }
            }
        });
    }

    public static void main(String[] args) {
        launch(args);
    }
}
