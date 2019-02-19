package View;

import Model.myModel;
import ViewModel.MyViewModel;
import javafx.animation.FadeTransition;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import javax.swing.text.html.ImageView;

public class IntroController {
    @FXML
    private Button startGameButton;

    @FXML
    public void handleButtonClick(ActionEvent event)
    {
        loadNextScene();
    }

    private void loadNextScene(){
        FXMLLoader fxmlLoader=new FXMLLoader();
        try{
            Parent secondView = (BorderPane)fxmlLoader.load(getClass().getResource("MyView.fxml").openStream());
            Scene newScene = new Scene(secondView,800,650);
            newScene.getStylesheets().add(getClass().getResource("ViewStyle.css").toExternalForm());
            myModel model = new myModel();
            model.startServers();
            MyViewModel viewModel = new MyViewModel(model);
            model.addObserver(viewModel);
            MyViewController view = fxmlLoader.getController();
            Stage curStage = (Stage) startGameButton.getScene().getWindow();
            view.setPrimaryStage(curStage);
            view.setResizeEvent(newScene);
            view.setViewModel(viewModel);
            viewModel.addObserver(view);
            curStage.setScene(newScene);
            curStage.setOnCloseRequest(e -> {
                e.consume();
                view.Exit();
            });

        }
        catch(Exception e){
        //    e.printStackTrace();
        }


    }
}
