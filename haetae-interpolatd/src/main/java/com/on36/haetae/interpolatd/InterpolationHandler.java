package com.on36.haetae.interpolatd;


public interface InterpolationHandler<T> {

    PrefixHandler<T> prefixedBy(String prefix);
    
    EnclosureOpeningHandler<T> enclosedBy(String opening);
}
