package com.company.Controller;

import com.company.Model.BlockData;
import com.company.Model.Transaction;
import com.company.Model.WalletData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.Optional;

public class MainWindowController {

    @FXML
    public TableView tableview = new TableView(); //this is read-only UI table refer to ContactData table for editing
    @FXML
    private TableColumn from;
    @FXML
    private TableColumn to;
    @FXML
    private TableColumn value;
    @FXML
    private TableColumn signature;
    @FXML
    private BorderPane borderPane;
    @FXML
    public static int index;
    @FXML
    public TableView tableECoins = new TableView();;
    @FXML
    private ListView eCoins ;


    public void initialize() {
    //todo:get this to work
        from.setCellValueFactory(
                new PropertyValueFactory<Transaction, String>("from"));
        to.setCellValueFactory(
                new PropertyValueFactory<Transaction, String>("to"));
        value.setCellValueFactory(
                new PropertyValueFactory<Transaction, Integer>("value"));
        signature.setCellValueFactory(
                new PropertyValueFactory<Transaction, String>("signature"));
        eCoins.setItems(WalletData.getInstance().getWalletBalanceFX());

//        tableview.setItems(contacts);
//       ContactData.getInstance().setContacts(contacts);
//        tableECoins.getColumns().setAll(WalletData.getInstance().getWalletBalance());
        tableview.setItems(BlockData.getInstance().getTransactionLedger());
        tableview.getSelectionModel().select(0);

    }

    @FXML
    public void toNewTransactionController() throws URISyntaxException, MalformedURLException {
        Dialog<ButtonType> newTransactionController = new Dialog<>();
        newTransactionController.initOwner(borderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../View/AddNewTransactionWindow.fxml"));
        try {
            newTransactionController.getDialogPane().setContent(fxmlLoader.load());
//            FileInputStream fileInputStream = new FileInputStream(new File("C:\\Users\\spiro\\IdeaProjects\\e-coin\\src\\com\\company\\View\\AddNewTransactionWindow.fxml"));
//            newTransactionController.getDialogPane().setContent(fxmlLoader.load(fileInputStream));
        } catch (IOException e) {
            System.out.println("Cant load dialog");
            e.printStackTrace();
            return;
        }
        newTransactionController.getDialogPane().getButtonTypes().add(ButtonType.OK);
        newTransactionController.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = newTransactionController.showAndWait();
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
