package com.sixt.service.framework.ports;

import com.google.inject.matcher.AbstractMatcher;

public class DisableSyntheticClasses
    extends AbstractMatcher<Class> {

    @Override
    public boolean matches(final Class aClass) {
        return !aClass.isSynthetic();
    }
}
