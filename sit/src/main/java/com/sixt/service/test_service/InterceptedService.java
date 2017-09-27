package com.sixt.service.test_service;

import com.sixt.service.framework.AbstractService;
import com.sixt.service.framework.annotation.OrangeMicroservice;
import com.sixt.service.framework.kafka.messaging.ConsumerFactory;
import com.sixt.service.framework.kafka.messaging.DiscardFailedMessages;
import com.sixt.service.framework.ports.PortsInterceptor;
import com.sixt.service.test_service.handler.RandomEventHandler;
import com.sixt.service.test_service.handler.SITInterceptionHandler;

import com.google.inject.Module;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

@OrangeMicroservice
public class InterceptedService
    extends AbstractService {

    @Override
    protected List<Module> getGuiceModules() {
        PortsInterceptor portsInterceptor = new PortsInterceptor("com.sixt.service.test_service.handler");
        return Arrays.asList(portsInterceptor.exceptionHandlerAndLogger());
    }

    @Override
    public void registerMethodHandlers() {
        registerMethodHandlerFor("Intercepted.InterceptedMethod", SITInterceptionHandler.class);
    }

    @Override
    public void displayHelp(PrintStream out) {
    }

    @Override
    public void bootstrapComplete() throws InterruptedException {
        // Start a messaging consumer for the default inbox.
        ConsumerFactory consumerFactory = injector.getInstance(ConsumerFactory.class);
        consumerFactory.defaultInboxConsumer(new DiscardFailedMessages());

        injector.getInstance(RandomEventHandler.class);

        super.bootstrapComplete();
    }
}
