package org.beanmaker.v2.codegen.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * ...
 */
public class ImportsManager {

    private final Set<String> imports = new HashSet<>();
    private final Set<String> staticImports = new HashSet<>();

    private static final Pattern importValidationPattern;

    static {
        try {
            importValidationPattern = Pattern.compile("([_a-zA-Z][_\\w]+\\.)+[_a-zA-Z][_\\w]+(\\.\\*)?");
        } catch (PatternSyntaxException pse) {
            throw new RuntimeException(pse);
        }
    }


    public void addImport(String importStr) {
        Matcher matcher = importValidationPattern.matcher(importStr);
        if (!matcher.matches())
            throw new IllegalArgumentException("Illegal character in import name: " + importStr);

        imports.add(importStr);
    }

    public void addStaticImport(String importStr) {
        Matcher matcher = importValidationPattern.matcher(importStr);
        if (!matcher.matches())
            throw new IllegalArgumentException("Illegal character in import name: " + importStr);

        staticImports.add(importStr);
    }

    public void clear() {
        imports.clear();
        staticImports.clear();
    }

    public int getCount() {
        return imports.size();
    }

    public int getStaticImportCount() {
        return staticImports.size();
    }

    public String getImports() {
        List<String> javaImports = new ArrayList<>();
        List<String> otherImports = new ArrayList<>();

        for (String importStr: imports) {
            if (importStr.startsWith("java.") || importStr.startsWith("javax."))
                javaImports.add(importStr);
            else
                otherImports.add(importStr);
        }

        StringBuilder buf = new StringBuilder();
        if (!javaImports.isEmpty()) {
            Collections.sort(javaImports);
            addImports(buf, javaImports);
            buf.append("\n");
        }
        if (!otherImports.isEmpty()) {
            Collections.sort(otherImports);
            addImports(buf, otherImports);
            buf.append("\n");
        }

        if (!staticImports.isEmpty()) {
            addStaticImports(buf);
            buf.append("\n");
        }

        return buf.toString();
    }

    private void addImports(StringBuilder buf, List<String> imports) {
        String lastSubPackageName = "";

        for (String importStr: imports) {
            String subPackageName = getPackageName(importStr);

            if (!subPackageName.equals(lastSubPackageName) && !lastSubPackageName.isEmpty())
                buf.append("\n");

            buf.append("import ");
            buf.append(importStr);
            buf.append(";\n");

            lastSubPackageName = subPackageName;
        }
    }

    private void addStaticImports(StringBuilder buf) {
        List<String> imports = new ArrayList<>(staticImports);
        Collections.sort(imports);

        String lastClassName = "";

        for (String importStr: imports) {
            String className = getPackageName(importStr);

            if (!className.equals(lastClassName) && !lastClassName.isEmpty())
                buf.append("\n");

            buf.append("import static ");
            buf.append(importStr);
            buf.append(";\n");

            lastClassName = className;
        }
    }

    private String getPackageName(String importStr) {
        return importStr.substring(0, importStr.lastIndexOf("."));
    }

}
