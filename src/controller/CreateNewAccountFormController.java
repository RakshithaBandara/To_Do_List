package controller;

import com.sun.corba.se.impl.resolver.SplitLocalResolverImpl;
import db.DBConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class CreateNewAccountFormController {
    public PasswordField txtNewPassword;
    public PasswordField txtConfirmPassword;
    public AnchorPane root;
    public Label lblPasswordDoesNotMatched1;
    public Label lblPasswordDoesNotMatched2;
    public Button btnRegister;
    public TextField txtUsername;
    public TextField txtEmail;
    public Label lblID;

    public void initialize(){
        setPasswordMatchLabelVisibility(false);
        interfaceOptionDisable(true);
    }

    public void btnRegisterOnAction(ActionEvent actionEvent) {
        register();
    }

    public void txtConfirmPasswordOnAction(ActionEvent actionEvent) {
        register();
    }

    public void register(){
        String newPassword = txtNewPassword.getText();
        String confirmPassword = txtConfirmPassword.getText();

        if(newPassword.equals(confirmPassword)){
            setBorderColor("transparent");
            setPasswordMatchLabelVisibility(false);

            String id = lblID.getText();
            String email = txtEmail.getText();
            String username = txtUsername.getText();

            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement =  connection.prepareStatement("insert into user values(?,?,?,?)");
                preparedStatement.setObject(1,id);
                preparedStatement.setObject(2,email);
                preparedStatement.setObject(3,username);
                preparedStatement.setObject(4,confirmPassword);

                int i = preparedStatement.executeUpdate();
                if(i!=0){
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"Successfully Registered...");
                    alert.showAndWait();

                    Parent parent = FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml"));
                    Scene scene = new Scene(parent);

                    Stage primaryStage = (Stage) root.getScene().getWindow();
                    primaryStage.setScene(scene);
                    primaryStage.setTitle("Login Form");
                    primaryStage.centerOnScreen();
                }
            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }

//            Parent parent = FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml"));
//            Scene scene = new Scene(parent);
//            Stage primaryStage = (Stage) root.getScene().getWindow();
//            primaryStage.setScene(scene);
//            primaryStage.setTitle("Login Form");
//            primaryStage.centerOnScreen();

        }
        else{
            setBorderColor("red");
            txtNewPassword.requestFocus();
            setPasswordMatchLabelVisibility(true);
        }
    }

    public void setBorderColor(String color){
        txtNewPassword.setStyle("-fx-border-color: "+color);
        txtConfirmPassword.setStyle("-fx-border-color: "+color);
    }

    public void setPasswordMatchLabelVisibility(Boolean option){
        lblPasswordDoesNotMatched1.setVisible(option);
        lblPasswordDoesNotMatched2.setVisible(option);
    }

    public void btnAddNewUserOnAction(ActionEvent actionEvent) {
        interfaceOptionDisable(false);
        txtUsername.requestFocus();

        autoGenerateID();
//        DBConnection dbConnection = DBConnection.getInstance();
//        Connection connection = dbConnection.getConnection();
//        This 2 lines in single line--.
//        Connection connection = DBConnection.getInstance().getConnection();
//
//        System.out.println(connection);
    }

    public void interfaceOptionDisable(boolean bool){
        txtEmail.setDisable(bool);
        txtUsername.setDisable(bool);
        txtNewPassword.setDisable(bool);
        txtConfirmPassword.setDisable(bool);
    }

    public void autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();

        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from user order by id desc limit 1");

            boolean isExist = resultSet.next();

            if (isExist){
                String userID =  resultSet.getString(1);
                userID = userID.substring(1,4);
                int intID = Integer.parseInt(userID);
                intID++;

                if(intID<10){
                    lblID.setText("U00"+intID);
                }else if(intID<100){
                    lblID.setText("U0"+intID);
                }else{
                    lblID.setText("U"+intID);
                }
            }else {
                lblID.setText("U001");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
