package com.on36.haetae.interpolatd.core;

import java.util.regex.Pattern;

import com.on36.haetae.interpolatd.PrefixHandler;

public class PrefixHandlerImpl<T> extends SubstitutionHandlerImpl<T>
    implements PrefixHandler<T> {

    private final Pattern pattern;
    
    private final String prefix;
    
    public PrefixHandlerImpl(String prefix, String characterClass) {
        
        this.prefix = prefix;
        
        String quotedPrefix = Pattern.quote(prefix);
        if (characterClass == null) {
            characterClass = "[^" + quotedPrefix + "\\s]+";
        }
        this.pattern = Pattern.compile("(" + quotedPrefix + characterClass + ")");
    }
    
    @Override
    protected Pattern getPattern() {
        
        return pattern;
    }
    
    @Override
    protected String getCaptured(String found) {

        return found.substring(prefix.length());
    }
}
