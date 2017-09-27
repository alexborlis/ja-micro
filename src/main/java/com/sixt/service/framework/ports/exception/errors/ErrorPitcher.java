package com.sixt.service.framework.ports.exception.errors;

import com.sixt.service.framework.rpc.RpcCallException;

public final class ErrorPitcher {

    public RpcCallException exception(final Errors errors, final Exception originalException) {
        return createException(
            errors.getOrDefault(originalException.getClass()),
            originalException.getMessage()
        );
    }

    private RpcCallException createException(final RpcError error, final String message) {
        return new RpcCallException(
            error.category(),
            message
        )
            .withErrorCode(error.code().getValueDescriptor().getName())
            .withRetriable(error.retriable());
    }
}
