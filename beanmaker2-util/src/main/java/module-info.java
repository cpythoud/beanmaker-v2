@SuppressWarnings("module")
module org.beanmaker.v2.util {
    requires java.sql;
    requires java.naming;
    requires java.net.http;

    exports org.beanmaker.v2.util;
    exports org.beanmaker.v2.util.testing;
}
