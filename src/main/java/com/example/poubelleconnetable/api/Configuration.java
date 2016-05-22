package com.example.poubelleconnetable.api;

/**
 * Contains the client IDs and scopes for allowed clients consuming the
 * helloworld API.
 */
public class Configuration {
    /**
     * Client id of web client
     */
    public static final String WEB_CLIENT_ID =
            "599643511493-otk98584efl01di4hp1rfqdgm3tkn25l.apps.googleusercontent.com";
    /**
     * Client Id of api explorer available at /_ah/api/explorer on production env
     */
    public static final String API_EXPLORER_CLIENT_ID =
            com.google.api.server.spi.Constant.API_EXPLORER_CLIENT_ID;
    /**
     * This scope lets OAuth work with Google Accounts
     */
    public static final String EMAIL_SCOPE =
            "https://www.googleapis.com/auth/userinfo.email";

}
