package com.company.Controller;

import com.company.ServiceData.BlockData;
import com.company.Model.Transaction;
import com.company.ServiceData.WalletData;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.security.GeneralSecurityException;
import java.util.Base64;

public class AddNewTransactionController {

    @FXML
    public Button sendTransaction;
//    @FXML
//    private TextField fromAddress;
    @FXML
    private TextField toAddress;
    @FXML
    private TextField value;
    @FXML
    private TextField notes;

    private Integer ledgerId;

//    public void setFromAddress(String fromAddress) {
//        this.fromAddress.setText(fromAddress);
//    }

    public void setToAddress(String toAddress) {
        this.toAddress.setText(toAddress);
    }

    public void setValue(String value) {
        this.value.setText(value);
    }

    public void setNotes(String notes) {
        this.notes.setText(notes);
    }

    @FXML
    public void createNewTransaction() throws GeneralSecurityException {
        if (BlockData.getInstance().getTransactionLedgerFX().isEmpty()) {
            ledgerId = BlockData.getInstance().getLatestBlock().getLedgerId() + 1;
        } else {
            ledgerId = BlockData.getInstance().getTransactionLedgerFX().get(0).getLedgerId();
        }

        Base64.Decoder decoder = Base64.getDecoder();
        byte[] sendB = decoder.decode(toAddress.getText());

        Transaction transaction = new Transaction(WalletData.getInstance().getWallet(),sendB ,Integer.parseInt(value.getText()), ledgerId);
        BlockData.getInstance().addTransaction(transaction);
    }
}
