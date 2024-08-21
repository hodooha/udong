package com.multi.udong.share.exeption;


public class NoLocationException extends RuntimeException{

    public NoLocationException() {
        super();
    }

    public NoLocationException(String message) {
        super(message);
    }
}
