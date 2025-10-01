package org.beanmaker.v2.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DecimalValueTest {

    private static final DecimalValueParser STANDARD_PARSER = DecimalValueParser.create(".");
    private static final DecimalValueParser COMMA_SPACE_PARSER =
            DecimalValueParser.builder().addSeparator(",").addDecorator(" ").decimals(3).build();
    private static final DecimalValueParser LENIENT_PARSER =
            DecimalValueParser.builder().addSeparator(".").decimals(3).lenient(true).build();
    
    private static final DecimalValueFormat COMMA_APOSTROPHY = new DecimalValueFormat(",", "'");

    @Test
    public void testBasicParsing() {
        DecimalValue decimalValue = STANDARD_PARSER.parse("123.456", 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        assertEquals(123456, decimalValue.toInt());
        assertEquals(123456, decimalValue.toLong());
        assertEquals("123.456", decimalValue.toBigDecimal().toString());
        assertEquals("123.456", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("123,456", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("123,456", COMMA_APOSTROPHY.format(decimalValue));

        decimalValue = DecimalValue.from(123456, 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(123456L, 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(new BigDecimal("123.456"), 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());
    }

    @Test
    public void testParseLargeNumber() {
        DecimalValue decimalValue = COMMA_SPACE_PARSER.parse("634 412 123,456");
        assertEquals(634412123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        var ex = assertThrows(NumberFormatException.class, decimalValue::toInt);
        assertEquals("DecimalValue cannot be converted to int", ex.getMessage());
        assertEquals(634412123456L, decimalValue.toLong());
        assertEquals("634412123.456", decimalValue.toBigDecimal().toString());
        assertEquals("634412123.456", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("634412123,456", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("634'412'123,456", COMMA_APOSTROPHY.format(decimalValue));

        // * DecimalValue.from(int) cannot be used here

        decimalValue = DecimalValue.from(634412123456L, 3);
        assertEquals(634412123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(new BigDecimal("634412123.456"), 3);
        assertEquals(634412123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        DecimalValueParser parser = DecimalValueParser.builder()
                .addSeparator(",")
                .addDecorator(" ")
                .addDecorator("'")
                .decimals(3)
                .build();
        decimalValue = parser.parse("634 412 123,456");
        assertEquals(634412123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        parser = DecimalValueParser.builder()
                .addSeparator(".")
                .addSeparator(",")
                .addDecorator(" ")
                .decimals(3)
                .build();
        decimalValue = parser.parse("634 412 123,456");
        assertEquals(634412123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        parser = DecimalValueParser.builder()
                .addSeparators(List.of(".", ","))
                .addDecorators(List.of(" ", "'"))
                .decimals(3)
                .build();
        decimalValue = parser.parse("634 412 123,456");
        assertEquals(634412123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        parser = DecimalValueParser.builder()
                .addSeparators(List.of(".", ","))
                .decimals(3)
                .build();
        decimalValue = parser.parse("634412123.456");
        assertEquals(634412123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());
    }

    @Test
    public void testParseMediumNumber() {
        DecimalValue decimalValue = COMMA_SPACE_PARSER.parse("54 321,789");
        assertEquals(54321, decimalValue.getIntegerPart());
        assertEquals(789, decimalValue.getFractionalPart());
        assertEquals(789, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        assertEquals(54321789, decimalValue.toInt());
        assertEquals(54321789, decimalValue.toLong());
        assertEquals("54321.789", decimalValue.toBigDecimal().toString());
        assertEquals("54321.789", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("54321,789", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("54'321,789", COMMA_APOSTROPHY.format(decimalValue));

        decimalValue = DecimalValue.from(54321789, 3);
        assertEquals(54321, decimalValue.getIntegerPart());
        assertEquals(789, decimalValue.getFractionalPart());
        assertEquals(789, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(54321789L, 3);
        assertEquals(54321, decimalValue.getIntegerPart());
        assertEquals(789, decimalValue.getFractionalPart());
        assertEquals(789, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(new BigDecimal("54321.789"), 3);
        assertEquals(54321, decimalValue.getIntegerPart());
        assertEquals(789, decimalValue.getFractionalPart());
        assertEquals(789, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());
    }

    @Test
    public void testParseNegativeNumber() {
        DecimalValue decimalValue = STANDARD_PARSER.parse("-123.456", 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertTrue(decimalValue.isNegative());

        assertEquals(-123456, decimalValue.toInt());
        assertEquals(-123456, decimalValue.toLong());
        assertEquals("-123.456", decimalValue.toBigDecimal().toString());
        assertEquals("-123.456", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("-123,456", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("-123,456", COMMA_APOSTROPHY.format(decimalValue));

        decimalValue = DecimalValue.from(-123456, 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertTrue(decimalValue.isNegative());

        decimalValue = DecimalValue.from(-123456L, 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertTrue(decimalValue.isNegative());

        decimalValue = DecimalValue.from(new BigDecimal("-123.456"), 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(456, decimalValue.getFractionalPart());
        assertEquals(456, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertTrue(decimalValue.isNegative());
    }

    @Test
    public void testParseZero() {
        DecimalValue decimalValue = STANDARD_PARSER.parse("0", 3);
        assertEquals(0, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        assertEquals(0, decimalValue.toInt());
        assertEquals(0, decimalValue.toLong());
        assertEquals("0.000", decimalValue.toBigDecimal().toString());
        assertEquals("0.000", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("0,000", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("0,000", COMMA_APOSTROPHY.format(decimalValue));

        decimalValue = DecimalValue.from(0, 3);
        assertEquals(0, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(0L, 3);
        assertEquals(0, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(new BigDecimal("0"), 3);
        assertEquals(0, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(BigDecimal.ZERO, 3);
        assertEquals(0, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());
    }

    @Test
    public void testParseNegativeZero() {
        DecimalValue decimalValue = STANDARD_PARSER.parse("-0", 3);
        assertEquals(0, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        assertEquals(0, decimalValue.toInt());
        assertEquals(0, decimalValue.toLong());
        assertEquals("0.000", decimalValue.toBigDecimal().toString());
        assertEquals("0.000", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("0,000", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("0,000", COMMA_APOSTROPHY.format(decimalValue));

        decimalValue = DecimalValue.from(-0, 3);
        assertEquals(0, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(-0L, 3);
        assertEquals(0, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(new BigDecimal("-0"), 3);
        assertEquals(0, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());
    }

    @Test
    public void testParseFewDecimals() {
        DecimalValue decimalValue = STANDARD_PARSER.parse("123.45", 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(45, decimalValue.getFractionalPart());
        assertEquals(450, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        assertEquals(123450, decimalValue.toInt());
        assertEquals(123450, decimalValue.toLong());
        assertEquals("123.450", decimalValue.toBigDecimal().toString());
        assertEquals("123.450", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("123,450", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("123,450", COMMA_APOSTROPHY.format(decimalValue));

        decimalValue = DecimalValue.from(123450, 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(450, decimalValue.getFractionalPart());
        assertEquals(450, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(123450L, 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(450, decimalValue.getFractionalPart());
        assertEquals(450, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(new BigDecimal("123.45"), 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(450, decimalValue.getFractionalPart());
        assertEquals(450, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        assertEquals(123450, decimalValue.toInt());
        assertEquals(123450, decimalValue.toLong());
        assertEquals("123.450", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("123,450", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("123,450", COMMA_APOSTROPHY.format(decimalValue));
    }

    @Test
    public void testParseTooManyDecimals() {
        var ex = assertThrows(IllegalArgumentException.class, () -> STANDARD_PARSER.parse("123.456789", 3));
        assertEquals("You specified 3 decimals. Fraction part 456789 is too large", ex.getMessage());

        DecimalValue decimalValue = LENIENT_PARSER.parse("123.456789", 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(457, decimalValue.getFractionalPart());
        assertEquals(457, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        assertEquals(123457, decimalValue.toInt());
        assertEquals(123457, decimalValue.toLong());
        assertEquals("123.457", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("123,457", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("123,457", COMMA_APOSTROPHY.format(decimalValue));

        decimalValue = DecimalValue.from(new BigDecimal("123.456789"), 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(457, decimalValue.getFractionalPart());
        assertEquals(457, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());
    }

    @Test
    public void testParseNoDecimals() {
        DecimalValue decimalValue = STANDARD_PARSER.parse("123", 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        assertEquals(123000, decimalValue.toInt());
        assertEquals(123000, decimalValue.toLong());
        assertEquals("123.000", decimalValue.toBigDecimal().toString());
        assertEquals("123.000", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("123,000", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("123,000", COMMA_APOSTROPHY.format(decimalValue));

        decimalValue = DecimalValue.from(123000, 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(123000L, 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(new BigDecimal("123"), 3);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(3, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());
    }

    @Test
    public void testParseNoDecimalsAndNoneRequired() {
        DecimalValue decimalValue = STANDARD_PARSER.parse("123", 0);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(0, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        assertEquals(123, decimalValue.toInt());
        assertEquals(123, decimalValue.toLong());
        assertEquals("123", decimalValue.toBigDecimal().toString());
        assertEquals("123", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("123", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("123", COMMA_APOSTROPHY.format(decimalValue));

        decimalValue = DecimalValue.from(123, 0);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(0, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(123L, 0);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(0, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = DecimalValue.from(new BigDecimal("123"), 0);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(0, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());
    }

    @Test
    public void testParseDecimalsWhenZeroExpected() {
        var ex = assertThrows(IllegalArgumentException.class, () -> STANDARD_PARSER.parse("123.456789", 0));
        assertEquals("You specified 0 decimals. Fraction part 456789 is too large", ex.getMessage());
        ex = assertThrows(IllegalArgumentException.class, () -> STANDARD_PARSER.parse("123.1", 0));
        assertEquals("You specified 0 decimals. Fraction part 1 is too large", ex.getMessage());

        DecimalValue decimalValue = STANDARD_PARSER.parse("123.0", 0);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(0, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        assertEquals(123, decimalValue.toInt());
        assertEquals(123, decimalValue.toLong());
        assertEquals("123", decimalValue.toBigDecimal().toString());
        assertEquals("123", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("123", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("123", COMMA_APOSTROPHY.format(decimalValue));

        decimalValue = STANDARD_PARSER.parse("123.000", 0);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(0, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        assertEquals(123, decimalValue.toInt());
        assertEquals(123, decimalValue.toLong());
        assertEquals("123", decimalValue.toBigDecimal().toString());
        assertEquals("123", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("123", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("123", COMMA_APOSTROPHY.format(decimalValue));

        decimalValue = STANDARD_PARSER.parse("123.000000", 0);
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(0, decimalValue.getFractionalPart());
        assertEquals(0, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(0, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        assertEquals(123, decimalValue.toInt());
        assertEquals(123, decimalValue.toLong());
        assertEquals("123", decimalValue.toBigDecimal().toString());
        assertEquals("123", DecimalValueFormat.DOT.format(decimalValue));
        assertEquals("123", DecimalValueFormat.COMMA.format(decimalValue));
        assertEquals("123", COMMA_APOSTROPHY.format(decimalValue));
    }

    @Test
    public void testBadParsers() {
        var ex = assertThrows(IllegalStateException.class, () -> DecimalValueParser.builder().build());
        assertEquals("At least one separator must be provided", ex.getMessage());

        ex = assertThrows(IllegalStateException.class, () ->
                DecimalValueParser.builder()
                        .addSeparators(List.of(".", ","))
                        .addDecorators(List.of(" ", "'", ","))
                        .build()
        );
        assertEquals("Separator also present in decorators: [,], not allowed", ex.getMessage());
    }

    @Test
    public void testLenientParsingLimitCases() {
        DecimalValueParser twoDecimalParser =
                DecimalValueParser.builder()
                        .decimals(2)
                        .addSeparators(".", ",")
                        .addDecorator(" ")
                        .lenient(true)
                        .build();

        DecimalValue decimalValue = twoDecimalParser.parse("123.456");
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(46, decimalValue.getFractionalPart());
        assertEquals(46, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(2, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = twoDecimalParser.parse("123.455");
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(46, decimalValue.getFractionalPart());
        assertEquals(46, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(2, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = twoDecimalParser.parse("123.454");
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(45, decimalValue.getFractionalPart());
        assertEquals(45, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(2, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());

        decimalValue = twoDecimalParser.parse("123.5555555");
        assertEquals(123, decimalValue.getIntegerPart());
        assertEquals(56, decimalValue.getFractionalPart());
        assertEquals(56, decimalValue.getZeroPatchedFractionalPart());
        assertEquals(2, decimalValue.getDecimals());
        assertFalse(decimalValue.isNegative());
    }

}
