package dev.sepler.vaultbox.model.exception;

public class InvalidVaultItemException extends Exception {
    public InvalidVaultItemException(final String reason) {
        super(reason);
    }

}
