@SuppressWarnings("module")
module org.beanmaker.v2.runtime {
    requires java.sql;
    requires javax.servlet.api;

    requires org.beanmaker.v2.util;
    requires org.dbbeans.sql;
    requires org.jcodegen.html;
    requires rodeo.password.pgencheck;

    requires org.apache.commons.csv;
    requires org.apache.commons.fileupload2.javax;
    requires org.apache.commons.fileupload2.core;

    exports org.beanmaker.v2.runtime;
    exports org.beanmaker.v2.runtime.annotations;
    exports org.beanmaker.v2.runtime.dbutil;
    exports org.beanmaker.v2.runtime.util;
}
