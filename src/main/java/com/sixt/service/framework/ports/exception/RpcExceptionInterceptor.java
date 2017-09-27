package com.sixt.service.framework.ports.exception;

import com.sixt.service.framework.ports.exception.errors.ErrorPitcher;
import com.sixt.service.framework.ports.exception.errors.Errors;

import com.google.protobuf.GeneratedMessageV3;

import net.logstash.logback.marker.Markers;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

public class RpcExceptionInterceptor
    implements MethodInterceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public Object invoke(final MethodInvocation methodInvocation) throws Throwable {
        Map<String, Object> markers = new LinkedHashMap<>();
        markers.put("type", RpcExceptionInterceptor.class.getSimpleName());
        markers.put("startedAt", UTCDateTime.now().toInstant().toString());
        Object result = null;

        try {
            log.trace(Markers.appendEntries(markers), "Started pointcut processing for RpcCallException.");

            result = methodInvocation.proceed();
        } catch (Exception e) {
            markers.put("caseProcessed", e.getClass().getSimpleName());
            markers.put("stoppedAt", UTCDateTime.now().toInstant().toString());
            log.trace(
                Markers.appendEntries(markers),
                "Converting " + e.getClass().getSimpleName() + " to RpcCallException."
            );

            HandleExceptions annotation = methodInvocation.getMethod().getAnnotation(HandleExceptions.class);
            if (rpcExceptionShouldBeThrown(annotation, e)) {
                new ErrorPitcher().exception(getErrorMapping(annotation), e);
            } else {
                return createDefaultResponse(annotation);
            }
        }

        markers.put("stoppedAt", UTCDateTime.now().toInstant().toString());
        log.trace(Markers.appendEntries(markers), "Stopped pointcut processing for RpcCallException.");

        return result;
    }

    private <DEFAULT extends GeneratedMessageV3> DEFAULT createDefaultResponse(final HandleExceptions annotation)
        throws IllegalAccessException, InvocationTargetException, InstantiationException {
        Class<? extends GeneratedMessageV3> responseClass = annotation.defaultResponseClass();
        return (DEFAULT) responseClass.getConstructors()[0].newInstance();
    }

    private boolean rpcExceptionShouldBeThrown(
        final HandleExceptions annotation,
        final Exception e
    ) {
        Class<? extends Exception>[] exceptionsToSkip = annotation.skip();
        return Arrays.stream(exceptionsToSkip).anyMatch(exception -> e.getCause().getClass().equals(exception));
    }

    @SuppressWarnings("unchecked")
    private <T extends Errors> T getErrorMapping(
        final HandleExceptions handleExceptions
    ) throws IllegalAccessException, InstantiationException, InvocationTargetException {
        Class<? extends Errors> mappingOffErrors = handleExceptions.errorsMapped();
        return (T) mappingOffErrors.getConstructors()[0].newInstance();
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
