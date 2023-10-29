package controller;

import db.DBConnection;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tm.ToDoTM;

import java.io.IOException;
import java.sql.*;
import java.util.Optional;

public class DashboardFormController {
    public Label lblUserIWelcome;
    public Label lblUserID;
    public AnchorPane root;
    public Pane subRoot;
    public TextField txtDescription;
    public ListView<ToDoTM>lstToDo;
    public TextField txtSelectedDescription;
    public Button btnDelete;
    public Button btnUpdate;

    public String selectedID;

    public void initialize(){
        lblUserID.setText(LoginFormController.loginUserID);
        lblUserIWelcome.setText("Hello "+LoginFormController.loginUsername+", Welcome to To-Do List");
        subRoot.setVisible(false);
        loadList();

        setDisableeItems(true);

        lstToDo.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<ToDoTM>() {
            @Override
            public void changed(ObservableValue<? extends ToDoTM> observable, ToDoTM oldValue, ToDoTM newValue) {
                setDisableeItems(false);
                subRoot.setVisible(false);

                ToDoTM selectedItem = lstToDo.getSelectionModel().getSelectedItem();

                if(selectedItem == null){
                    return;
                }
                txtSelectedDescription.setText(selectedItem.getDescription());

                selectedID = selectedItem.getId();
            }
        });
    }

    public void setDisableeItems(Boolean bool){
        txtSelectedDescription.setDisable(bool);
        btnDelete.setDisable(bool);
        btnUpdate.setDisable(bool);
    }

    public void btnLogoutOnAction(ActionEvent actionEvent) throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you want  to logout?", ButtonType.YES,ButtonType.NO);
        Optional<ButtonType> buttonType = alert.showAndWait();

        if (buttonType.get().equals(ButtonType.YES)){
            Parent parent = FXMLLoader.load(getClass().getResource("../view/LoginForm.fxml"));
            Scene scene = new Scene(parent);

            Stage primaryStage = (Stage) root.getScene().getWindow();
            primaryStage.setScene(scene);
            primaryStage.setTitle("Login Form");
            primaryStage.centerOnScreen();
            Image image = new Image("image/login.png");

        }
    }

    public void btnAddNewToDoOnAction(ActionEvent actionEvent) {
        lstToDo.getSelectionModel().clearSelection();
        setDisableeItems(true);
        subRoot.setVisible(true);
        txtSelectedDescription.clear();
    }

    public void txtDescriptionOnAction(ActionEvent actionEvent) {
        addToList();
    }

    public void btnAddToListOnAction(ActionEvent actionEvent) {
        addToList();
    }

    public String autoGenerateID(){
        Connection connection = DBConnection.getInstance().getConnection();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select id from todo order by id desc limit 1");

            boolean isExist = resultSet.next();

            if(isExist){
                String toDoId = resultSet.getString(1);
                toDoId = toDoId.substring(1,4);
                int intToDoID = Integer.parseInt(toDoId);
                intToDoID++;

                if(intToDoID<10){
                    return ("T00"+intToDoID);
                }
                else if(intToDoID<100){
                    return  ("T0"+intToDoID);
                }
                else{
                    return  ("T"+intToDoID);
                }
            }else {
                return  "T001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void addToList(){
        String description = txtDescription.getText();
        String id = autoGenerateID();
        String user_id = lblUserID.getText();

        Connection connection = DBConnection.getInstance().getConnection();
        PreparedStatement preparedStatement = null;
        try {
            preparedStatement = connection.prepareStatement("insert into todo values(?,?,?)");
            preparedStatement.setObject(1,id);
            preparedStatement.setObject(2,description);
            preparedStatement.setObject(3,user_id);

            preparedStatement.executeUpdate();

            txtDescription.clear();
            subRoot.setVisible(false);

        } catch (SQLException e) {
            e.printStackTrace();
        }

        loadList();

    }

    public void loadList(){
        ObservableList<ToDoTM> items = lstToDo.getItems();
        items.clear();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("select * from todo where user_id = ?");
            preparedStatement.setObject(1, LoginFormController.loginUserID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String id = resultSet.getString(1);
                String description = resultSet.getString(2);
                String user_id = resultSet.getString(3);

                ToDoTM toDoTM = new ToDoTM(id, description, user_id);

                items.add(toDoTM);
            }
            lstToDo.refresh();

        }catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void btnDeleteOnAction(ActionEvent actionEvent) {
        Alert aler = new Alert(Alert.AlertType.CONFIRMATION,"Do you want to delete this To-Do?", ButtonType.YES, ButtonType.NO);
        Optional<ButtonType> buttonType = aler.showAndWait();

        if(buttonType.get().equals(ButtonType.YES)){
            Connection connection = DBConnection.getInstance().getConnection();

            try {
                PreparedStatement preparedStatement = connection.prepareStatement("delete from todo where id = ?");
                preparedStatement.setObject(1,selectedID);
                preparedStatement.executeUpdate();

                loadList();
                txtSelectedDescription.clear();
                setDisableeItems(true);
            } catch (SQLException e) {

            }
        }
    }

    public void btnUpdateOnAction(ActionEvent actionEvent) {
        String description = txtSelectedDescription.getText();

        Connection connection = DBConnection.getInstance().getConnection();

        try {
            PreparedStatement preparedStatement = connection.prepareStatement("update todo set description = ? where id = ?");
            preparedStatement.setObject(1,description);
            preparedStatement.setObject(2,selectedID);

            preparedStatement.executeUpdate();

            loadList();
            setDisableeItems(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
