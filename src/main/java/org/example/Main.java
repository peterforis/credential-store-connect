package org.example;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.grpc.ManagedChannel;
import org.example.Connection.Connection;
import org.example.Services.TransactionService;
import org.hyperledger.fabric.client.Contract;
import org.hyperledger.fabric.client.Gateway;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

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
        if (!loadConfig()) {
            return;
        }

        final String mspId = properties.getProperty("MSP_ID");
        final String channelName = properties.getProperty("CHANNEL_NAME");
        final String chaincodeName = properties.getProperty("CHAINCODE_NAME");
//        final Path cryptoPath = Paths.get(properties.getProperty("CRYPTO_PATH"));
        final Path certificatePath = Paths.get(properties.getProperty("CERT_PATH"));
        final Path keyDirectoryPath = Paths.get(properties.getProperty("KEY_DIR_PATH"));
        final Path tlsCertificatePath = Paths.get(properties.getProperty("TLS_CERT_PATH"));
        final String peerEndpoint = properties.getProperty("PEER_ENDPOINT");
        final String overrideAuth = properties.getProperty("OVERRIDE_AUTH");

        Contract contract;
//        final String credentialId = "credential" + Instant.now().toEpochMilli();
//        final Gson gson = new GsonBuilder().setPrettyPrinting().create();

        Connection connection = new Connection(
                mspId,
                tlsCertificatePath,
                peerEndpoint,
                certificatePath,
                keyDirectoryPath,
                overrideAuth
        );

        ManagedChannel managedChannel = connection.newGrpcConnection();

        Gateway.Builder builder = Gateway.newInstance().identity(connection.newIdentity()).signer(connection.newSigner()).connection(managedChannel)
                .evaluateOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .endorseOptions(options -> options.withDeadlineAfter(15, TimeUnit.SECONDS))
                .submitOptions(options -> options.withDeadlineAfter(5, TimeUnit.SECONDS))
                .commitStatusOptions(options -> options.withDeadlineAfter(1, TimeUnit.MINUTES));

        try (Gateway gateway = builder.connect()) {
            contract = gateway.getNetwork(channelName).getContract(chaincodeName);
            TransactionService transactionService = new TransactionService(contract);

            // Execute transactions
        } finally {
            managedChannel.shutdownNow().awaitTermination(5, TimeUnit.SECONDS);
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