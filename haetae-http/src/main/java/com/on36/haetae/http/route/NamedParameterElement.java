package com.on36.haetae.http.route;


public class NamedParameterElement extends PathElement {
    
    private final String regex; 
    
    public NamedParameterElement(String name, int index, String regex) {
        super(name, index);
        this.regex = regex;
    }
    
    /**
     * Returns the regex pattern for the element if it exists,
     * or null if no regex pattern was provided.
     * 
     * @return the regex pattern for the element if it exists, 
     * or null otherwise
     */
    public String regex() {
        return regex;
    }
    
    public boolean hasRegex() {
        return regex != null && regex.trim().length() > 0;
    }
    
    public boolean equals(Object o) {
        
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof NamedParameterElement)) return false;
        NamedParameterElement that = (NamedParameterElement)o;
        
        return super.equals(o) && (this.regex == null ? that.regex == null : 
            this.regex.equals(that.regex));
    }
}