package org.example.Connection;

import io.grpc.ManagedChannel;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import org.hyperledger.fabric.client.identity.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.cert.CertificateException;

public class Connection {

    private String mspId;
    private Path tlsCertificatePath;
    private String peerEndpoint;
    private Path certificatePath;
    private Path keyDirectoryPath;
    private String overrideAuth;

    public Connection(String mspId, Path tlsCertificatePath, String peerEndpoint, Path certificatePath, Path keyDirectoryPath, String overrideAuth) {
        this.mspId = mspId;
        this.tlsCertificatePath = tlsCertificatePath;
        this.peerEndpoint = peerEndpoint;
        this.certificatePath = certificatePath;
        this.keyDirectoryPath = keyDirectoryPath;
        this.overrideAuth = overrideAuth;
    }

    public ManagedChannel newGrpcConnection() throws IOException, CertificateException {
        var tlsCertReader = Files.newBufferedReader(tlsCertificatePath);
        var tlsCert = Identities.readX509Certificate(tlsCertReader);

        return NettyChannelBuilder.forTarget(peerEndpoint)
                .sslContext(GrpcSslContexts.forClient().trustManager(tlsCert).build()).overrideAuthority(overrideAuth)
                .build();
    }

    public Identity newIdentity() throws IOException, CertificateException {
        var certReader = Files.newBufferedReader(certificatePath);
        var certificate = Identities.readX509Certificate(certReader);

        return new X509Identity(mspId, certificate);
    }

    public Signer newSigner() throws IOException, InvalidKeyException {
        var keyReader = Files.newBufferedReader(getPrivateKeyPath());
        var privateKey = Identities.readPrivateKey(keyReader);

        return Signers.newPrivateKeySigner(privateKey);
    }

    public Path getPrivateKeyPath() throws IOException {
        try (var keyFiles = Files.list(keyDirectoryPath)) {
            return keyFiles.findFirst().orElseThrow();
        }
    }
}
