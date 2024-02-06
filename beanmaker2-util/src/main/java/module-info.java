@SuppressWarnings("module")
module org.beanmaker.v2.util {
    requires java.sql;
    requires java.naming;

    exports org.beanmaker.v2.util;
    exports org.beanmaker.v2.util.logging;
    exports org.beanmaker.v2.util.testing;
}
