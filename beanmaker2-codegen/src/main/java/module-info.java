@SuppressWarnings("module")
module org.beanmaker.v2.codegen {
    requires java.sql;
    requires org.beanmaker.v2.util;
    requires org.beanmaker.v2.database.sql;
    requires org.beanmaker.v2.codegen.java;
    requires org.beanmaker.v2.codegen.html;

    exports org.beanmaker.v2.codegen;
}
