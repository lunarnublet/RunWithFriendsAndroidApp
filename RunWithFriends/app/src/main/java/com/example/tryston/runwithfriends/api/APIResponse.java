package com.example.tryston.runwithfriends.api;

import java.net.HttpURLConnection;

/**
 * Created by Dallin on 6/1/2017.
 */

public class APIResponse {

    public static final String ALRIGHT = "it worked";
    public static final String FAIL = "Failed to connect to server";

    public enum Code {
        UNKNOWN(1),

        OK(200),
        CREATED(201),

        BAD_REQUEST(400),
        UNAUTHORIZED(401),
        FORBIDDEN(403),
        NOT_FOUND(404);


        private int val;

        Code(int val) {
            this.val = val;
        }

        public static Code fromInt(int val) {
            switch(val) {
                case 200:
                    return OK;
                case 201:
                    return CREATED;
                case 400:
                    return BAD_REQUEST;
                case 401:
                    return UNAUTHORIZED;
                case 403:
                    return FORBIDDEN;
                case 404:
                    return NOT_FOUND;
                default:
                    return UNKNOWN;
            }
        }

        @Override
        public String toString() {
            return Integer.toString(val) + ": " + this.name();
        }

    }
    public final String response;
    public final Code code;
    public final String message;

    public APIResponse(String response, Code code, String message) {
        this.response = response;
        this.code = code;
        this.message = message;
    }

    public String getResponse() {
        return response;
    }

}
