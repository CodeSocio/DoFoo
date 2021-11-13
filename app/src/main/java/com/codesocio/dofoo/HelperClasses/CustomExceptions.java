package com.codesocio.dofoo.HelperClasses;

import java.lang.Exception;
import java.lang.Throwable;

public class CustomExceptions {
    public static class InvalidPasswordException extends Exception {
        public InvalidPasswordException(String message) {
            super(message);
        }
    };
    
    public static class InvalidPhoneException extends Exception {
        public InvalidPhoneException(String message) {
            super(message);
        }
    };
    
    public static class InvalidEmailException extends Exception {
        public InvalidEmailException(String message) {
            super(message);
        }
    };
}
