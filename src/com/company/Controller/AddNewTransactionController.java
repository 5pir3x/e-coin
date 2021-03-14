package com.company.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class AddNewTransactionController {

    @FXML
    public Button addContact;
    @FXML
    private TextField firstName;
    @FXML
    private TextField secondName;
    @FXML
    private TextField phoneNumber;
    @FXML
    private TextField notes;


    public void setFirstName(String firstName) {
        this.firstName.setText(firstName);
    }

    public void setSecondName(String secondName) {
        this.secondName.setText(secondName);
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber.setText(phoneNumber);
    }

    public void setNotes(String notes) {
        this.notes.setText(notes);
    }

    @FXML
    public void createNewTransaction(){
//        Contact contact = new Contact(firstName.getText(),secondName.getText(),phoneNumber.getText(),notes.getText());
//        ContactData.getInstance().addContact(contact);
    }
}
