package com.company.Controller;

import com.company.Model.BlockData;
import com.company.Model.Transaction;
import com.company.Model.WalletData;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Optional;

public class MainWindowController {

    @FXML
    public TableView tableview = new TableView(); //this is read-only UI table
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
    private TextField eCoins ;
    @FXML
    private TextArea publicKey;

    public void initialize() throws NoSuchAlgorithmException, SQLException, InvalidKeySpecException {

        from.setCellValueFactory(
                new PropertyValueFactory<Transaction, String>("from"));
        to.setCellValueFactory(
                new PropertyValueFactory<Transaction, String>("to"));
        value.setCellValueFactory(
                new PropertyValueFactory<Transaction, Integer>("value"));
        signature.setCellValueFactory(
                new PropertyValueFactory<Transaction, String>("signature"));
//        eCoins.setItems(WalletData.getInstance().getWalletBalanceFX());

        eCoins.setText(WalletData.getInstance().getWalletBalanceFX(BlockData.getInstance().getCurrentBlockChain()));
        publicKey.setText(Arrays.toString(WalletData.getInstance().getWallet().getPublicKey().getEncoded()));
        ObservableList<Transaction> tl = BlockData.getInstance().getTransactionLedger();
        tableview.setItems(BlockData.getInstance().getTransactionLedger());
        tableview.getSelectionModel().select(0);

    }

    @FXML
    public void toNewTransactionController() {
        Dialog<ButtonType> newTransactionController = new Dialog<>();
        newTransactionController.initOwner(borderPane.getScene().getWindow());
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("../View/AddNewTransactionWindow.fxml"));
        try {
            newTransactionController.getDialogPane().setContent(fxmlLoader.load());
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
    public void mineBlock() {
        BlockData.getInstance().mineBlock();
        tableview.setItems(BlockData.getInstance().getTransactionLedger());
        tableview.getSelectionModel().select(0);
        eCoins.setText(WalletData.getInstance().getWalletBalanceFX(BlockData.getInstance().getCurrentBlockChain()));
    }


//    @FXML
//    public void editSelectedItem() {
//        index = tableview.getSelectionModel().getSelectedIndex();
//        Dialog<ButtonType> newContactController = new Dialog<>();
//        newContactController.initOwner(borderPane.getScene().getWindow());
//        FXMLLoader fxmlLoader = new FXMLLoader();
//        fxmlLoader.setLocation(getClass().getResource("EditNewContactWindow.fxml"));
//        try {
//            Parent root = FXMLLoader.load(getClass().getResource("AddNewContactWindow.fxml"));
//            newContactController.getDialogPane().setContent(fxmlLoader.load());
//        } catch (IOException e) {
//            System.out.println("Cant load dialog");
//            e.printStackTrace();
//            return;
//        }
//        newContactController.getDialogPane().getButtonTypes().add(ButtonType.OK);
//        newContactController.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
//
//        newContactController.showAndWait();
//
//
//
//    }
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
