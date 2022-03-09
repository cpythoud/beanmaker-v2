package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.PasswordMakerCharacterSets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbBeanFileDefaultInternalFilenameCalculator implements DbBeanFileInternalFilenameCalculator {

    private static final Set<Character> ACCEPTABLE_FILENAME_CHARACTERS;

    static {
        ACCEPTABLE_FILENAME_CHARACTERS = new HashSet<>();
        for (String charList: List.of(PasswordMakerCharacterSets.LOWER_CASES, PasswordMakerCharacterSets.UPPER_CASES, PasswordMakerCharacterSets.DIGITS))
            for (char character: charList.toCharArray())
                ACCEPTABLE_FILENAME_CHARACTERS.add(character);
    }

    private static final int MAX_FILENAME_LENGTH = 8;
    private static final char FILLER_CHARACTER = 'x';

    @Override
    public String calc(String orginalFilename) {
        return extractMainName(orginalFilename) + extractExtension(orginalFilename);
    }

    private String extractMainName(String orginalFilename) {
        StringBuilder mainName = new StringBuilder();

        int count = 0;
        for (char character: orginalFilename.toCharArray()) {
            if (count == MAX_FILENAME_LENGTH)
                return mainName.toString();
            if (ACCEPTABLE_FILENAME_CHARACTERS.contains(character)) {
                mainName.append(character);
                ++count;
            }
        }

        for (int i = count; i < MAX_FILENAME_LENGTH; ++i)
            mainName.append(FILLER_CHARACTER);

        return mainName.toString();
    }

    private String extractExtension(String orginalFilename) {
        var parts = orginalFilename.split("\\.");
        if (parts.length < 2)
            return "";

        return "." + parts[parts.length - 1];
    }
}
