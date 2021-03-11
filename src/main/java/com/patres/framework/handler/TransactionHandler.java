package com.patres.framework.handler;

import com.patres.framework.component.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

public class TransactionHandler extends ProxyHandler {

    private static final Logger logger = LoggerFactory.getLogger(TransactionHandler.class);

    public TransactionHandler(Object objectToHandle) {
        super(objectToHandle, Transactional.class);
    }

    public Object executeWithTransaction(Supplier<Object> resultSupplier) {
        openTransaction();
        try {
            Object result = resultSupplier.get();
            commitTransaction();
            return result;
        } catch (Exception e) {
            rollbackTransaction();
            throw e;
        }
    }

    private void openTransaction() {
        logger.debug("OPEN TRANSACTION");
    }

    private void commitTransaction() {
        logger.debug("COMMIT TRANSACTION");
    }

    private void rollbackTransaction() {
        logger.error("ROLLBACK TRANSACTION");
    }

}
