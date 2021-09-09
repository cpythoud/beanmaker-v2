package org.beanmaker.v2.runtime;

public class Codes {

    public static void adjustCopyCode(DbBeanWithCode copy) {
        copy.setCode(copy.getCode() + "_copy");
        if (!copy.isCodeUnique()) {
            String baseCode = copy.getCode();
            int index = 2;
            do {
                copy.setCode(baseCode + index);
                ++index;
            } while (!copy.isCodeUnique());
        }
    }
}
