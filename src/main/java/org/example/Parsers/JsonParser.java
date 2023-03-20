package org.example.Parsers;

import com.google.gson.Gson;
import org.example.Entities.Credential;

import java.util.ArrayList;

public class JsonParser {

    private final Gson gson;


    public JsonParser() {
        this.gson = new Gson();
    }

    public Credential parseCredential(String credentialString) {
        return this.gson.fromJson(credentialString, Credential.class);
    }

    public ArrayList<Credential> parseCredentialArray(String credentialArrayString) {
        ArrayList<Credential> credentials = new ArrayList<>();
        credentialArrayString = credentialArrayString.replace("[", "").replace("]", "").replace("},{", "};{");
        String[] credentialStrings = credentialArrayString.split(";");
        for (String s : credentialStrings) {
            credentials.add(this.gson.fromJson(s, Credential.class));
        }
        return credentials;
    }
}
