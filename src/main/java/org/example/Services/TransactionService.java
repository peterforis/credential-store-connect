package org.example.Services;

import org.example.Entities.Credential;
import org.example.Parsers.JsonParser;
import org.hyperledger.fabric.gateway.Contract;

import java.util.ArrayList;

public class TransactionService {

    private final Contract contract;

    private final JsonParser jsonParser;

    public TransactionService(Contract contract) {
        this.contract = contract;
        this.jsonParser = new JsonParser();
    }

    public void initLedger() throws Exception {
        System.out.println("[===> SubmitTransaction: InitLedger]");
        this.contract.submitTransaction("InitLedger");
    }

    public boolean credentialExists(String credentialID) throws Exception {
        System.out.println("[===> EvaluateTransaction: CredentialExists " + credentialID + "]");
        String result = new String(this.contract.evaluateTransaction("CredentialExists", credentialID));
        System.out.println("[result: " + result + "]\n");

        return result.equals("true");
    }

    public Credential createCredential(String credentialID, String credentialName, String credentialOwner, String credentialValue) throws Exception {
        System.out.println("[===> SubmitTransaction: CreateCredential " + credentialID + " with name " + credentialName + " with value " + credentialValue + "]");
        String result = new String(this.contract.submitTransaction("CreateCredential", credentialID, credentialName, credentialOwner, credentialValue));
        System.out.println("[result: " + result + "]\n");
        //todo encode password using key, here or in spring?
        return this.jsonParser.parseCredential(result);
    }

    public Credential readCredential(String credentialID) throws Exception {
        System.out.println("[===> EvaluateTransaction: ReadCredential " + credentialID + "]");
        String result = new String(this.contract.evaluateTransaction("ReadCredential", credentialID));
        System.out.println("[result: " + result + "]\n");

        return this.jsonParser.parseCredential(result);
    }

    public Credential updateCredential(String credentialID, String credentialName, String credentialOwner, String newCredentialValue) throws Exception {
        System.out.println("[===> SubmitTransaction: UpdateCredential " + credentialID + " with name " + credentialName + " new CredentialValue: " + newCredentialValue + "]");
        String result = new String(this.contract.submitTransaction("UpdateCredential", credentialID, credentialName, credentialOwner, newCredentialValue));
        System.out.println("[result: " + result + "]\n");

        return this.jsonParser.parseCredential(result);
    }

    public void deleteCredential(String credentialID) throws Exception {
        System.out.println("[===> SubmitTransaction: DeleteCredential " + credentialID + "]\n");
        this.contract.submitTransaction("DeleteCredential", credentialID);
    }

    public ArrayList<Credential> getAllCredentials() throws Exception {
        System.out.println("[===> EvaluateTransaction: GetAllCredentials]");
        String result = new String(this.contract.evaluateTransaction("GetAllCredentials"));
        System.out.println("Evaluate Transaction: GetAllCredentials, result:\n" + result + "]\n");

        return this.jsonParser.parseCredentialArray(result);
    }
}
