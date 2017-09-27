package com.sixt.service.framework.ports.logs.logger;

import com.google.gson.JsonObject;

public interface HandlerExceptionLogger {
    void log(Class handlerClass, JsonObject requestJson, Exception exception, String[] markerNames);
}