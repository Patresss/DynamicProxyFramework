package com.patres.framework.handler;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ProxyHandler {

    private final Object objectToHandle;
    private final Class<? extends Annotation> annotation;

    public ProxyHandler(Object objectToHandle, Class<? extends Annotation> annotation) {
        this.objectToHandle = objectToHandle;
        this.annotation = annotation;
    }

    public boolean isSupported(Method method) {
        try {
            return objectToHandle.getClass().getMethod(method.getName(), method.getParameterTypes()).getAnnotation(annotation) != null;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

}
