package org.beanmaker.v2.runtime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseEditableView extends BaseView {

    protected List<ErrorMessage> errorMessages = new ArrayList<>();
    protected List<WarningMessage> warningMessages = new ArrayList<>();

    public BaseEditableView(DbBeanLocalization dbBeanLocalization) {
        super(dbBeanLocalization);
    }

    public List<ErrorMessage> getErrorMessages() {
        return Collections.unmodifiableList(errorMessages);
    }

    public List<WarningMessage> getWarningMessages() {
        return Collections.unmodifiableList(warningMessages);
    }

}
