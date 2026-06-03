package org.beanmaker.v2.runtime.dbutil;

import org.beanmaker.v2.database.sql.DbTransaction;

import java.util.function.Consumer;
import java.util.function.Function;

public final class Transactions {

    private Transactions() { }

    public static void wrap(Consumer<DbTransaction> transactedFunction, DbTransaction transaction) {
        wrap(transactedFunction, transaction, null);
    }

    public static void wrap(
            Consumer<DbTransaction> transactedFunction,
            DbTransaction transaction,
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
            Function<DbTransaction, T> transactedFunction,
            DbTransaction transaction)
    {
        return extract(transactedFunction, transaction, null);
    }

    public static <T> T extract(
            Function<DbTransaction, T> transactedFunction,
            DbTransaction transaction,
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
