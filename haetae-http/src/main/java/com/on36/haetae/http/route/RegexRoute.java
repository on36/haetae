package com.on36.haetae.http.route;

import java.util.regex.Pattern;

import static com.on36.haetae.http.route.RouteHelper.*;

public class RegexRoute {

    private final Route route;
    /*
     * From the Java API documentation for the Pattern class:
     * Instances of this (Pattern) class are immutable and are safe for use by 
     * multiple concurrent threads. Instances of the Matcher class are not 
     * safe for such use.
     */
    private final Pattern pattern;
    
    public RegexRoute(Route route) {
        
        this.route = route;
        this.pattern = compilePattern();
    }
    
    private Pattern compilePattern() {
        
        String paramPath = escapeNonCustomRegex(route.toString().substring(1));
        String[] tokens = paramPath.split(PATH_ELEMENT_SEPARATOR);
        StringBuilder routeRegex = new StringBuilder("^").append(PATH_ELEMENT_SEPARATOR);
        
        for (int i = 0; i < tokens.length; i++) {
            
            if (i > 0) routeRegex.append(PATH_ELEMENT_SEPARATOR);
            String currentToken = tokens[i];
            if (currentToken.startsWith(PARAM_PREFIX)) {
                
                currentToken = currentToken.substring(1);
                int customRegexIdx = currentToken.indexOf(CUSTOM_REGEX_START);
                if (customRegexIdx == -1) {
                    routeRegex.append("([^").append(PATH_ELEMENT_SEPARATOR).append("]+)");
                } else {
                    String customRegex = currentToken.substring(customRegexIdx + 1, 
                            currentToken.indexOf(CUSTOM_REGEX_END));
                    routeRegex.append("(").append(customRegex).append(")");
                }
                
            } else if (currentToken.equals("\\" + WILDCARD)) {
                
                routeRegex.append("(");
                if ((i + 1) == tokens.length) {
                    /* this is the last token */
                    routeRegex.append(".");
                } else {
                    routeRegex.append("[^").append(PATH_ELEMENT_SEPARATOR).append("]");
                }
                routeRegex.append("*)");
                
            } else {
                
                routeRegex.append(currentToken);
            }
        }
        
        if (route.hasPathElements() && route.endsWithPathSeparator()) {
            routeRegex.append(PATH_ELEMENT_SEPARATOR);
        }
        routeRegex.append("$");
        return Pattern.compile(routeRegex.toString());
    }
    
    public Pattern pattern() {
        
        return pattern;
    }
    
    public Route getRoute() {
        
        return route;
    }
    
    public String toString() {
        
        return pattern.toString();
    }
    
    public int hashCode() {
        
        return route.hashCode();
    }
    
    public boolean equals(Object o) {
        
        if (o == null) return false;
        if (o == this) return true;
        if (!(o instanceof RegexRoute)) return false;
        RegexRoute that = (RegexRoute)o;
        return this.route.equals(that.route);
    }
}
