package com.sixt.service.test_service.handler;

import com.sixt.service.framework.ports.exception.errors.Errors;
import com.sixt.service.framework.ports.exception.errors.RpcError;

public class TestInterceptionErrors
    implements Errors {

    @Override
    public RpcError getOrDefault(final Class<? extends Exception> exception) {
        return null;
    }

    @Override
    public RpcError defaultError() {
        return null;
    }
}
