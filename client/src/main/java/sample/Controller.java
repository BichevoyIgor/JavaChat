package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ResourceBundle;

public class Controller implements Initializable {
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
    public TextField loginField;
    @FXML
    public PasswordField passwordField;
    @FXML
    public GridPane messagePanel;
    @FXML
    public HBox authPanel;

    private Socket socket;
    private final int PORT = 8188;
    private DataInputStream in;
    private DataOutputStream out;
    private final String HOST = "localhost";

    boolean authentification;
    private String nickname;
    private Stage stage;

    public void setAuthentification(boolean authentification) {
        this.authentification = authentification;
        messagePanel.setVisible(authentification);
        messagePanel.setManaged(authentification);
        authPanel.setVisible(!authentification);
        authPanel.setManaged(!authentification);
        textArea1.clear();
        setTitle(nickname);
    }

    //метод initialize запустится после прогрузки всех объектов формы (implements Initializable)
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Platform.runLater(() -> {
            stage = (Stage) textArea1.getScene().getWindow();
        });
    }

    private void connect() {
        try {
            socket = new Socket(HOST, PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());

            new Thread(() -> {
                try {
                    while (true) {
                        String str = in.readUTF();
                        if (str.equals("/exit")) {
                            System.out.println("Клиент отключен");
                            out.writeUTF("/exit");
                            break;
                        }
                        if (str.startsWith("/authok")) {
                            String[] token = str.split("\\s");
                            nickname = token[1];
                            setAuthentification(true);
                        }
                        textArea1.appendText(str + "\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    setAuthentification(false);
                    setTitle("");
                    loginField.clear();

                    try {
                        in.close();
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void send(ActionEvent actionEvent) {
        if (!textField1.getText().isEmpty()) {
            try {
                out.writeUTF(textField1.getText());
                textField1.clear();
                textField1.requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else textField1.requestFocus();
    }

    @FXML
    public void sendTextFromTextField(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER) && !textField1.getText().isEmpty()) {
            try {
                out.writeUTF(textField1.getText());
                textField1.clear();
                textField1.requestFocus();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else textField1.requestFocus();
    }

    @FXML
    public void exit(ActionEvent actionEvent) {
        Platform.runLater(() -> {
            Stage stage = (Stage) sendButton.getScene().getWindow();
            stage.close();
            if (socket != null){
            try {
                out.writeUTF("/exit");
            } catch (IOException e) {
                e.printStackTrace();
            }}
        });
    }

    public void tryAuth(ActionEvent actionEvent) {
        if (socket == null || socket.isClosed()) connect();
        try {
            out.writeUTF(String.format("/auth %s %s", loginField.getText().trim(), passwordField.getText().trim()));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            passwordField.clear();
        }
    }

    private void setTitle(String nickname){
        Platform.runLater(()->{
            if (nickname.equals("")){
                stage.setTitle("JavaChat");
            }else {
                stage.setTitle("JavaChat - " + nickname);
            }
        });
    }
}
