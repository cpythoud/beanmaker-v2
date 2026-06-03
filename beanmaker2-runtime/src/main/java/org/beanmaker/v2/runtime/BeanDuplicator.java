package org.beanmaker.v2.runtime;

import org.beanmaker.v2.database.sql.DbTransaction;

import org.beanmaker.v2.util.DecimalValue;
import org.beanmaker.v2.util.Money;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

public interface BeanDuplicator {

    Boolean duplicateBoolean(Boolean value);

    Integer duplicateInteger(Integer value);

    Long duplicateLong(Long value);

    String duplicateString(String value);

    Date duplicateDate(Date date);

    Time duplicateTime(Time time);

    Timestamp duplicateTimestamp(Timestamp timestamp);

    Money duplicateMoney(Money money);

    DecimalValue duplicateDecimalValue(DecimalValue value);

    long duplicateLabel(DbBeanLabel label);

    long duplicateLabel(long idLabel, DbTransaction transaction);

    long duplicateBean(DbBeanEditorInterface editor);

    long duplicateIdBean(long id);

    long duplicateFile(long id);

    int getNextBeanVersion(VersionedBean bean);

    long getOriginalBeanId(VersionedBean bean);

}
