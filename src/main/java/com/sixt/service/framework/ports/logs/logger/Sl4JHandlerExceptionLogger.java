package com.sixt.service.framework.ports.logs.logger;

import com.sixt.service.framework.rpc.RpcCallException;

import com.google.gson.JsonObject;

import net.logstash.logback.marker.Markers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Sl4JHandlerExceptionLogger
    implements HandlerExceptionLogger {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void log(
        final Class handlerClass,
        final JsonObject requestJson,
        final Exception exception,
        final String[] markerNames
    ) {
        MarkersMapper mappedMarkers = new MarkersMapper(handlerClass.getSimpleName(), requestJson, markerNames);

        if (exception instanceof RpcCallException) {
            RpcCallException rpcException = (RpcCallException) exception;

            if (isInfoLevel(rpcException)) {
                logger.info(Markers.appendEntries(mappedMarkers.markers()), "", rpcException);
            } else if (isWarningLevel(rpcException)) {
                logger.warn(Markers.appendEntries(mappedMarkers.markers()), requestJson.toString(), rpcException);
            } else {
                logger.error(Markers.appendEntries(mappedMarkers.markers()), requestJson.toString(), rpcException);
            }
        }
    }

    private boolean isInfoLevel(final RpcCallException e) {
        return e.getCategory() == RpcCallException.Category.ResourceNotFound;
    }

    private boolean isWarningLevel(final RpcCallException e) {
        return (e.getCategory() == RpcCallException.Category.BadRequest) || (e.getCategory()
            == RpcCallException.Category.BackendError);
    }
}
