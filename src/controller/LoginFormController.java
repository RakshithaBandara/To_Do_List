package controller;

import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.stage.Window;
import sun.awt.ScrollPaneWheelScroller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginFormController {
    public AnchorPane root;
    public TextField txtUsername;
    public PasswordField txtPassword;
    public static String loginUsername;
    public static String loginUserID;

    public void btnCreateNewAccountOnAction(ActionEvent actionEvent) throws IOException {
        Parent parent = FXMLLoader.load(this.getClass().getResource("../view/CreateNewAccountForm.fxml"));
        Scene scene = new Scene(parent);
        Stage primaryStage =(Stage) root.getScene().getWindow();
        primaryStage.setScene(scene);
        primaryStage.setTitle("Register Form");
        primaryStage.centerOnScreen();
        Image image = new Image("image/add.png");
    }

    public void btnLoginOnAction(ActionEvent actionEvent) {
        login();
    }

    public void txtPasswordOnAction(ActionEvent actionEvent) {
        login();
    }

    public void login(){

        if(txtUsername.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR,"Username Cannot Be Empty");
            alert.showAndWait();
        }
        else if(txtPassword.getText().isEmpty()){
            Alert alert = new Alert(Alert.AlertType.ERROR,"Password Cannot Be Empty");
            alert.showAndWait();
        }
        else {
            String username = txtUsername.getText();
            String password = txtPassword.getText();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("select * from user where username = ? and password = ?");
                preparedStatement.setObject(1,username);
                preparedStatement.setObject(2,password);

                ResultSet resultSet = preparedStatement.executeQuery();
                if(resultSet.next()){
                    loginUsername = resultSet.getString(3);
                    loginUserID = resultSet.getString(1);

                    Parent parent = FXMLLoader.load(getClass().getResource("../view/DashboardForm.fxml"));
                    Scene scene = new Scene(parent);
                    Stage primaryStage = (Stage) root.getScene().getWindow();
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("To Do Form");
                    primaryStage.centerOnScreen();
                    Image image = new Image("image/directory.png");
                }
                else {
                    Alert alert = new Alert(Alert.AlertType.ERROR,"Username or password does not matched.");
                    alert.showAndWait();

                    txtUsername.clear();
                    txtPassword.clear();
                    txtUsername.requestFocus();
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }

        }

    }
}
