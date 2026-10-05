package motorcomusic.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import motorcomusic.events.parser.impl.DateExtractor.DateRange;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for DateExtractor.
 * Tests date extraction from div.tc-event-date span structure.
 */
class DateExtractorTest {

    private DateExtractor extractor;

    @Test
    void extractDateRangeRaw_withEventDateSpan_returnsRawString() {
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>December 15, 2025</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractRaw(element);

        assertThat(result).isEqualTo("December 15, 2025");
    }

    @Test
    void extractDateRangeRaw_withNoEventDate_returnsEmptyString() {
        String html = "<div><p>No date information</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extractRaw(element);

        assertThat(result).isEmpty();
    }

    @Test
    void extract_DateRange_withAdditionalClasses_findsCorrectly() {
        String html = """
            <div>
                <div class="tc-event-date other-class">
                    <span>January 5, 2026</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2026, 1, 5));
    }

    @Test
    void extract_DateRange_withBlankSpan_returnsNull() {
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>   </span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNull();
    }

    @Test
    void extract_DateRange_withDateRange_extractsBothDates() {
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>Dec 15-20, 2025</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2025, 12, 15));
        assertThat(result.end()).isEqualTo(LocalDate.of(2025, 12, 20));
    }

    @Test
    void extract_DateRange_withEmptySpan_returnsNull() {
        String html = """
            <div>
                <div class="tc-event-date">
                    <span></span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNull();
    }

    @Test
    void extract_DateRange_withEventDateNoSpan_returnsNull() {
        String html = """
            <div>
                <div class="tc-event-date">
                    December 15, 2025
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNull();
    }

    @Test
    void extract_DateRange_withEventDateSpan_returnsDateRange() {
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>December 15, 2025</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2025, 12, 15));
        assertThat(result.end()).isNull();
    }

    @Test
    void extract_DateRange_withLongDateFormat_extractsCorrectly() {
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>Saturday, December 15, 2025</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void extract_DateRange_withMotorcoLiveFormatVariant_extractsCorrectly() {
        // Another variant from live site
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>Fri Jan 9, 2026 7:30 pm (Doors: 6:30 pm)</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2026, 1, 9));
        assertThat(result.end()).isNull();
    }

    @Test
    void extract_DateRange_withMotorcoLiveFormat_extractsCorrectly() {
        // Actual format from live motorcomusic.com site
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>Wed Dec 31, 2025 9:30 pm (Doors: 8:30 pm)</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(result.end()).isNull();
    }

    @Test
    void extract_DateRange_withMultipleEventDates_usesFirst() {
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>December 15, 2025</span>
                </div>
                <div class="tc-event-date">
                    <span>January 20, 2026</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void extract_DateRange_withMultipleSpans_usesFirst() {
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>December 15, 2025</span>
                    <span>January 20, 2026</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void extract_DateRange_withNestedStructure_findsCorrectly() {
        String html = """
            <div>
                <div class="outer">
                    <div class="tc-event-date">
                        <span>March 10, 2026</span>
                    </div>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2026, 3, 10));
    }

    @Test
    void extract_DateRange_withNoEventDate_returnsNull() {
        String html = "<div><p>No date information</p></div>";
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNull();
    }

    @Test
    void extract_DateRange_withNumericDate_extractsCorrectly() {
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>12/15/2025</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void extract_DateRange_withShortDateFormat_returnsNull() {
        // "Dec 15" without year cannot be parsed to LocalDate
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>Dec 15</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        // Cannot parse without year
        assertThat(result).isNull();
    }

    @Test
    void extract_DateRange_withSpanAttributes_extractsTextCorrectly() {
        String html = """
            <div>
                <div class="tc-event-date">
                    <span class="date-text" id="event-date">February 10, 2026</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2026, 2, 10));
    }

    @Test
    void extract_DateRange_withTimeIncluded_extractsCorrectly() {
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>December 15, 2025 at 8:00 PM</span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2025, 12, 15));
    }

    @Test
    void extract_DateRange_withWhitespace_trimsCorrectly() {
        String html = """
            <div>
                <div class="tc-event-date">
                    <span>  January 20, 2026  </span>
                </div>
            </div>
            """;
        Element element = Jsoup.parse(html).body();

        DateRange result = extractor.extractDateRange(element);

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2026, 1, 20));
    }

    @Test
    void parseDateString_withBlank_returnsNull() {
        DateRange result = extractor.parseDateString("   ");

        assertThat(result).isNull();
    }

    @Test
    void parseDateString_withDateRange_returnsBothDates() {
        DateRange result = extractor.parseDateString("Dec 15-20, 2025");

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2025, 12, 15));
        assertThat(result.end()).isEqualTo(LocalDate.of(2025, 12, 20));
    }

    @Test
    void parseDateString_withDirectTimeAndDoors_parsesCorrectly() {
        DateRange result = extractor.parseDateString(
            "Sun Jan 18, 2026 1:00 pm (Doors: 12:00 pm)");

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2026, 1, 18));
        assertThat(result.end()).isNull();
    }

    @Test
    void parseDateString_withInvalidFormat_returnsNull() {
        DateRange result = extractor.parseDateString("Not a valid date");

        assertThat(result).isNull();
    }

    @Test
    void parseDateString_withMotorcoLiveFormat_parsesCorrectly() {
        DateRange result = extractor.parseDateString(
            "Wed Dec 31, 2025 9:30 pm (Doors: 8:30 pm)");

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2025, 12, 31));
        assertThat(result.end()).isNull();
    }

    @Test
    void parseDateString_withNull_returnsNull() {
        DateRange result = extractor.parseDateString(null);

        assertThat(result).isNull();
    }

    @Test
    void parseDateString_withValidDate_returnsDateRange() {
        DateRange result = extractor.parseDateString("December 15, 2025");

        assertThat(result).isNotNull();
        assertThat(result.start()).isEqualTo(LocalDate.of(2025, 12, 15));
        assertThat(result.end()).isNull();
    }

    @BeforeEach
    void setUp() {
        extractor = new DateExtractor();
    }
}
