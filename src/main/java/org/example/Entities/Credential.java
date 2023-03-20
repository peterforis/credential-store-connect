package org.example.Entities;

public class Credential {
    private final String credentialID;
    private final String credentialName;
    private final String credentialOwner;
    private final String credentialValue;

    public Credential(String credentialID, String credentialName, String credentialOwner, String credentialValue) {
        this.credentialID = credentialID;
        this.credentialName = credentialName;
        this.credentialOwner = credentialOwner;
        this.credentialValue = credentialValue;
    }

    @Override
    public String toString() {
        return "Credential{" +
                "credentialID='" + credentialID + '\'' +
                ", credentialName='" + credentialName + '\'' +
                ", credentialOwner='" + credentialOwner + '\'' +
                ", credentialValue='" + credentialValue + '\'' +
                '}';
    }
}
