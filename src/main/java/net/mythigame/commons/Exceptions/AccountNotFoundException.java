package net.mythigame.commons.Exceptions;

import java.util.UUID;

public class AccountNotFoundException extends Exception{
    public AccountNotFoundException(UUID uuid){
        super("The account (" + uuid + ") not found");
    }
}
