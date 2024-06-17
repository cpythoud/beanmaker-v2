package org.beanmaker.v2.runtime;

import java.util.Locale;
import java.util.Optional;

public interface DbBeanLanguage extends DbBeanInterface {

    String getName();

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

}
