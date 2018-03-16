package org.mule.tooling.platform;

public class PlatformUser {

    private String token;

    public PlatformUser(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
