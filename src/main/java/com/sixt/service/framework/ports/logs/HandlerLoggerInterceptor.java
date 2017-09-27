package com.sixt.service.framework.ports.logs;

import static com.sixt.service.framework.protobuf.ProtobufUtil.protobufToJson;

import com.sixt.service.framework.ports.logs.logger.HandlerExceptionLogger;
import com.sixt.service.framework.rpc.RpcCallException;

import com.google.inject.Inject;
import com.google.protobuf.GeneratedMessageV3;

import net.logstash.logback.marker.Markers;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashMap;
import java.util.Map;

public class HandlerLoggerInterceptor
    implements MethodInterceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Inject
    HandlerExceptionLogger exceptionLogger;

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        Map<String, Object> aspectLogMarkers = new LinkedHashMap<>();
        aspectLogMarkers.put("aspectType", HandlerLoggerInterceptor.class.getSimpleName());
        aspectLogMarkers.put("startedAt", UTCDateTime.now().toInstant().toString());
        Object result = null;

        try {
            log.trace(Markers.appendEntries(aspectLogMarkers), "Started pointcut to process handler logs.");
            result = methodInvocation.proceed();
        } catch (RpcCallException e) {
            aspectLogMarkers.put("loggingExceptionCategory", e.getCategory().name());
            aspectLogMarkers.put("data", e.getData());
            aspectLogMarkers.put("stoppedAt", UTCDateTime.now().toInstant().toString());

            exceptionLogger.log(
                methodInvocation.getMethod().getClass(),
                protobufToJson(getRequest(methodInvocation)),
                e,
                methodInvocation.getMethod().getAnnotation(Log.class).markers()
            );

            throw e;
        }

        aspectLogMarkers.put("stoppedAt", UTCDateTime.now().toInstant().toString());
        log.trace(Markers.appendEntries(aspectLogMarkers), "Stopped pointcut to process handler logs.");

        return result;
    }

    @SuppressWarnings("unchecked")
    private <T extends GeneratedMessageV3> T getRequest(
        final MethodInvocation methodInvocation
    ) {
        Object[] parameters = methodInvocation.getArguments();
        for (Object object : parameters) {
            if (object instanceof GeneratedMessageV3) {
                return (T) object;
            }
        }

        throw new IllegalStateException("No request found in RpcHandler handle method signature.");
    }

    static class UTCDateTime {

        private static final ZoneId UTC_ZONE = ZoneId.of("UTC");

        public UTCDateTime() {
        }

        public static ZonedDateTime now() {
            return ZonedDateTime.now(UTC_ZONE);
        }

        public static ZonedDateTime fromStringISOFormat(String value, String valueName) {

            try {
                Instant valueInstant = Instant.parse(value);
                return ZonedDateTime.ofInstant(valueInstant, UTC_ZONE);
            } catch (DateTimeParseException var3) {
                throw new IllegalArgumentException(String.format(
                    "(%s) Invalid [%s] for parsing. %s",
                    UTCDateTime.class.getName(),
                    valueName,
                    var3.getMessage()
                ));
            }
        }
    }
}
