package com.sixt.service.framework.ports.exception.errors;

public interface Errors {

    RpcError getOrDefault(Class<? extends Exception> exception);

    RpcError defaultError();
}
