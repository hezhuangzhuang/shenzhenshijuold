package com.zxwl.network.exception;

/**
 * Copyright 2015 蓝色互动. All rights reserved.
 * author：hw
 * data:2017/4/20 12:55
 * ClassName: ${Class_Name}
 */

public class ResponeThrowable extends Exception {
    public int code;
    public String message;

    public ResponeThrowable(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public ResponeThrowable(String message, String message1) {
        super(message);
        this.message = message1;
    }
}
