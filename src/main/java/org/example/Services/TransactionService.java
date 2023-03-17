package org.example.Services;

import org.hyperledger.fabric.client.Contract;

public class TransactionService {

    private final Contract contract;

    public TransactionService(Contract contract) {
        this.contract = contract;
    }

    public void initLedger() throws Exception {
        System.out.println("Submit Transaction: InitLedger creates the initial set of credentials on the ledger.");
        this.contract.submitTransaction("InitLedger");
    }

    public void createCredential(String credentialID, String credentialValue) throws Exception {
        System.out.println("Submit Transaction: CreateCredential " + credentialID + " with value " + credentialValue);
        this.contract.submitTransaction("CreateCredential", credentialID, credentialValue);
    }

    public void getAllCredentials() throws Exception {
        byte[] result = this.contract.evaluateTransaction("GetAllCredentials");
        System.out.println("Evaluate Transaction: GetAllCredentials, result: " + new String(result));
    }

    public void readCredential(String credentialID) throws Exception {
        System.out.println("Evaluate Transaction: ReadCredential " + credentialID);
        byte[] result = this.contract.evaluateTransaction("ReadCredential", credentialID);
        System.out.println("result: " + new String(result));
    }

    public void credentialExists(String credentialID) throws Exception {
        System.out.println("Evaluate Transaction: CredentialExists " + credentialID);
        byte[] result = this.contract.evaluateTransaction("CredentialExists", credentialID);
        System.out.println("result: " + new String(result));
    }

    public void updateCredential(String credentialID, String newCredentialValue) throws Exception {
        System.out.println("Submit Transaction: UpdateCredential credential1, new CredentialValue : " + newCredentialValue);
        this.contract.submitTransaction("UpdateCredential", credentialID, "blue", "5", "Tomoko", newCredentialValue);
    }
}
