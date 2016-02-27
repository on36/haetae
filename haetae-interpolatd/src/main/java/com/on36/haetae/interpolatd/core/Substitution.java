package com.on36.haetae.interpolatd.core;

public class Substitution implements Comparable<Substitution> {

    private final String found;
    private final String value;
    private final int start;
    private final int end;
    private final boolean escape;

    public Substitution(String found, String value, int start, int end) {
        this(found, value, start, end, false);
    }
    
    public Substitution(String found, String value, int start, int end, boolean escape) {
        
        this.found = found;
        this.value = value;
        this.start = start;
        this.end = end;
        this.escape = escape;
    }

    public String found() {
        return found;
    }
    
    public String value() {
        return value;
    }

    public int start() {
        return start;
    }

    public int end() {
        return end;
    }

    public boolean isEscape() {
        return escape;
    }
    
    public boolean isAfter(Substitution that) {
        return this.start() == that.end();
    }
    
    public int compareTo(Substitution that) {
        return Integer.compare(this.start, that.start);
    }
}
