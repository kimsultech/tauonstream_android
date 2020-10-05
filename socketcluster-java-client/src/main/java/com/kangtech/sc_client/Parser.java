package com.kangtech.sc_client;


import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by sachin on 15/11/16.
 */
public class Parser {

    public enum ParseResult {
        ISAUTHENTICATED,
        PUBLISH,
        REMOVETOKEN,
        SETTOKEN,
        EVENT,
        ACKRECEIVE
    }


    public static ParseResult parse(Object dataobject, String event) throws JSONException {

        if (dataobject instanceof JSONObject && ((JSONObject) dataobject).opt("isAuthenticated") != null) {
            return ParseResult.ISAUTHENTICATED;
        } else if (event != null) {
            if (event.equals("#publish")) {
                return ParseResult.PUBLISH;
            } else if (event.equals("#removeAuthToken")) {
                return ParseResult.REMOVETOKEN;
            } else if (event.equals("#setAuthToken")) {
                return ParseResult.SETTOKEN;
            } else {
                return ParseResult.EVENT;
            }
        } else {
            return ParseResult.ACKRECEIVE;
        }
    }

}
