package com.smargav.api.net;

import java.io.IOException;

/**
 * Created by Amit S on 17/10/15.
 */
public class NetIOException extends IOException {

    private int code;

    public NetIOException(int code, String message) {
        super(message);
        this.code = code;

    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
