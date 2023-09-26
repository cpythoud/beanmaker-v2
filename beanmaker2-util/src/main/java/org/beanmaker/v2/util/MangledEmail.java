package org.beanmaker.v2.util;

import java.util.ArrayList;
import java.util.List;

public class MangledEmail {

    private static final int VARNAME_LENGTH = 16;

    private final String javascriptFunctionName;
    private final String javascriptFunction;

    private MangledEmail(String email, String functionName, String linkText, String cssClass) {
        javascriptFunctionName = functionName;

        String mailtoVarName = getRandomVarName();
        String emailVarName = getRandomVarName();
        var mailtoCodes = getCharacterCodes("mailto:" + email);
        var emailCodes = getCharacterCodes(email);

        var jsFunction = new StringBuilder();
        jsFunction.append("<script type=\"text/javascript\">\n//<![CDATA[\nfunction ");
        jsFunction.append(functionName);
        jsFunction.append("()\n{\nconst ");
        jsFunction.append(mailtoVarName);
        jsFunction.append("=[");
        for (int i = 0; i < mailtoCodes.size(); i++) {
            jsFunction.append("\"");
            jsFunction.append(mailtoCodes.get(i));
            jsFunction.append("\"");
            if (i < mailtoCodes.size() - 1)
                jsFunction.append(",");
        }
        if (linkText == null) {
            jsFunction.append("];\nconst ");
            jsFunction.append(emailVarName);
            jsFunction.append("=[");
            for (int i = 0; i < emailCodes.size(); i++) {
                jsFunction.append("\"");
                jsFunction.append(emailCodes.get(i));
                jsFunction.append("\"");
                if (i < emailCodes.size() - 1)
                    jsFunction.append(",");
            }
        }
        jsFunction.append("];\ndocument.write(\"<a");
        if (cssClass != null) {
            jsFunction.append(" class='");
            jsFunction.append(cssClass);
            jsFunction.append("'");
        }
        jsFunction.append(" href='\");\nfor (let i=0; i<");
        jsFunction.append(mailtoVarName);
        jsFunction.append(".length; i++) document.write(\"&#\"+");
        jsFunction.append(mailtoVarName);

        if (linkText == null) {
            jsFunction.append("[i]+\";\");\ndocument.write(\"'>\")\nfor (let i=0; i<");
            jsFunction.append(emailVarName);
            jsFunction.append(".length; i++) document.write(\"&#\"+");
            jsFunction.append(emailVarName);
            jsFunction.append("[i]+\";\");\ndocument.write(\"<\\/a>\");\n}\n//]]>\n</script>\n");
        } else {
            jsFunction.append("[i]+\";\");\ndocument.write(\"'>");
            jsFunction.append(linkText);
            jsFunction.append("</a>\");\n}\n//]]>\n</script>\n");
        }

        javascriptFunction = jsFunction.toString();
    }

    private static String getRandomVarName() {
        final String alphaRandom = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String alphaNumRandom = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        var varName = new StringBuilder();
        varName.append(alphaRandom.charAt((int) (Math.random() * alphaRandom.length())));
        for (int i = 0; i < VARNAME_LENGTH; i++)
            varName.append(alphaNumRandom.charAt((int) (Math.random() * alphaNumRandom.length())));
        return varName.toString();
    }

    private static List<String> getCharacterCodes(String s) {
        var codes = new ArrayList<String>();
        for (int i = 0; i < s.length(); i++) {
            if (Math.round(Math.random()) == 0)
                codes.add(Integer.toString(s.codePointAt(i)));
            else
                codes.add("x" + String.format("%x", s.codePointAt(i)));
        }
        return codes;
    }

    public String getJavascriptFunction() {
        return javascriptFunction;
    }

    public String getJavascriptFunctionName() {
        return javascriptFunctionName;
    }

    public static class Builder {

        private final String email;
        private final String functionName;
        private String linkText;
        private String cssClass;

        public Builder(String email, String functionName) {
            if (email == null)
                throw new NullPointerException("email cannot be null");
            if (!Emails.validate(email))
                throw new IllegalArgumentException("Not a valid email: " + email);
            if (functionName == null)
                throw new NullPointerException("functionName cannot be null");
            if (functionName.isEmpty())
                throw new IllegalArgumentException("functionName cannot be empty");

            this.email = email;
            this.functionName = functionName;
        }

        public Builder setLinkText(String linkText) {
            if (linkText == null)
                throw new NullPointerException("linkText cannot be null");
            if (linkText.isEmpty())
                throw new IllegalArgumentException("linkText cannot be empty");

            this.linkText = linkText;
            return this;
        }

        public Builder setCssClass(String cssClass) {
            this.cssClass = cssClass;
            return this;
        }

        public MangledEmail build() {
            return new MangledEmail(email, functionName, linkText, cssClass);
        }

    }

}
