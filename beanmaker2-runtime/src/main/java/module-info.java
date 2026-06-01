@SuppressWarnings("module")
module org.beanmaker.v2.runtime {
    requires java.sql;
    requires javaee.web.api;

    requires org.beanmaker.v2.util;
    requires org.beanmaker.v2.database.sql;
    requires org.beanmaker.v2.codegen.html;
    requires rodeo.password.pgencheck;

    requires commons.fileupload;
    requires org.apache.commons.csv;

    requires org.slf4j;
    requires org.json;

    exports org.beanmaker.v2.runtime;
    exports org.beanmaker.v2.runtime.annotations;
    exports org.beanmaker.v2.runtime.dbutil;
    exports org.beanmaker.v2.runtime.util;
}
