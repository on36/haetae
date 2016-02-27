package com.on36.haetae.http.route;

import java.util.Comparator;

import static com.on36.haetae.http.route.RouteHelper.*;

public class PathElementComparator implements Comparator<String> {

    public int compare(String r1Elem, String r2Elem) {
        
        if (r1Elem.equals("")) return -1;
        if (r2Elem.equals("")) return 1;
        
        if (r1Elem.equals(WILDCARD) && !r2Elem.equals("")) return -1;
        if (r2Elem.equals(WILDCARD) && !r1Elem.equals("")) return 1;
        
        if (r1Elem.equals(WILDCARD) && r2Elem.equals("")) return 1;
        if (r2Elem.equals(WILDCARD) && r1Elem.equals("")) return -1;
        
        if (r1Elem.startsWith(PARAM_PREFIX) && !r2Elem.equals("") && !r2Elem.equals(WILDCARD)) return 1;
        if (r2Elem.startsWith(PARAM_PREFIX) && !r1Elem.equals("") && !r1Elem.equals(WILDCARD)) return -1;
        
        if (r1Elem.startsWith(PARAM_PREFIX) && (r2Elem.equals(WILDCARD) || r2Elem.equals(""))) return -1;
        if (r2Elem.startsWith(PARAM_PREFIX) && (r1Elem.equals(WILDCARD) || r1Elem.equals(""))) return 1;
        
        return r1Elem.compareTo(r2Elem);
    }

}
