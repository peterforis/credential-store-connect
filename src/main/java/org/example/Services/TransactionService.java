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

    public Contract getContract(String userName) throws Exception {
        return Gateway.createBuilder()
                .identity(this.wallet, userName)
                .networkConfig(Paths.get(this.networkConfigPath))
                .discovery(true)
                .connect()
                .getNetwork(this.channelName)
                .getContract(this.chaincodeName);
    }

    public void initLedger(String userName) throws Exception {
        System.out.println("[===> SubmitTransaction: InitLedger]");
        getContract(userName).submitTransaction("InitLedger");
    }

    public boolean credentialExists(String userName, String credentialID) throws Exception {
        System.out.println("[===> EvaluateTransaction: CredentialExists " + credentialID + "]");
        String result = new String(getContract(userName).evaluateTransaction("CredentialExists", credentialID));
        System.out.println("[result: " + result + "]\n");

        return result.equals("true");
    }

    public Credential createCredential(String userName, String credentialName, String credentialValue) throws Exception {
        String credentialID;
        do {
            credentialID = UUID.randomUUID().toString();
        } while (credentialExists(userName, credentialID));

        System.out.println("[===> SubmitTransaction: CreateCredential " + credentialID + " with name " + credentialName + " with value " + credentialValue + "]");
        String result = new String(getContract(userName).submitTransaction("CreateCredential", credentialID, credentialName, userName, credentialValue));
        System.out.println("[result: " + result + "]\n");

        return this.jsonParser.parseCredential(result);
    }

    public Credential readCredential(String userName, String credentialID) throws Exception {
        System.out.println("[===> EvaluateTransaction: ReadCredential " + credentialID + "]");
        String result = new String(getContract(userName).evaluateTransaction("ReadCredential", credentialID));
        System.out.println("[result: " + result + "]\n");

        return this.jsonParser.parseCredential(result);
    }

    public Credential updateCredential(String userName, String credentialID, String credentialName, String newCredentialValue) throws Exception {
        System.out.println("[===> SubmitTransaction: UpdateCredential " + credentialID + " with name " + credentialName + " new CredentialValue: " + newCredentialValue + "]");
        String result = new String(getContract(userName).submitTransaction("UpdateCredential", credentialID, credentialName, userName, newCredentialValue));
        System.out.println("[result: " + result + "]\n");

        return this.jsonParser.parseCredential(result);
    }

    public void deleteCredential(String userName, String credentialID) throws Exception {
        System.out.println("[===> SubmitTransaction: DeleteCredential " + credentialID + "]\n");
        getContract(userName).submitTransaction("DeleteCredential", credentialID);
    }

    public ArrayList<Credential> getAllCredentials(String userName) throws Exception {
        System.out.println("[===> EvaluateTransaction: GetAllCredentials]");
        String result = new String(getContract(userName).evaluateTransaction("GetAllCredentials"));
        System.out.println("Evaluate Transaction: GetAllCredentials, result:\n" + result + "]\n");

        return this.jsonParser.parseCredentialArray(result);
    }
}
