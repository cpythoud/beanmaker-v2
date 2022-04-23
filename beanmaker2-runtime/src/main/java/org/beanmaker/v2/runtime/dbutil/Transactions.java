package org.beanmaker.v2.runtime.dbutil;

import org.dbbeans.sql.DBTransaction;

import java.util.function.Consumer;

public final class Transactions {

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
        } catch (RuntimeException rtex) {
            transaction.rollback();
            if (errorProcessor != null)
                errorProcessor.accept(rtex);
            throw rtex;
        } catch (Exception ex) {
            transaction.rollback();
            if (errorProcessor != null)
                errorProcessor.accept(ex);
            throw new RuntimeException(ex);
        }

        transaction.commit();
    }

}
