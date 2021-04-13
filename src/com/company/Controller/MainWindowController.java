package com.company.Controller;

import com.company.Model.Transaction;
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
    public TableView tableview = new TableView<>(); //this is read-only UI table
    @FXML
    private TableColumn from;
    @FXML
    private TableColumn to;
    @FXML
    private TableColumn value;
    @FXML
    private TableColumn timestamp;
    @FXML
    private TableColumn signature;
    @FXML
    private BorderPane borderPane;
    @FXML
    private TextField eCoins ;
    @FXML
    private TextArea publicKey;

    //singleton class
    private static MainWindowController instance;

    static {
        instance = new MainWindowController();
    }
    public static MainWindowController getInstance() {
        return instance;
    }

    public TableView getTableview() { return tableview; }
    public void setTableview(TableView tableview) { this.tableview = tableview; }

    public void initialize() throws NoSuchAlgorithmException, SQLException, InvalidKeySpecException, UnsupportedEncodingException {
        Base64.Encoder encoder = Base64.getEncoder();
        from.setCellValueFactory(
                new PropertyValueFactory<Transaction,String>("from"));
        to.setCellValueFactory(
                new PropertyValueFactory<Transaction,String>("to"));
        value.setCellValueFactory(
                new PropertyValueFactory<Transaction,Integer>("value"));
        signature.setCellValueFactory(
                new PropertyValueFactory<Transaction,String>("signature"));
        timestamp.setCellValueFactory(
                new PropertyValueFactory<Transaction,String>("timestamp"));


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
