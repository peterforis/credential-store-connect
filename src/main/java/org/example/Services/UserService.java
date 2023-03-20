package org.example.Services;

import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.HFCAIdentity;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Properties;
import java.util.Set;

public class UserService {

    private final HFCAClient hfcaClient;

    private final Wallet wallet;

    private final String mspID;
    private final String affiliation;

    public UserService(String pemPath, String hfcaClientEndpoint, Wallet wallet, String mspID, String affiliation) throws Exception {
        Properties props = new Properties();
        props.put("pemFile", pemPath);
        props.put("allowAllHostNames", "true");

        HFCAClient hfcaClient = HFCAClient.createNewInstance(hfcaClientEndpoint, props);
        CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
        hfcaClient.setCryptoSuite(cryptoSuite);

        this.hfcaClient = hfcaClient;
        this.wallet = wallet;

        this.mspID = mspID;
        this.affiliation = affiliation;

        enrollAdmin();
    }

    public void enrollAdmin() throws Exception {
        if (this.wallet.get("admin") != null) {
            System.out.println("An identity for the admin user \"admin\" already exists in the wallet");
            return;
        }

        final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
        enrollmentRequestTLS.addHost("localhost");
        enrollmentRequestTLS.setProfile("tls");
        Enrollment enrollment = this.hfcaClient.enroll("admin", "adminpw", enrollmentRequestTLS);
        Identity user = Identities.newX509Identity("Org1MSP", enrollment);
        this.wallet.put("admin", user);
        System.out.println("Successfully enrolled user \"admin\" and imported it into the wallet");
    }

    public User getAdmin() throws Exception {
        X509Identity adminIdentity = (X509Identity) this.wallet.get("admin");
        if (adminIdentity == null) {
            throw new Exception("User admin needs to be enrolled and added to the wallet first");
        }
        return new User() {
            @Override
            public String getName() {
                return "admin";
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return affiliation;
            }

            @Override
            public Enrollment getEnrollment() {
                return new Enrollment() {
                    @Override
                    public PrivateKey getKey() {
                        return adminIdentity.getPrivateKey();
                    }

                    @Override
                    public String getCert() {
                        return Identities.toPemString(adminIdentity.getCertificate());
                    }
                };
            }

            @Override
            public String getMspId() {
                return mspID;
            }
        };
    }

    public void deleteUser(String userID) throws Exception {
        this.wallet.remove(userID);

        User admin = getAdmin();
        this.hfcaClient.revoke(admin, userID, "Revoked by admin");
    }

    public User getUser(String userName) throws Exception {
        X509Identity userIdentity = (X509Identity) this.wallet.get(userName);
        if (userIdentity != null) {
            return createUserWithParams(userName, userIdentity);
        }
        return null;
    }

    public User enrollUser(String userName) throws Exception {
        User admin = getAdmin();

        if (this.wallet.get(userName) != null) {
            throw new Exception("An identity for the user " + userName + " already exists in the wallet");
        }

        ArrayList<String> hfcaIdentitiesString = listIdentities(admin);
        if (hfcaIdentitiesString.contains(userName)) {
            throw new Exception("An identity for the user " + userName + " already exists in the hfcaClient");
        }

        RegistrationRequest registrationRequest = new RegistrationRequest(userName);
        registrationRequest.setAffiliation(this.affiliation);
        registrationRequest.setEnrollmentID(userName);
        String enrollmentSecret = this.hfcaClient.register(registrationRequest, admin);
        Enrollment enrollment = this.hfcaClient.enroll(userName, enrollmentSecret);
        X509Identity userIdentity = Identities.newX509Identity(this.mspID, enrollment);

        this.wallet.put(userName, userIdentity);
        System.out.println("Successfully enrolled user " + userName + " and imported it into the wallet");

        return createUserWithParams(userName, userIdentity);
    }

    private User createUserWithParams(String userName, X509Identity userIdentity) {
        return new User() {
            @Override
            public String getName() {
                return userName;
            }

            @Override
            public Set<String> getRoles() {
                return null;
            }

            @Override
            public String getAccount() {
                return null;
            }

            @Override
            public String getAffiliation() {
                return affiliation;
            }

            @Override
            public Enrollment getEnrollment() {
                return new Enrollment() {
                    @Override
                    public PrivateKey getKey() {
                        return userIdentity.getPrivateKey();
                    }

                    @Override
                    public String getCert() {
                        return Identities.toPemString(userIdentity.getCertificate());
                    }
                };
            }

            @Override
            public String getMspId() {
                return mspID;
            }
        };
    }

    private ArrayList<String> listIdentities(User admin) throws Exception {
        ArrayList<String> hfcaIdentitiesString = new ArrayList<>();

        Collection<HFCAIdentity> hfcaIdentities = this.hfcaClient.getHFCAIdentities(admin);
        for (HFCAIdentity hfcaIdentity : hfcaIdentities) {
            hfcaIdentitiesString.add(hfcaIdentity.getEnrollmentId());
            System.out.println("IDENTITY FOUND: " + hfcaIdentity.getEnrollmentId());
        }
        return hfcaIdentitiesString;
    }
}
