package org.beanmaker.v2.codegen.java;

public class Configuration {

    private static Configuration currentConfiguration = new Configuration(Visibility.PACKAGE_PRIVATE);

    private final Visibility defaultDeclarationVisibility;

    private Configuration(Visibility defaultDeclarationVisibility) {
        this.defaultDeclarationVisibility = defaultDeclarationVisibility;
    }

    public static ConfigurationBuilder builder() {
        return new ConfigurationBuilder();
    }

    public static void setCurrentConfiguration(Configuration configuration) {
        currentConfiguration = configuration;
    }

    public static Configuration getCurrentConfiguration() {
        return currentConfiguration;
    }

    public Visibility getDefaultDeclarationVisibility() {
        return defaultDeclarationVisibility;
    }

    public static class ConfigurationBuilder {

        private Visibility defaultDeclarationVisibility;

        private ConfigurationBuilder() { }

        public ConfigurationBuilder setDefaultDeclarationVisibility(Visibility defaultDeclarationVisibility) {
            this.defaultDeclarationVisibility = defaultDeclarationVisibility;
            return this;
        }

        public Configuration create() {
            if (defaultDeclarationVisibility == null)
                throw new IllegalStateException("Missing parameter: defaultDeclarationVisibility");

            return new Configuration(defaultDeclarationVisibility);
        }

    }

}
