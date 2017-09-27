package com.sixt.service.framework.ports.exception.errors;

import com.sixt.service.framework.rpc.RpcCallException;

import com.google.protobuf.ProtocolMessageEnum;

public final class RpcError {

    private final ProtocolMessageEnum code;
    private final RpcCallException.Category category;
    private final boolean retriable;

    public RpcError(
        final ProtocolMessageEnum errorCode,
        final RpcCallException.Category errorCategory,
        final boolean isRetriable
    ) {
        code = errorCode;
        category = errorCategory;
        retriable = isRetriable;
    }

    public ProtocolMessageEnum code() {
        return code;
    }

    public RpcCallException.Category category() {
        return category;
    }

    public boolean retriable() {
        return retriable;
    }
}
