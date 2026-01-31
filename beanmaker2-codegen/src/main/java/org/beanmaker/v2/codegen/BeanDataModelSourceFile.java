package org.beanmaker.v2.codegen;

import org.beanmaker.v2.util.Version;
import org.jcodegen.java.Visibility;

import java.time.Instant;

import static org.beanmaker.v2.codegen.BaseCode.DEFAULT_PROJECT_PARAMETERS;

public class BeanDataModelSourceFile extends BaseInterfaceCode {

    public BeanDataModelSourceFile(String beanName, String packageName) {
        this(beanName, packageName, DEFAULT_PROJECT_PARAMETERS);
    }

    public BeanDataModelSourceFile(String beanName, String packageName, ProjectParameters projectParameters) {
        super(beanName, packageName, "DataModel", null, projectParameters);

        createSourceCode();
    }

    @Override
    protected void addGeneratedAnnotation() {
        importsManager.addImport("org.beanmaker.v2.runtime.annotations.Editable");

        String annotation = "@Editable(generator = \"%s\", version = \"%s\", date = \"%s\")".formatted(
                getClass().getName(), Version.get(), Instant.now().toString());

        javaInterface.annotate(annotation);
    }

    @Override
    protected void decorateJavaInterface() {
        javaInterface.visibility(Visibility.PUBLIC).extendsInterface(beanName + "DataModelBase");
    }

    @Override
    protected void addFunctionDeclarations() { }

}
