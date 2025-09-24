package br.com.zef.exception;

public class PixInUseException extends RuntimeException {

    public PixInUseException(String message) {
        super(message);
    }

}