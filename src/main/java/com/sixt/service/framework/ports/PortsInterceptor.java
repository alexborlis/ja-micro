package com.sixt.service.framework.ports;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.subclassesOf;

import com.sixt.service.framework.ServiceMethodHandler;
import com.sixt.service.framework.ports.exception.HandleExceptions;
import com.sixt.service.framework.ports.exception.RpcExceptionInterceptor;
import com.sixt.service.framework.ports.logs.HandlerLoggerInterceptor;
import com.sixt.service.framework.ports.logs.Log;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;

public final class PortsInterceptor {

    private final DisableSyntheticMethods disableSyntheticMethods = new DisableSyntheticMethods();
    private final DisableSyntheticClasses disableSyntheticClasses = new DisableSyntheticClasses();
    private final String ports;

    public PortsInterceptor(final String portsPackage) {
        ports = portsPackage;
    }

    public AbstractModule exceptionHandler() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bindInterceptor(
                    // class matcher
                    disableSyntheticClasses
                        .and(subclassesOf(ServiceMethodHandler.class)) // implements ServiceMethodHandler
                        .and(Matchers.inSubpackage(ports)), // in 'ports' package
                    disableSyntheticMethods
                        .and(annotatedWith(HandleExceptions.class)),
                    new RpcExceptionInterceptor()
                );
            }
        };
    }

    public AbstractModule exceptionHandlerAndLogger() {
        return new AbstractModule() {
            @Override
            protected void configure() {
                bindInterceptor(
                    // class matcher
                    disableSyntheticClasses
                        .and(subclassesOf(ServiceMethodHandler.class)) // implements ServiceMethodHandler
                        .and(Matchers.inSubpackage(ports)), // in 'ports' package
                    disableSyntheticMethods
                        .and(annotatedWith(HandleExceptions.class)),
                    new RpcExceptionInterceptor()
                );
                bindInterceptor(
                    // class matcher
                    disableSyntheticClasses
                        .and(subclassesOf(ServiceMethodHandler.class)) // implements ServiceMethodHandler
                        .and(Matchers.inSubpackage(ports)), // in 'ports' package
                    disableSyntheticMethods
                        .and(annotatedWith(Log.class)),
                    new HandlerLoggerInterceptor()
                );
            }
        };
    }
}
