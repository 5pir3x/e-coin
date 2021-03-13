package com.company.Controller;

import com.company.Model.BlockChainData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Optional;

public class MainWindowController {

    @FXML
    public TableView tableview = new TableView(); //this is read-only UI table refer to ContactData table for editing
    @FXML
    private TableColumn firstName;
    @FXML
    private TableColumn secondName;
    @FXML
    private TableColumn phoneNumber;
    @FXML
    private TableColumn notes;
    @FXML
    private BorderPane borderPane;
    @FXML

    public static int index;
//    private MenuItem addContact;


    public void initialize() {

//        firstName.setCellValueFactory(
//                new PropertyValueFactory<Transaction, String>("from"));
//        secondName.setCellValueFactory(
//                new PropertyValueFactory<Transaction, String>("to"));
//        phoneNumber.setCellValueFactory(
//                new PropertyValueFactory<Transaction, String>("value"));
//        notes.setCellValueFactory(
//                new PropertyValueFactory<Transaction, String>("signature"));
//
//        tableview.setItems(contacts);
//       ContactData.getInstance().setContacts(contacts);

        tableview.setItems(BlockChainData.getInstance().getTransactionLedger());
        tableview.getSelectionModel().select(0);

    }

    // Da sozdade dialog pane za kreiranje nov kontakt
    @FXML
    public void toNewContactController() {
        Dialog<ButtonType> newContactController = new Dialog<>();
        newContactController.initOwner(borderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("AddNewContactWindow.fxml"));
        try {
//            Parent root = FXMLLoader.load(getClass().getResource("AddNewContactWindow.fxml"));
            newContactController.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Cant load dialog");
            e.printStackTrace();
            return;
        }
        newContactController.getDialogPane().getButtonTypes().add(ButtonType.OK);
        newContactController.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = newContactController.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            System.out.println("Ok pressed");
        }
    }



    @FXML
    public void editSelectedItem() {
        index = tableview.getSelectionModel().getSelectedIndex();
        Dialog<ButtonType> newContactController = new Dialog<>();
        newContactController.initOwner(borderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("EditNewContactWindow.fxml"));
        try {
            Parent root = FXMLLoader.load(getClass().getResource("AddNewContactWindow.fxml"));
            newContactController.getDialogPane().setContent(fxmlLoader.load());
        } catch (IOException e) {
            System.out.println("Cant load dialog");
            e.printStackTrace();
            return;
        }
        newContactController.getDialogPane().getButtonTypes().add(ButtonType.OK);
        newContactController.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        newContactController.showAndWait();



    }
    @FXML
    public void deleteSelectedItem() {
        int index = tableview.getSelectionModel().getSelectedIndex();
//        ContactData.getInstance().deleteContact(index);


    }

    @FXML
    public void handleExit() {
        Platform.exit();
    }
}
