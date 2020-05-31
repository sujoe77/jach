package com.afrunt.jach.logic;

import com.afrunt.jach.exception.ACHException;

public class ACHErrorMixInBase implements ACHErrorMixIn {
    @Override
    public void throwError(String message) throws ACHException {
        throw error(message);
    }

    @Override
    public ACHException error(String message) {
        return new ACHException(message);
    }

    @Override
    public ACHException error(String message, Throwable e) {
        return new ACHException(message, e);
    }
}
