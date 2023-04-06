package org.example;

import org.example.Entities.Credential;
import org.example.Services.TransactionService;
import org.example.Services.UserService;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.sdk.User;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Optional;

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
    public boolean credentialExists(String username, String credentialID) throws Exception {
        return this.transactionService.credentialExists(username, credentialID);
    }

    public Credential createCredential(String username, String credentialName, String credentialValue) throws Exception {
        return this.transactionService.createCredential(username, credentialName, credentialValue);
    }

    public Credential readCredential(String username, String credentialID) throws Exception {
        return this.transactionService.readCredential(username, credentialID);
    }

    public Credential updateCredential(String username, String credentialID, String newCredentialName, String newCredentialValue) throws Exception {
        return this.transactionService.updateCredential(username, credentialID, newCredentialName, newCredentialValue);
    }

    public void deleteCredential(String username, String credentialID) throws Exception {
        this.transactionService.deleteCredential(username, credentialID);
    }

    public ArrayList<Credential> getAllCredentials(String username) throws Exception {
        return this.transactionService.getAllCredentials(username);
    }

    // User related methods
    public void getUserWithUsername(String username) throws Exception {
        Optional<User> optionalUser = this.userService.getUser(username);
        if (optionalUser.isEmpty()) {
            this.userService.enrollUser(username);
        }
    }

    public void deleteUser(String userID) throws Exception {
        this.userService.deleteUser(userID);
    }

}

