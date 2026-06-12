package org.beanmaker.v2.database.sql;

public interface DbExecutor {

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


    long createRecord(SecureQuery query, DbQuerySetup querySetup);

    int processUpdate(SecureQuery query, DbQuerySetup querySetup);
    int processUpdate(SecureQuery query);

    void processQuery(SecureQuery query, DbQuerySetup querySetup, DbQueryProcess queryProcess);
    void processQuery(SecureQuery query, DbQueryProcess queryProcess);

    <T> T processQuery(SecureQuery query, DbQuerySetup querySetup, DbQueryRetrieveData<T> queryRetrieveData);
    <T> T processQuery(SecureQuery query, DbQueryRetrieveData<T> retrieveData);

    void processUpdates(SecureQuery query, DbUpdates updates);

    void processQueries(SecureQuery query, DbQueriesNoReturn queries);
    <T> T processQueries(SecureQuery query, DbQueries<T> queries);


    long createRecord(DbQuery query, DbQuerySetup querySetup);

    int processUpdate(DbQuery query, DbQuerySetup querySetup);
    int processUpdate(DbQuery query);

    void processQuery(DbQuery query, DbQuerySetup querySetup, DbQueryProcess queryProcess);
    void processQuery(DbQuery query, DbQueryProcess queryProcess);

    <T> T processQuery(DbQuery query, DbQuerySetup querySetup, DbQueryRetrieveData<T> queryRetrieveData);
    <T> T processQuery(DbQuery query, DbQueryRetrieveData<T> retrieveData);

    void processUpdates(DbQuery query, DbUpdates updates);

    void processQueries(DbQuery query, DbQueriesNoReturn queries);
    <T> T processQueries(DbQuery query, DbQueries<T> queries);

}
