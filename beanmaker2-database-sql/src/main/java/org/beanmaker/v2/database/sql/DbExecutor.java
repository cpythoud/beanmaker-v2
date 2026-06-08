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

}
