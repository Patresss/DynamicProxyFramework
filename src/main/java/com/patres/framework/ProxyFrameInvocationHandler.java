package com.patres.framework;

import com.patres.framework.handler.CacheHandler;
import com.patres.framework.handler.TransactionHandler;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Optional;

public class ProxyFrameInvocationHandler implements InvocationHandler {

    private final Object objectToHandle;
    private final CacheHandler cacheHandler;
    private final TransactionHandler transactionHandler;


    public ProxyFrameInvocationHandler(Object objectToHandle) {
        this.objectToHandle = objectToHandle;
        this.cacheHandler = new CacheHandler(objectToHandle);
        this.transactionHandler = new TransactionHandler(objectToHandle);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Exception {
        if (cacheHandler.isSupported(method)) {
            final Optional<Object> cachedResult = cacheHandler.takeResultIfExist(method, args);
            if (cachedResult.isPresent()) {
                return cachedResult.get();
            }
        }

        if (transactionHandler.isSupported(method)) {
            return transactionHandler.executeWithTransaction(() -> calculateResult(method, args));
        }

        return calculateResult(method, args);
    }

    private Object calculateResult(Method method, Object[] args) {
        try {
            final Object result = method.invoke(objectToHandle, args);
            if (cacheHandler.isSupported(method)) {
                cacheHandler.addResultToCache(method, args, result);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
