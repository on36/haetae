package com.on36.haetae.http.route;

import static com.on36.haetae.http.route.RouteHelper.*;

public class SplatParameterElement extends PathElement {

    public SplatParameterElement(int index) {
        
        super(WILDCARD, index);
    }
}
