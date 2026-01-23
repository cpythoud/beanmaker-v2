package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.DecimalValue;
import org.beanmaker.v2.util.Money;

import org.dbbeans.sql.DBTransaction;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public class DefaultBeanDuplicator implements BeanDuplicator {

    private final DBTransaction transaction;
    private final DbBeanLabelBasicFunctions labelBasicFunctions;
    private final DbBeanParameters parameters;

    public DefaultBeanDuplicator(
            DBTransaction transaction,
            DbBeanLabelBasicFunctions labelBasicFunctions,
            DbBeanParameters parameters)
    {
        this.transaction = transaction;
        this.labelBasicFunctions = labelBasicFunctions;
        this.parameters = parameters;
    }

    @Override
    public Boolean duplicateBoolean(Boolean value) {
        return value;
    }

    @Override
    public Integer duplicateInteger(Integer value) {
        return value;
    }

    @Override
    public Long duplicateLong(Long value) {
        return value;
    }

    @Override
    public String duplicateString(String value) {
        return value;
    }

    @Override
    public Date duplicateDate(Date date) {
        return DBUtil.copy(date);
    }

    @Override
    public Time duplicateTime(Time time) {
        return DBUtil.copy(time);
    }

    @Override
    public Timestamp duplicateTimestamp(Timestamp timestamp) {
        return DBUtil.copy(timestamp);
    }

    @Override
    public Money duplicateMoney(Money money) {
        return money;  // * Money is immutable
    }

    @Override
    public DecimalValue duplicateDecimalValue(DecimalValue value) {
        return value;  // * DecimalValue is immutable
    }

    @Override
    public long duplicateLabel(DbBeanLabel label) {
        return labelBasicFunctions.duplicateLabel(label, transaction);
    }

    @Override
    public long duplicateBean(DbBeanEditorInterface editor) {
        var duplicata = editor.duplicate(transaction);
        duplicata.updateDB(transaction);
        return duplicata.getId();
    }

    @Override
    public long duplicateFile(long id) {
        return id;  // * For now
    }

    @Override
    public int getNextBeanVersion(VersionedBean bean) {
        long id = getOriginalBeanId(bean);
        return transaction.addQuery(
                "SELECT MAX(bean_version) FROM %s WHERE id=? OR id_original_bean=?"
                        .formatted(parameters.getDatabaseTableName()),
                stat -> {
                    stat.setLong(1, id);
                    stat.setLong(2, id);
                },
                rs -> {
                    rs.next();
                    return rs.getInt(1) + 1;
                }
        );
    }

    @Override
    public long getOriginalBeanId(VersionedBean bean) {
        long id = bean.getIdOriginalBean();
        if (id == 0)
            return bean.getId();
        return id;
    }

}
