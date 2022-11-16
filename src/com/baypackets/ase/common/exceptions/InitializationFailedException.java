package com.baypackets.ase.common.exceptions;

public final class InitializationFailedException extends Exception {
    
    public InitializationFailedException() {
        super();
    }
    
    public InitializationFailedException(String message) {
        super(message);
    }
    
    public InitializationFailedException(Exception e) {
        super(e.toString());
    }

}
