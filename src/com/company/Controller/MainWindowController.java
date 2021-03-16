package com.company.Controller;

import com.company.ServiceData.BlockData;
import com.company.Model.Transaction;
import com.company.ServiceData.WalletData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Optional;

public class MainWindowController {

    @FXML
    public TableView<Transaction> tableview = new TableView<>(); //this is read-only UI table
    @FXML
    private TableColumn<byte[],byte[]> from;
    @FXML
    private TableColumn<Transaction,byte[]> to;
    @FXML
    private TableColumn<Transaction,Integer> value;
    @FXML
    private TableColumn<Transaction,byte[]> signature;
    @FXML
    private BorderPane borderPane;
    @FXML
    public static int index;
    @FXML
    private TextField eCoins ;
    @FXML
    private TextArea publicKey;

    public void initialize() throws NoSuchAlgorithmException, SQLException, InvalidKeySpecException, UnsupportedEncodingException {

        from.setCellValueFactory(
                new PropertyValueFactory<>("from"));
        to.setCellValueFactory(
                new PropertyValueFactory<>("to"));
        value.setCellValueFactory(
                new PropertyValueFactory<Transaction, Integer>("value"));
        signature.setCellValueFactory(
                new PropertyValueFactory<>("signature"));

        Base64.Encoder encoder = Base64.getEncoder();
        eCoins.setText(WalletData.getInstance().getWalletBalanceFX(BlockData.getInstance().getCurrentBlockChain()));
        publicKey.setText(encoder.encodeToString(WalletData.getInstance().getWallet().getPublicKey().getEncoded()));
        tableview.setItems(BlockData.getInstance().getTransactionLedgerFX());
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
        if (result.isPresent() ) {
            tableview.setItems(BlockData.getInstance().getTransactionLedgerFX());
            eCoins.setText(BlockData.getInstance().getWalletBallanceFX());
        }
    }
    @FXML
    public void mineBlock() {
        BlockData.getInstance().mineBlock();
        tableview.setItems(BlockData.getInstance().getTransactionLedgerFX());
        tableview.getSelectionModel().select(0);
        eCoins.setText(BlockData.getInstance().getWalletBallanceFX());
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
