package org.beanmaker.v2.util;

import java.text.BreakIterator;
import java.text.Normalizer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public final class StringFilter {

    public enum WordMode {
        OR,
        AND
    }

    private StringFilter() { }

    public static <T> List<T> containsIgnoreCaseAndAccents(
            Collection<T> values,
            String query,
            Locale locale,
            Function<T, String> textExtractor)
    {
        List<T> result = new ArrayList<>();
        if (values == null || query == null || query.isBlank() || textExtractor == null) {
            return result;
        }

        Locale effectiveLocale = effectiveLocale(locale);
        String needle = normalize(query, effectiveLocale);

        for (T value : values) {
            if (value == null) {
                continue;
            }

            String text = textExtractor.apply(value);
            if (text != null && normalize(text, effectiveLocale).contains(needle)) {
                result.add(value);
            }
        }

        return result;
    }

    public static <T> List<T> wholeWordsIgnoreCaseAndAccents(
            Collection<T> values,
            String query,
            WordMode mode,
            Locale locale,
            Function<T, String> textExtractor)
    {
        List<T> result = new ArrayList<>();
        if (values == null || query == null || query.isBlank() || mode == null || textExtractor == null) {
            return result;
        }

        Locale effectiveLocale = effectiveLocale(locale);
        String[] terms = splitTerms(query, effectiveLocale);
        if (terms.length == 0) {
            return result;
        }

        for (T value : values) {
            if (value == null) {
                continue;
            }

            String text = textExtractor.apply(value);
            if (text == null) {
                continue;
            }

            List<String> wordsInValue = extractWords(text, effectiveLocale);
            boolean matches = mode == WordMode.AND
                    ? matchesAll(wordsInValue, terms)
                    : matchesAny(wordsInValue, terms);

            if (matches) {
                result.add(value);
            }
        }

        return result;
    }

    private static Locale effectiveLocale(Locale locale) {
        return locale != null ? locale : Locale.getDefault();
    }

    private static String normalize(String text, Locale locale) {
        String lower = text.toLowerCase(locale);
        String decomposed = Normalizer.normalize(lower, Normalizer.Form.NFD);
        return decomposed.replaceAll("\\p{M}+", "");
    }

    private static String[] splitTerms(String query, Locale locale) {
        return normalize(query, locale).trim().split("\\s+");
    }

    private static List<String> extractWords(String text, Locale locale) {
        List<String> words = new ArrayList<>();
        BreakIterator iterator = BreakIterator.getWordInstance(locale);
        iterator.setText(text);

        int start = iterator.first();
        for (int end = iterator.next(); end != BreakIterator.DONE; start = end, end = iterator.next()) {
            String part = text.substring(start, end).trim();
            if (!part.isEmpty() && Character.isLetterOrDigit(part.codePointAt(0))) {
                words.add(normalize(part, locale));
            }
        }
        return words;
    }

    private static boolean matchesAll(List<String> words, String[] terms) {
        for (String term : terms) {
            if (!containsWord(words, term)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesAny(List<String> words, String[] terms) {
        for (String term : terms) {
            if (containsWord(words, term)) {
                return true;
            }
        }
        return false;
    }

    private static boolean containsWord(List<String> words, String term) {
        for (String word : words) {
            if (word.equals(term)) {
                return true;
            }
        }
        return false;
    }

}
