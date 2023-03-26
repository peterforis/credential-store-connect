package org.example;

import org.example.Entities.Credential;
import org.example.Services.TransactionService;
import org.example.Services.UserService;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.sdk.User;

import java.nio.file.Paths;
import java.util.ArrayList;

public class CredentialStoreConnect {

    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    private final UserService userService;
    private final TransactionService transactionService;

    public CredentialStoreConnect(
            String walletPath,
            String pemPath,
            String hfcaClientEndpoint,
            String mspID,
            String affiliation,
            String chaincodeName,
            String networkConfigPath,
            String channelName
    ) throws Exception {
        Wallet wallet = Wallets.newFileSystemWallet(Paths.get(walletPath));
        this.userService = new UserService(pemPath, hfcaClientEndpoint, wallet, mspID, affiliation);
        this.transactionService = new TransactionService(
                wallet,
                networkConfigPath,
                channelName,
                chaincodeName
        );
    }

    // Transaction related methods
    private void validate() throws Exception {
        if (this.transactionService == null) {
            throw new Exception("TransactionService is not initialized");
        }
    }

    public void initializeLedger(String userName) throws Exception {
        validate();
        this.transactionService.initLedger(userName);
    }

    public boolean credentialExists(String userName, String credentialID) throws Exception {
        validate();
        return this.transactionService.credentialExists(userName, credentialID);
    }

    public Credential createCredential(String userName, String credentialName, String credentialValue) throws Exception {
        validate();
        return this.transactionService.createCredential(userName, credentialName, credentialValue);
    }

    public Credential readCredential(String userName, String credentialID) throws Exception {
        validate();
        return this.transactionService.readCredential(userName, credentialID);
    }

    public Credential updateCredential(String userName, String credentialID, String credentialName, String newCredentialValue) throws Exception {
        validate();
        return this.transactionService.updateCredential(userName, credentialID, credentialName, newCredentialValue);
    }

    public void deleteCredential(String userName, String credentialID) throws Exception {
        validate();
        this.transactionService.deleteCredential(userName, credentialID);
    }

    public ArrayList<Credential> getAllCredentials(String userName) throws Exception {
        validate();
        return this.transactionService.getAllCredentials(userName);
    }

    // User related methods
    public User createUser(String userName) throws Exception {
        return this.userService.enrollUser(userName);
    }

    public void deleteUser(String userID) throws Exception {
        this.userService.deleteUser(userID);
    }

}

