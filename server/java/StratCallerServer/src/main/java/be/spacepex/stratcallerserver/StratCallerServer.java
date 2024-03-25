package be.spacepex.stratcallerserver;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class StratCallerServer extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(StratCallerServer.class.getResource("strat_caller_server-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 420, 320);
        stage.setTitle("Strategem Caller Server");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}