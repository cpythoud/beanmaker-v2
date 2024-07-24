package org.beanmaker.v2.codegen;

public class DefaultProjectParameters implements ProjectParameters {

    @Override
    public boolean createEditorFieldsConstructor() {
        return false;
    }

    @Override
    public boolean createSealedClasses() {
        return false;
    }

    @Override
    public boolean createDatabaseProviderReference() {
        return false;
    }

}
