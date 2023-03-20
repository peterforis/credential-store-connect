package org.example;

import org.example.Entities.Credential;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

public class Main {
    private Properties properties;

    public static void main(String[] args) {
        try {
            new Main().run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void run() throws Exception {

//        boolean reset = true;
//        if (reset) {
//            FileUtils.deleteDirectory(new File("wallet"));
//            return;
//        }

        if (!loadConfig()) {
            return;
        }

        final String channelName = properties.getProperty("CHANNEL_NAME");
        final String chaincodeName = properties.getProperty("CHAINCODE_NAME");
        final String networkConfigPath = properties.getProperty("NETWORK_CONFIG_PATH");

        final String mspID = properties.getProperty("MSP_ID");
        final String walletPath = properties.getProperty("WALLET_PATH");
        final String pemPath = properties.getProperty("PEM_PATH");
        final String hfcaClientEndpoint = properties.getProperty("HFCA_CLIENT_ENDPOINT");
        final String affiliation = properties.getProperty("AFFILIATION");


        System.out.println("walletPath " + walletPath +
                "\npemPath " + pemPath +
                "\nhfcaClientEndpoint " + hfcaClientEndpoint +
                "\nmspID " + mspID +
                "\naffiliation " + affiliation +
                "\nchaincodeName " + chaincodeName +
                "\nnetworkConfigPath " + networkConfigPath +
                "\nchannelName " + channelName);

        CredentialStoreConnect credentialStoreConnect = new CredentialStoreConnect(
                walletPath,
                pemPath,
                hfcaClientEndpoint,
                mspID,
                affiliation,
                chaincodeName,
                networkConfigPath,
                channelName
        );

        String credentialID = "credential103";
        credentialStoreConnect.createUser("newUser32");
//        credentialStoreConnect.initializeLedger();
        boolean credentialExists = credentialStoreConnect.credentialExists(credentialID);
        System.out.println("credentialExists" + credentialExists);
        Credential createCredential =  credentialStoreConnect.createCredential(credentialID, "credential-name", "value101");
        System.out.println("createCredential" + createCredential.toString());
        Credential readCredential = credentialStoreConnect.readCredential(credentialID);
        System.out.println("readCredential" + readCredential.toString());
        Credential updateCredential = credentialStoreConnect.updateCredential(credentialID, "credential-name","value101 new value");
        System.out.println("updateCredential" + updateCredential.toString());
        Credential readCredential2 = credentialStoreConnect.readCredential(credentialID);
        System.out.println("readCredential2" + readCredential2.toString());
        credentialStoreConnect.deleteCredential(credentialID);
        ArrayList<Credential> credentials = credentialStoreConnect.getAllCredentials();
        for(Credential c: credentials){
            System.out.println("credentials n: " + c.toString());
        }
    }

    public boolean loadConfig() {
        Properties properties = new Properties();
        try {
            FileInputStream fileInputStream = new FileInputStream("./config.properties");
            properties.load(fileInputStream);
            this.properties = properties;
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}