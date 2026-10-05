package motorcomusic.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Tests for DateParser.
 * Verifies date parsing with multiple format strategies.
 */
class DateParserTest {

    private DateParser parser;

    @Test
    void parse_withAbbreviatedMonthFormat_returnsLocalDate() {
        LocalDate result = parser.parse("Dec 15, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withAtTimeFormat_returnsLocalDate() {
        LocalDate result = parser.parse("December 15, 2025 at 8:00 PM");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withBlankString_returnsNull() {
        LocalDate result = parser.parse("   ");

        assertThat(result).isNull();
    }

    @Test
    void parse_withDayOfWeekFormat_returnsLocalDate() {
        // This format is supported: "EEEE, MMMM d, yyyy"
        LocalDate result = parser.parse("Monday, December 15, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withEmptyString_returnsNull() {
        LocalDate result = parser.parse("");

        assertThat(result).isNull();
    }

    @Test
    void parse_withFullMonthFormat_returnsLocalDate() {
        LocalDate result = parser.parse("December 15, 2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withInvalidFormat_returnsNull() {
        LocalDate result = parser.parse("not a date");

        assertThat(result).isNull();
    }

    @Test
    void parse_withIsoFormat_returnsLocalDate() {
        LocalDate result = parser.parse("2025-12-15");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withNull_returnsNull() {
        LocalDate result = parser.parse(null);

        assertThat(result).isNull();
    }

    @Test
    void parse_withSlashFormat_returnsLocalDate() {
        LocalDate result = parser.parse("12/15/2025");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void parse_withTimeFormat_returnsLocalDate() {
        // This format is supported: "MMMM d, yyyy 'at' h:mm a"
        LocalDate result = parser.parse("December 15, 2025 at 8:00 PM");

        assertThat(result).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @BeforeEach
    void setUp() {
        parser = new DateParser();
    }
}
