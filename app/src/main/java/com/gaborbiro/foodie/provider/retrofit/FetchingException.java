package com.gaborbiro.foodie.provider.retrofit;

public class FetchingException extends Exception {

    private int mRequestId;

    public FetchingException(int requestId, Exception e) {
        super("Error fetching data", e);
        this.mRequestId = requestId;
    }

    public int getRequestId() {
        return mRequestId;
    }
}
