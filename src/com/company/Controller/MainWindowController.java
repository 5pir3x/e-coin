package com.company.Controller;

import com.company.Model.Transaction;
import com.company.Model.TransactionFX;
import com.company.ServiceData.BlockData;
import com.company.ServiceData.WalletData;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

public class MainWindowController {

    @FXML
    public TableView tableview = new TableView<>(); //this is read-only UI table
    @FXML
    private TableColumn<Transaction, String> from;
    @FXML
    private TableColumn<Transaction, String> to;
    @FXML
    private TableColumn<Transaction, Integer> value;
    @FXML
    private TableColumn<Transaction, String> timestamp;
    @FXML
    private TableColumn<Transaction, String> signature;
    @FXML
    private BorderPane borderPane;
    @FXML
    public TextField eCoins;
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

    public void setTableview(ObservableList<TransactionFX> items,String balance) {
        tableview.setItems(items);
        tableview.getSelectionModel().select(0);
//        eCoins.setText(balance);
    }

    public void initialize() {
        Base64.Encoder encoder = Base64.getEncoder();
        from.setCellValueFactory(
                new PropertyValueFactory<>("from"));
        to.setCellValueFactory(
                new PropertyValueFactory<>("to"));
        value.setCellValueFactory(
                new PropertyValueFactory<>("value"));
        signature.setCellValueFactory(
                new PropertyValueFactory<>("signature"));
        timestamp.setCellValueFactory(
                new PropertyValueFactory<>("timestamp"));
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
        eCoins.setText(WalletData.getInstance().getWalletBalanceFX(BlockData.getInstance().getCurrentBlockChain()));
    }

    @FXML
    public void handleExit() {
        Platform.setImplicitExit(false);
        Platform.exit();
    }
}
