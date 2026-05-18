package br.com.fiap.nora.exceptions;

public class PythonApiException extends RuntimeException {

    public PythonApiException(String message) {
        super(message);
    }

    public PythonApiException(String message, Throwable cause) {
        super(message, cause);
    }
}
