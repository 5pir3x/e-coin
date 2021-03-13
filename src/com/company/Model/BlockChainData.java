package com.company.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.security.NoSuchAlgorithmException;
import java.sql.*;
import java.util.ArrayList;

public class BlockChainData {

    private ObservableList<Block> currentBlockChain;
    //singleton class
    private static BlockChainData instance;
    static {
        try {
            instance = new BlockChainData();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public BlockChainData() throws NoSuchAlgorithmException {
        currentBlockChain = FXCollections.observableArrayList();
    }

    public static BlockChainData getInstance(){
        return instance;
    }
    public ObservableList<Transaction> getTransactionLedger() {
        return FXCollections.observableArrayList(currentBlockChain.get(currentBlockChain.size()-1).getTransactionLedger());
    }

    public void loadBlockChain() {
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");
            Statement stmt = connection.createStatement();
            ResultSet resultSet = stmt.executeQuery(" SELECT  * FROM BLOCKCHAIN ");
            while (resultSet.next()) {
                ArrayList<Transaction> transactionLedger = loadTransactionLedger(resultSet.getInt("LEDGER_ID"));
                currentBlockChain.add(new Block(
                        resultSet.getString("PREVIOUS_HASH").getBytes(),
                        resultSet.getString("CURRENT_HASH").getBytes(),
                        resultSet.getString("CREATED_ON"),
                        resultSet.getString("CREATED_BY").getBytes(),
                        transactionLedger
                ));
            }
            resultSet.close();
            stmt.close();
            connection.close();
        }
        catch (SQLException | NoSuchAlgorithmException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private ArrayList<Transaction> loadTransactionLedger(Integer ledgerID) throws SQLException {
        ArrayList<Transaction> transactions = new ArrayList<>();
        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");
            PreparedStatement stmt = connection.prepareStatement(" SELECT  * FROM TRANSACTIONS WHERE LEDGER_ID = ?");

            stmt.setInt(1,ledgerID);
            ResultSet resultSet = stmt.executeQuery();
            while (resultSet.next()) {
                transactions.add(new Transaction(
                        resultSet.getString("FROM").getBytes(),
                        resultSet.getString("TO").getBytes(),
                        resultSet.getInt("VALUE"),
                        resultSet.getString("SIGNATURE").getBytes()
                ));
            }
            resultSet.close();
            stmt.close();
            connection.close();
        } catch (SQLException | NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return transactions;
    }

    //todo:Outstanding for impl
    public void saveBlockChain() {

        try {
            Connection connection = DriverManager.getConnection("jdbc:sqlite:C:\\Users\\spiro\\IdeaProjects\\e-coin\\db\\ecointest2.db");
            Statement stmt = connection.createStatement();
            stmt.executeQuery("");

            stmt.close();
            connection.close();

        }
        catch (SQLException e) {
            System.out.println("Problem with DB: " + e.getMessage());
            e.printStackTrace();
        }

    }

}
