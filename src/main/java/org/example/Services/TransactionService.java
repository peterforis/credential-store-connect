package org.example.Services;

import org.example.Entities.Credential;
import org.example.Parsers.JsonParser;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Wallet;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.UUID;

public class TransactionService {

    private final JsonParser jsonParser;
    private final Wallet wallet;
    private final String networkConfigPath;
    private final String channelName;
    private final String chaincodeName;

    public TransactionService(Wallet wallet, String networkConfigPath, String channelName, String chaincodeName) {
        this.jsonParser = new JsonParser();
        this.wallet = wallet;
        this.networkConfigPath = networkConfigPath;
        this.channelName = channelName;
        this.chaincodeName = chaincodeName;
    }

    public Contract getContract(String username) throws Exception {
        return Gateway.createBuilder()
                .identity(this.wallet, username)
                .networkConfig(Paths.get(this.networkConfigPath))
                .discovery(true)
                .connect()
                .getNetwork(this.channelName)
                .getContract(this.chaincodeName);
    }

    public boolean credentialExists(String username, String credentialID) throws Exception {
        String result = new String(getContract(username).evaluateTransaction("CredentialExists", credentialID));

        return result.equals("true");
    }

    public Credential createCredential(String username, String credentialName, String credentialValue) throws Exception {
        String credentialID;
        do {
            credentialID = UUID.randomUUID().toString();
        } while (credentialExists(username, credentialID));

        String result = new String(getContract(username).submitTransaction("CreateCredential", credentialID, credentialName, username, credentialValue));

        return this.jsonParser.parseCredential(result);
    }

    public Credential readCredential(String username, String credentialID) throws Exception {
        String result = new String(getContract(username).evaluateTransaction("ReadCredential", credentialID));

        return this.jsonParser.parseCredential(result);
    }

    public Credential updateCredential(String username, String credentialID, String credentialName, String newCredentialValue) throws Exception {
        String result = new String(getContract(username).submitTransaction("UpdateCredential", credentialID, credentialName, username, newCredentialValue));

        return this.jsonParser.parseCredential(result);
    }

    public void deleteCredential(String username, String credentialID) throws Exception {
        getContract(username).submitTransaction("DeleteCredential", credentialID);
    }

    public ArrayList<Credential> getAllCredentials(String username) throws Exception {
        String result = new String(getContract(username).evaluateTransaction("GetAllCredentials"));

        return this.jsonParser.parseCredentialArray(result);
    }
}
