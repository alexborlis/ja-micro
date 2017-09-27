package com.sixt.service.framework.ports;

import com.google.inject.matcher.AbstractMatcher;

import java.lang.reflect.Method;

public class DisableSyntheticMethods
    extends AbstractMatcher<Method> {

    @Override
    public boolean matches(final Method method) {
        return !method.isSynthetic();
    }
}
