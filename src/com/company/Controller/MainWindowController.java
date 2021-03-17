package com.company.Controller;

import com.company.Model.TransactionFX;
import com.company.ServiceData.BlockData;
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
    public TableView<TransactionFX> tableview = new TableView<>(); //this is read-only UI table
    @FXML
    private TableColumn<TransactionFX,byte[]> from;
    @FXML
    private TableColumn<TransactionFX,byte[]> to;
    @FXML
    private TableColumn<TransactionFX,Integer> value;
    @FXML
    private TableColumn<TransactionFX,byte[]> signature;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField eCoins ;
    @FXML
    private TextArea publicKey;

    public void initialize() throws NoSuchAlgorithmException, SQLException, InvalidKeySpecException, UnsupportedEncodingException {
        Base64.Encoder encoder = Base64.getEncoder();
        from.setCellValueFactory(
                new PropertyValueFactory<>("from"));
        to.setCellValueFactory(
                new PropertyValueFactory<>("to"));
        value.setCellValueFactory(
                new PropertyValueFactory<>("value"));
        signature.setCellValueFactory(
                new PropertyValueFactory<>("signature"));


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


    @FXML
    public void handleExit() {
        Platform.exit();
    }
}
