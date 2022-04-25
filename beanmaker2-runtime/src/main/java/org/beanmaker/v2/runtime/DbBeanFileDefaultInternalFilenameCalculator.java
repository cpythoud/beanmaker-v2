package org.beanmaker.v2.runtime;

import rodeo.password.pgencheck.CharacterGroups;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DbBeanFileDefaultInternalFilenameCalculator implements DbBeanFileInternalFilenameCalculator {

    private static final Set<Character> ACCEPTABLE_FILENAME_CHARACTERS;

    static {
        ACCEPTABLE_FILENAME_CHARACTERS = new HashSet<>();
        for (String charList: List.of(CharacterGroups.LOWER_CASE, CharacterGroups.UPPER_CASE, CharacterGroups.DIGITS))
            for (char character: charList.toCharArray())
                ACCEPTABLE_FILENAME_CHARACTERS.add(character);
    }

    private static final int MAX_FILENAME_LENGTH = 8;
    private static final char FILLER_CHARACTER = 'x';

    @Override
    public String calc(String originalFilename) {
        return extractMainName(originalFilename) + extractExtension(originalFilename);
    }

    private String extractMainName(String originalFilename) {
        String mainPart;
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex == -1)
            mainPart = originalFilename;
        else
            mainPart = originalFilename.substring(0, lastDotIndex);

        StringBuilder mainName = new StringBuilder();

        int count = 0;
        for (char character: mainPart.toCharArray()) {
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

    private String extractExtension(String originalFilename) {
        var parts = originalFilename.split("\\.");
        if (parts.length < 2)
            return "";

        return "." + parts[parts.length - 1];
    }

}
