package com.on36.haetae.interpolatd.core;

import com.on36.haetae.interpolatd.EnclosureClosingHandler;
import com.on36.haetae.interpolatd.EnclosureOpeningHandler;

public class EnclosureOpeningHandlerImpl<T> implements EnclosureOpeningHandler<T> {

    private final String opening;
    
    private final String characterClass;
    
    private EnclosureClosingHandlerImpl<T> closingHandler;
    
    public EnclosureOpeningHandlerImpl(String opening, String characterClass) {
        
        this.opening = opening;
        this.characterClass = characterClass;
    }

    public EnclosureClosingHandler<T> and(String closing) {
        
        EnclosureClosingHandlerImpl<T> closingHandler = 
                new EnclosureClosingHandlerImpl<T>(opening, closing, characterClass);
        this.closingHandler = closingHandler;
        return closingHandler;
    }
    
    public EnclosureClosingHandlerImpl<T> getEnclosureClosingHandler() {
        
        return closingHandler;
    }
}
