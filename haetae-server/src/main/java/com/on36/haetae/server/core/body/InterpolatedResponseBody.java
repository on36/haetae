package com.on36.haetae.server.core.body;

import com.on36.haetae.api.Context;
import com.on36.haetae.server.core.interpolation.ResponseBodyInterpolator;

public class InterpolatedResponseBody extends StringResponseBody {

    public InterpolatedResponseBody(String body, Context req) {
        super(ResponseBodyInterpolator.interpolate(body, req));
    }
}
