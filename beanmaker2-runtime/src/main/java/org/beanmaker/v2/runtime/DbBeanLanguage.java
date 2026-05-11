package org.beanmaker.v2.runtime;

import org.beanmaker.v2.util.DecimalValueFormat;

import java.util.Locale;
import java.util.Optional;

/**
 * Represents a language entity in the database that extends general properties
 * from {@link DbBeanInterface}. This interface provides various methods for working
 * with language-related properties such as ISO codes, region codes, tags, and
 * more. It also includes utility methods for handling localization-related tasks.
 * <p>
 * Your application should contain a 'Language' class that implements this interface. See Beanmaker documentation.
 */
public interface DbBeanLanguage extends DbBeanInterface {

    String getName();

    default String getName(DbBeanLanguage language) {
        return getName();
    }

    String getIso();

    default Optional<String> getRegionCode() {
        return Optional.empty();
    }

    default boolean hasRegion() {
        return getRegionCode().isPresent();
    }

    default String getTag() {
        return getRegionCode().map(code -> getIso() + "-" + code).orElseGet(this::getIso);
    }

    default String getFieldLabelSuffix() {
        return " " + getTag();
    }

    default String getFieldLabelSuffix(DbBeanLanguage language) {
        return getFieldLabelSuffix();
    }

    default String getFieldLabelSuffix(DbBeanLocalization localization) {
        return getFieldLabelSuffix(localization.getLanguage());
    }

    /**
     * Returns the bare language of the {@link DbBeanLanguage} instance.
     * The bare language represents the language without any region code.
     * <p>
     * The default implementation is only suitable if your application doesn't use region codes. If it does,
     * this function needs to be overloaded to return the corresponding language without region code. If the
     * language is a bare language, this function should return 'this'.
     *
     * @return the bare language
     */
    default DbBeanLanguage getBareLanguage() {
        return this;
    }

    default boolean isBareLanguage() {
        return !hasRegion();
    }

    default String getCapIso() {
        return getIso().toUpperCase();
    }

    default Locale getLocale() {
        return Locale.forLanguageTag(getTag());
    }

    default String getCapTag() {
        return getRegionCode()
                .map(code -> getCapIso() + " (" + code.toUpperCase() + ")")
                .orElseGet(this::getCapIso);
    }


    /**
     * Checks if the language is the default language.
     * <p>
     * Please note that the return type needs to be Boolean and not boolean in case this method is overloaded
     * by a function in the generated base class because of a database table field called default_language.
     * @return {@code true} if the language is the default language, {@code false} otherwise.
     */
    default Boolean isDefaultLanguage() {
        return false;
    }


    /**
     * Determines whether a space is needed before high punctuation characters.
     * High punctuation characters typically include symbols like colon, semicolon, etc.
     * This can be useful when tailoring text formatting for specific languages or regions.
     *
     * @return {@code true} if a space is required before high punctuation characters,
     *         {@code false} otherwise.
     */
    default boolean needSpaceBeforeHighPunctuation() {
        return false;
    }

    /**
     * Returns a colon character ":" which may optionally be preceded by a space,
     * depending on the result of the {@code needSpaceBeforeHighPunctuation()} method.
     * If the {@code needSpaceBeforeHighPunctuation()} method returns {@code true},
     * the colon is preceded by a space (" :"); otherwise, it returns the colon without a space (":").
     *
     * @return a colon character, optionally preceded by a space, depending on language-specific formatting rules.
     */
    default String colon() {
        return needSpaceBeforeHighPunctuation() ? " :" : ":";
    }

    /**
     * Returns a semicolon (";") which may optionally be preceded by a space,
     * depending on the result of the {@code needSpaceBeforeHighPunctuation()} method.
     * If {@code needSpaceBeforeHighPunctuation()} returns {@code true}, the semicolon
     * is preceded by a space (" ;"); otherwise, it is returned without a space (";").
     *
     * @return a semicolon character, optionally preceded by a space, depending on language-specific formatting rules.
     */
    default String semicolon() {
        return needSpaceBeforeHighPunctuation() ? " ;" : ";";
    }

    /**
     * Retrieves the default {@link DecimalValueFormat} for representing decimal values.
     * The default implementation returns {@link DecimalValueFormat#DOT}, which uses a dot (".")
     * as the decimal separator.
     *
     * @return the default {@link DecimalValueFormat} instance, typically {@link DecimalValueFormat#DOT}.
     */
    default DecimalValueFormat decimalValueFormat() {
        return DecimalValueFormat.DOT;
    }

}
