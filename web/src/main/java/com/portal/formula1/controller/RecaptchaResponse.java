package com.portal.formula1.controller;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RecaptchaResponse {
    private boolean success;
    private String hostname;
    private String challenge_ts;

    @JsonProperty("error-codes")
    private String[] errorCodes;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getChallenge_ts() {
        return challenge_ts;
    }

    public void setChallenge_ts(String challenge_ts) {
        this.challenge_ts = challenge_ts;
    }

    public String[] getErrorCodes() {
        return errorCodes;
    }

    public void setErrorCodes(String[] errorCodes) {
        this.errorCodes = errorCodes;
    }
}
