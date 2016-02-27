package com.on36.haetae.interpolatd.core;

import java.util.ArrayList;
import java.util.List;

import com.on36.haetae.interpolatd.EnclosureOpeningHandler;
import com.on36.haetae.interpolatd.InterpolationHandler;
import com.on36.haetae.interpolatd.PrefixHandler;

public class InterpolationHandlerImpl<T> implements InterpolationHandler<T>, Interpolating<T> {

    private PrefixHandlerImpl<T> prefixHandler;
    
    private EnclosureOpeningHandlerImpl<T> enclosureOpeningHandler;
    
    private final String characterClass;
    
    public InterpolationHandlerImpl() {
        this(null);
    }
    
    public InterpolationHandlerImpl(String characterClass) {
        this.characterClass = characterClass;
    }
    
    public PrefixHandler<T> prefixedBy(String prefix) {
        
        PrefixHandlerImpl<T> prefixHandler = new PrefixHandlerImpl<T>(prefix, characterClass);
        this.prefixHandler = prefixHandler;
        return prefixHandler;
    }
    
    public EnclosureOpeningHandler<T> enclosedBy(String opening) {
        
        EnclosureOpeningHandlerImpl<T> enclosureOpeningHandler = 
                new EnclosureOpeningHandlerImpl<T>(opening, characterClass);
        this.enclosureOpeningHandler = enclosureOpeningHandler;
        return enclosureOpeningHandler;
    }
    
    public List<Substitution> interpolate(String toInterpolate, T arg) {
        
        List<Substitution> substitutions = new ArrayList<Substitution>();
        if (prefixHandler != null) {
            
            substitutions.addAll(prefixHandler.interpolate(toInterpolate, arg));
            
        } else if (enclosureOpeningHandler != null) {
            
            substitutions.addAll(enclosureOpeningHandler.getEnclosureClosingHandler()
                                                   .interpolate(toInterpolate, arg));
        }
        
        return substitutions;
    }
}
