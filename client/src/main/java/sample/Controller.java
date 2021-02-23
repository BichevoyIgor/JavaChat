package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;

public class Controller {

    @FXML
    public TextArea textArea1;
    @FXML
    public TextField textField1;
    @FXML
    public Button sendButton;
    @FXML
    public MenuBar menuBar;
    @FXML
    public Menu menuFile;
    @FXML
    public MenuItem menuItemExit;


    @FXML
    public void send(ActionEvent actionEvent) {
        if (!textField1.getText().isEmpty()) {
            textArea1.appendText(textField1.getText() + "\n");
            textField1.clear();
            textField1.requestFocus();
        } else textField1.requestFocus();
    }

    @FXML
    public void sendTextFromTextField(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER) && !textField1.getText().isEmpty()) {
            textArea1.appendText(textField1.getText() + "\n");
            textField1.clear();
            textField1.requestFocus();
        }
    }

    @FXML
    public void exit(ActionEvent actionEvent) {
        Platform.runLater(() ->{
            Stage stage = (Stage) sendButton.getScene().getWindow();
            stage.close();
        });
    }
}
