package com.on36.haetae.interpolatd.core;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EscapeHandler<T> implements Interpolating<T> {

    private final String escape;
    private final Pattern pattern;
    
    public EscapeHandler(String escape) {
        
        this.escape = escape;
        this.pattern = Pattern.compile("(" + Pattern.quote(escape) + ")");
    }

    public List<Substitution> interpolate(String toInterpolate, T arg) {
        
        List<Substitution> substitutions = new ArrayList<Substitution>(); 
        Matcher m = pattern.matcher(toInterpolate);
        while (m.find()) {
            substitutions.add(new Substitution(escape, "", m.start(), m.end(), true));
        }

        return substitutions;
    }
}
