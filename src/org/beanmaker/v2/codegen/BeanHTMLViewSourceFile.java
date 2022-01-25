package org.beanmaker.v2.codegen;

import org.jcodegen.java.FunctionArgument;
import org.jcodegen.java.FunctionCall;
import org.jcodegen.java.Visibility;

import static org.beanmaker.v2.util.Strings.uncapitalize;

public class BeanHTMLViewSourceFile extends BeanCode {

    private final String editorClass;
    private final String editorObject;

    public BeanHTMLViewSourceFile(String beanName, String packageName) {
        super(beanName, packageName, null, "HTMLView");

        editorClass = beanName + "Editor";
        editorObject = uncapitalize(editorClass);

        createSourceCode();
    }

    @Override
    protected void addImports() {
        importsManager.addImport("org.beanmaker.v2.runtime.DbBeanLanguage");
    }

    @Override
    protected void decorateJavaClass() {
        javaClass.visibility(Visibility.PUBLIC).extendsClass(beanName + "HTMLViewBase");
    }

    @Override
    protected void addConstructors() {
        javaClass
                .addContent(javaClass.createConstructor()
                        .visibility(Visibility.PUBLIC)
                        .addArgument(new FunctionArgument(editorClass, editorObject))
                        .addArgument(new FunctionArgument("DbBeanLanguage", "dbBeanLanguage"))
                        .addContent(new FunctionCall("super")
                                .byItself()
                                .addArguments(editorObject, "dbBeanLanguage")))
                .addContent(EMPTY_LINE);
    }

    @Override
    protected void addCoreFunctionality() { }

}
