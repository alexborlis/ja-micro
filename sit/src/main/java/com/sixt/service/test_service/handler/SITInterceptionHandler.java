package com.sixt.service.test_service.handler;

import com.sixt.service.framework.OrangeContext;
import com.sixt.service.framework.ServiceMethodHandler;
import com.sixt.service.framework.annotation.RpcHandler;
import com.sixt.service.framework.intercepted.api.Intercepted;
import com.sixt.service.framework.ports.exception.HandleExceptions;
import com.sixt.service.framework.ports.logs.Log;
import com.sixt.service.framework.protobuf.RpcEnvelope;
import com.sixt.service.framework.rpc.RpcCallException;

import java.io.IOException;

@RpcHandler("InterceptedService.InterceptedMethod")
public class SITInterceptionHandler
    implements ServiceMethodHandler<Intercepted.InterceptedRequest, Intercepted.InterceptedResponse> {

    private Exception exceptionToThrow;

    public void setExceptionToThrow(final Exception exceptionToThrow) {
        this.exceptionToThrow = exceptionToThrow;
    }

    @HandleExceptions(
        skip = IOException.class,
        errorsMapped = TestInterceptionErrors.class,
        defaultResponseClass = RpcEnvelope.Response.class
    )
    @Log(markers = {"invoice_number", "invoice_version"})
    @Override
    public Intercepted.InterceptedResponse handleRequest(
        final Intercepted.InterceptedRequest request, final OrangeContext ctx
    ) throws RpcCallException {
        if (exceptionToThrow != null) {
            throw new RuntimeException(exceptionToThrow);
        }

        return Intercepted.InterceptedResponse.getDefaultInstance();
    }
}
