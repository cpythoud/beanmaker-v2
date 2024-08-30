package org.beanmaker.v2.runtime.csv;

import org.beanmaker.v2.runtime.DbBeanEditor;
import org.beanmaker.v2.runtime.dbutil.Transactions;

import org.dbbeans.sql.DBTransaction;

import java.lang.reflect.InvocationTargetException;

import java.util.HashMap;
import java.util.Map;

public abstract class BeanImportBase implements DbBeanCsvImport {

    private final DataFile dataFile;
    private final DataEntries dataEntries;
    private final Map<String, String> fieldToHeaderMap;
    private final DbBeanEditor editor;
    private final Map<String, Boolean> booleanMappings;

    private boolean booleanLenientParsing = false;

    private DBTransaction dbTransaction;

    public BeanImportBase(DataFile dataFile, Class<?> editorClass, String... fields) {
        this.dataFile = dataFile;
        dataEntries = dataFile.parseFile();
        fieldToHeaderMap = createDefaultFieldToHeaderMap(fields);
        editor = createEditor(editorClass);
        booleanMappings = DataValidator.getDefaultBooleanValues();
    }

    private Map<String, String> createDefaultFieldToHeaderMap(String... fields) {
        var map = new HashMap<String, String>();
        for (String field: fields)
            map.put(field, field);
        return map;
    }

    private DbBeanEditor createEditor(Class<?> editorClass) {
        DbBeanEditor editor;
        try {
            var constructor = editorClass.getConstructor();
            editor = (DbBeanEditor) constructor.newInstance();
        } catch (InvocationTargetException | NoSuchMethodException | InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return editor;
    }

    protected DataFile getDataFile() {
        return dataFile;
    }

    protected DataEntries getDataEntries() {
        return dataEntries;
    }

    protected Map<String, String> getFieldToHeaderMap() {
        return fieldToHeaderMap;
    }

    protected DbBeanEditor getEditor() {
        return editor;
    }

    protected DBTransaction getDbTransaction() {
        return dbTransaction;
    }

    protected Map<String, Boolean> getBooleanMappings() {
        return booleanMappings;
    }

    public boolean isBooleanParsingLenient() {
        return booleanLenientParsing;
    }

    public void setBooleanLenientParsing(boolean booleanLenientParsing) {
        this.booleanLenientParsing = booleanLenientParsing;
    }

    @Override
    public void importData(DBTransaction dbTransaction) {
        importData(dbTransaction, DataValidator.ALWAYS_TRUST);
    }

    @Override
    public void importData(DBTransaction dbTransaction, DataValidator validator) {
        this.dbTransaction = dbTransaction;
        Transactions.wrap(
                transaction -> {
                    for (var dataEntry: dataEntries) {
                        setupEditor(dataEntry);
                        setFields(dataEntry);
                        if (validator.validate(editor, dataEntry))
                            editor.updateDB(transaction);
                    }
                },
                dbTransaction
        );
    }

    protected void setupEditor(DataEntry dataEntry) {
        long id = retrieveId(dataEntry);
        if (id == 0)
            editor.fullReset();
        else
            editor.setId(id, dbTransaction);
    }

    protected long retrieveId(DataEntry dataEntry) {
        return dataEntry.getLongValue("id");
    }

    protected abstract void setFields(DataEntry dataEntry);

    protected String getStringValue(DataEntry dataEntry, String field) {
        return dataEntry.getStringValue(fieldToHeaderMap.get(field));
    }

    protected long getLongValue(DataEntry dataEntry, String field) {
        return dataEntry.getLongValue(fieldToHeaderMap.get(field));
    }

    protected Boolean getBooleanValue(DataEntry dataEntry, String field) {
        return dataEntry.getBooleanValue(fieldToHeaderMap.get(field), booleanMappings, booleanLenientParsing);
    }

    protected Integer getIntegerValue(DataEntry dataEntry, String field) {
        return dataEntry.getIntegerValue(fieldToHeaderMap.get(field));
    }

}
