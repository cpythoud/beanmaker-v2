package org.beanmaker.v2.codegen;

public class OneToManyRelationship {

    private final String beanClass;
    private final String javaName;
    private final String table;
    private final String idSqlName;

    public OneToManyRelationship(String beanClass, String javaName, String table, String idSqlName) {
        this.beanClass = beanClass;
        this.javaName = javaName;
        this.table = table;
        this.idSqlName = idSqlName;
    }

    public String getBeanClass() {
        return beanClass;
    }

    public String getJavaName() {
        return javaName;
    }

    public String getTable() {
        return table;
    }

    public String getIdSqlName() {
        return idSqlName;
    }

}
