package be.spacepex.stratcallerserver;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class StratCallerServerController {
    @FXML
    private TextField ipTextField;
    @FXML
    private Label portLabel;
    @FXML
    private Button connectButton;
    @FXML
    private Button stopButton;
    @FXML
    private TextArea console;

    Thread thread;
    Server server;

    @FXML
    protected void onConnectButtonPress() {
        server = new Server(this);
        if(server.validateIpAddress(ipTextField.getText())){
            server.establishConnection(ipTextField.getText());
        }
        else{
            printToConsole("Invalid ip address format.");
            return;
        }

        thread = new Thread(server.receiveAndPressKeyInputs());
        thread.start();

        connectButton.setDisable(true);
        stopButton.setDisable(false);
        ipTextField.setEditable(false);

        printToConsole("");
        printToConsole("Server now accepts inputs from phone.");
        printToConsole("Thread id: " + thread.getId());
    }

    @FXML
    protected void onStopButtonPress(){
        printToConsole("");
        if(thread.isAlive()){
            if(server != null && server.validateIpAddress(ipTextField.getText())){
                server.stopServerSocket(ipTextField.getText());
            }
            thread.stop();
            printToConsole("Stopped thread with id: " + thread.getId());
        }

        connectButton.setDisable(false);
        stopButton.setDisable(true);
        ipTextField.setEditable(true);
        printToConsole("Server stopped, connection lost.");
        printToConsole("--------------------STOP--------------------");
    }

    public void printToConsole(String text){
        console.appendText(text + "\n");
    }
}