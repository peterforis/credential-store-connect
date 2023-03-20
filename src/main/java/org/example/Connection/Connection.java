package org.example.Connection;

import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Wallet;

import java.nio.file.Paths;

public class Connection {
    private final Wallet wallet;
    private final String userName;
    private final String networkConfigPath;

    public Connection(Wallet wallet, String userName, String networkConfigPath) {
        this.wallet = wallet;
        this.userName = userName;
        this.networkConfigPath = networkConfigPath;
    }

    public Gateway getGateway() throws Exception {
        Gateway.Builder builder = Gateway.createBuilder();
        builder.identity(this.wallet, this.userName)
                .networkConfig(Paths.get(this.networkConfigPath))
                .discovery(true);
        return builder.connect();
    }
}
