package com.on36.haetae.interpolatd;

public interface Substitutor<T> {

    String substitute(String captured, T arg);
}
