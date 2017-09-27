package com.sixt.service.framework.ports.exception;

import com.sixt.service.framework.ports.exception.errors.Errors;

import com.google.protobuf.GeneratedMessageV3;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HandleExceptions {

    Class<? extends Errors> errorsMapped();

    Class<? extends Exception>[] skip();

    Class<? extends GeneratedMessageV3> defaultResponseClass();
}
