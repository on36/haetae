package com.on36.haetae.interpolatd.core;

import java.util.List;

public interface Interpolating<T> {

    List<Substitution> interpolate(String toInterpolate, T arg);
}
