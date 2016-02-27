package com.on36.haetae.http.route;

import java.util.Comparator;

import static com.on36.haetae.http.route.RouteHelper.*;

public class RegexRouteComparator implements Comparator<RegexRoute> {

    public int compare(RegexRoute r1, RegexRoute r2) {
        
        String r1Path = r1.getRoute().getResourcePath();
        String r2Path = r2.getRoute().getResourcePath();
        
        r1Path = CUSTOM_REGEX_PATTERN.matcher(r1Path).replaceAll("");
        r2Path = CUSTOM_REGEX_PATTERN.matcher(r2Path).replaceAll("");
        
        String[] r1Elements = r1Path.substring(1).split(PATH_ELEMENT_SEPARATOR, -1);
        String[] r2Elements = r2Path.substring(1).split(PATH_ELEMENT_SEPARATOR, -1);
        
        if (r1Elements.length != r2Elements.length) {
            return Integer.compare(r1Elements.length, r2Elements.length);
        }
        
        for (int i = 0; i < r1Elements.length; i++) {
            
            String r1Elem = r1Elements[i];
            String r2Elem = r2Elements[i];
            
            if (!r1Elem.equals(r2Elem)) {
                
                return new PathElementComparator().compare(r1Elem, r2Elem);
            }
        }
        
        return r1.getRoute().getResourcePath().compareTo(r2.getRoute().getResourcePath());
     }
}
