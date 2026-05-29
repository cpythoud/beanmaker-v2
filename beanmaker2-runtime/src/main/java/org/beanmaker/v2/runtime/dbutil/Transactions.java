package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.database.sql.DBTransaction;

import java.util.function.Consumer;
import java.util.function.Function;

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

    public static <T> T extract(
            Function<DBTransaction, T> transactedFunction,
            DBTransaction transaction)
    {
        return extract(transactedFunction, transaction, null);
    }

    public static <T> T extract(
            Function<DBTransaction, T> transactedFunction,
            DBTransaction transaction,
            Consumer<Throwable> errorProcessor)
    {
        T result = null;
        try {
            result = transactedFunction.apply(transaction);
        }  catch (Throwable t) {
            transaction.rollback();
            if (errorProcessor == null)
                throw new RuntimeException(t);
            errorProcessor.accept(t);
        }

        transaction.commit();
        return result;
    }

}
