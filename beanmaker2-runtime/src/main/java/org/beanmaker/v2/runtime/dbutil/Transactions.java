package org.beanmaker.v2.runtime.dbutil;

import org.dbbeans.sql.DBTransaction;

import java.util.function.Consumer;

public final class Transactions {

    private Transactions() { }

    public static void wrap(Consumer<DBTransaction> transactedFunction, DBTransaction transaction) {
        wrap(transactedFunction, transaction, null);
    }

    public static void wrap(
            Consumer<DBTransaction> transactedFunction,
            DBTransaction transaction,
            Consumer<Throwable> errorProcessor)
    {
        try {
            transactedFunction.accept(transaction);
        } catch (Throwable t) {
            transaction.rollback();
            if (errorProcessor == null)
                throw new RuntimeException(t);
            errorProcessor.accept(t);
        }

        transaction.commit();
    }

}
