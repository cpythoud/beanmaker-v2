@SuppressWarnings("module")
module beanmaker.v2.email.smtp {
    requires beanmaker.v2.email;
    requires commons.email;
    requires javax.mail;

    exports org.beanmaker.v2.email.smtp;
}
