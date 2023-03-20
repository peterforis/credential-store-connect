package org.example;

import org.example.Connection.Connection;
import org.example.Entities.Credential;
import org.example.Services.TransactionService;
import org.example.Services.UserService;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.sdk.User;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class CredentialStoreConnect {

    // Revisit ?
    static {
        System.setProperty("org.hyperledger.fabric.sdk.service_discovery.as_localhost", "true");
    }

    private UserService userService;
    private User user;
    private TransactionService transactionService;
    private String chaincodeName;
    private String networkConfigPath;
    private String channelName;
    private Wallet wallet;

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
        this.wallet = initializeWallet(Paths.get(walletPath));
        initializeUserService(pemPath, hfcaClientEndpoint, this.wallet, mspID, affiliation);
        this.chaincodeName = chaincodeName;
        this.networkConfigPath = networkConfigPath;
        this.channelName = channelName;
    }

    // Transaction related methods
    private void validate() throws Exception{
        if (this.user == null || this.transactionService == null){
            throw new Exception("User or TransactionService is not initialized");
        }
    }

    public void initializeLedger() throws Exception {
        validate();
        this.transactionService.initLedger();
    }

    public boolean credentialExists(String credentialID) throws Exception {
        validate();
        return this.transactionService.credentialExists(credentialID);
    }

    public Credential createCredential(String credentialID, String credentialName, String credentialValue) throws Exception {
        validate();
        return this.transactionService.createCredential(credentialID, credentialName, this.user.getName(), credentialValue);
    }

    public Credential readCredential(String credentialID) throws Exception {
        validate();
        return this.transactionService.readCredential(credentialID);
    }

    public Credential updateCredential(String credentialID, String credentialName, String newCredentialValue) throws Exception {
        validate();
        return this.transactionService.updateCredential(credentialID, credentialName, this.user.getName(), newCredentialValue);
    }

    public void deleteCredential(String credentialID) throws Exception {
        validate();
        this.transactionService.deleteCredential(credentialID);
    }

    public ArrayList<Credential> getAllCredentials() throws Exception {
        validate();
        return this.transactionService.getAllCredentials();
    }

    // User related methods
    public User createUser(String userName) throws Exception {
        User user = this.userService.enrollUser(userName);
        setUser(user);
        initializeTransactionServiceForUser(this.wallet, userName, this.networkConfigPath, this.channelName, this.chaincodeName);
        return user;
    }

    public void deleteUser(String userID) throws Exception {
        this.userService.deleteUser(userID);
        this.transactionService = null;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    private void setUser(User user) {
        this.user = user;
    }

    // Constructor methods
    private Wallet initializeWallet(Path walletPath) throws Exception {
        return Wallets.newFileSystemWallet(walletPath);
    }

    private void initializeUserService(String pemPath, String hfcaClientEndpoint, Wallet wallet, String mspID, String affiliation) throws Exception {
        this.userService = new UserService(pemPath, hfcaClientEndpoint, wallet, mspID, affiliation);
    }

    private void initializeTransactionServiceForUser(Wallet wallet, String userName, String networkConfigPath, String channelName, String chaincodeName) throws Exception {
        System.out.println("");

        Contract contract = new Connection(wallet, userName, networkConfigPath)
                .getGateway()
                .getNetwork(channelName)
                .getContract(chaincodeName);

        this.transactionService = new TransactionService(contract);
    }
}

