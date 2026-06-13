package org.beanmaker.v2.database.sql;

public interface DbExecutor {

    DbType dbType();


    long createRecord(String query, DbQuerySetup querySetup);

    int processUpdate(String query, DbQuerySetup querySetup);
    int processUpdate(String query);

    void processQuery(String query, DbQuerySetup querySetup, DbQueryProcess queryProcess);
    void processQuery(String query, DbQueryProcess queryProcess);

    <T> T processQuery(String query, DbQuerySetup querySetup, DbQueryRetrieveData<T> queryRetrieveData);
    <T> T processQuery(String query, DbQueryRetrieveData<T> retrieveData);

    void processUpdates(String query, DbUpdates updates);

    void processQueries(String query, DbQueriesNoReturn queries);
    <T> T processQueries(String query, DbQueries<T> queries);


    default long createRecord(SecureQuery query, DbQuerySetup querySetup) {
        return createRecord(query.parse(dbType()), querySetup);
    }

    default int processUpdate(SecureQuery query, DbQuerySetup querySetup) {
        return processUpdate(query.parse(dbType()), querySetup);
    }

    default int processUpdate(SecureQuery query) {
        return processUpdate(query.parse(dbType()));
    }

    default void processQuery(SecureQuery query, DbQuerySetup querySetup, DbQueryProcess queryProcess) {
        processQuery(query.parse(dbType()), querySetup, queryProcess);
    }

    default void processQuery(SecureQuery query, DbQueryProcess queryProcess) {
        processQuery(query.parse(dbType()), queryProcess);
    }

    default <T> T processQuery(SecureQuery query, DbQuerySetup querySetup, DbQueryRetrieveData<T> queryRetrieveData) {
        return processQuery(query.parse(dbType()), querySetup, queryRetrieveData);
    }

    default <T> T processQuery(SecureQuery query, DbQueryRetrieveData<T> retrieveData) {
        return processQuery(query.parse(dbType()), retrieveData);
    }

    default void processUpdates(SecureQuery query, DbUpdates updates) {
        processUpdates(query.parse(dbType()), updates);
    }

    default void processQueries(SecureQuery query, DbQueriesNoReturn queries) {
        processQueries(query.parse(dbType()), queries);
    }

    default <T> T processQueries(SecureQuery query, DbQueries<T> queries) {
        return processQueries(query.parse(dbType()), queries);
    }


    default long createRecord(DbQuery query, DbQuerySetup querySetup) {
        return createRecord(query.parse(dbType()), querySetup);
    }

    default int processUpdate(DbQuery query, DbQuerySetup querySetup) {
        return processUpdate(query.parse(dbType()), querySetup);
    }

    default int processUpdate(DbQuery query) {
        return processUpdate(query.parse(dbType()));
    }

    default void processQuery(DbQuery query, DbQuerySetup querySetup, DbQueryProcess queryProcess) {
        processQuery(query.parse(dbType()), querySetup, queryProcess);
    }

    default void processQuery(DbQuery query, DbQueryProcess queryProcess) {
        processQuery(query.parse(dbType()), queryProcess);
    }

    default <T> T processQuery(DbQuery query, DbQuerySetup querySetup, DbQueryRetrieveData<T> queryRetrieveData) {
        return processQuery(query.parse(dbType()), querySetup, queryRetrieveData);
    }

    default <T> T processQuery(DbQuery query, DbQueryRetrieveData<T> retrieveData) {
        return processQuery(query.parse(dbType()), retrieveData);
    }

    default void processUpdates(DbQuery query, DbUpdates updates) {
        processUpdates(query.parse(dbType()), updates);
    }

    default void processQueries(DbQuery query, DbQueriesNoReturn queries) {
        processQueries(query.parse(dbType()), queries);
    }

    default <T> T processQueries(DbQuery query, DbQueries<T> queries) {
        return processQueries(query.parse(dbType()), queries);
    }

}
