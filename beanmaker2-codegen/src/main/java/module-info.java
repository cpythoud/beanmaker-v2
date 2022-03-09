@SuppressWarnings("module")
module org.beanmaker.v2.codegen {
    requires java.sql;
    requires org.beanmaker.v2.util;
    requires org.jcodegen.java;
    requires org.jcodegen.html;
    requires org.dbbeans.sql;

    exports org.beanmaker.v2.codegen;
}