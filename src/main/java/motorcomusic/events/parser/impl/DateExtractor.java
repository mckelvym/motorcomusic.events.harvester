package motorcomusic.events.parser.impl;

import static java.util.Objects.requireNonNull;
import static motorcomusic.events.parser.impl.CssSelectors.EVENT_DATE;

import java.time.LocalDate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Extracts event date from Motorco event HTML.
 * Returns parsed LocalDate values for start and optional end dates.
 */
public final class DateExtractor {

    // Pattern for date ranges like "Dec 15-20, 2025"
    private static final Pattern DATE_RANGE_PATTERN =
        Pattern.compile("(\\w+)\\s+(\\d{1,2})-(\\d{1,2}),?\\s*(\\d{4})");
    private static final String DOORS = "(Doors:";
    // Pattern for date ranges like "Dec 15, 2025 - Dec 20, 2025"
    private static final Pattern FULL_DATE_RANGE_PATTERN =
        Pattern.compile("(\\w+\\s+\\d{1,2},?\\s*\\d{4})\\s*-\\s*(\\w+\\s+\\d{1,2},"
            + "?\\s*\\d{4})");

    private final DateParser dateParser;

    /**
     * Creates a DateExtractor with default DateParser.
     */
    public DateExtractor() {
        this.dateParser = new DateParser();
    }

    private String cleanDateString(String dateString) {
        // Remove "(Doors: ...)" part if present first
        String cleaned = dateString;
        int doorsIndex = cleaned.indexOf(DOORS);
        if (doorsIndex > 0) {
            cleaned = cleaned.substring(0, doorsIndex);
        }
        // Remove time portion like " at 8:00 PM" or direct time like " 9:30 pm"
        cleaned = cleaned.replaceAll("\\s+at\\s+\\d+:\\d+\\s*(AM|PM|am|pm)?", "");
        cleaned = cleaned.replaceAll("\\s+\\d{1,2}:\\d{2}\\s*(AM|PM|am|pm)", "");
        return cleaned.trim();
    }

    /**
     * Extracts date information from an event element.
     *
     * @param eventElement The event HTML element
     * @return DateRange with start and optional end date, or null if no date found
     */
    @Nullable
    public DateRange extractDateRange(Element eventElement) {
        String rawDate = extractRaw(eventElement);
        if (rawDate.isEmpty()) {
            return null;
        }
        return parseDateString(rawDate);
    }

    /**
     * Extracts the date string from an event element.
     *
     * @param eventElement The event HTML element
     * @return The extracted date string, or empty string if not found
     */
    public String extractRaw(Element eventElement) {
        Elements dateElements = eventElement.select(EVENT_DATE);
        if (!dateElements.isEmpty()) {
            return requireNonNull(dateElements.first()).text().trim();
        }
        return "";
    }

    /**
     * Parses a date string to a DateRange.
     *
     * @param dateString The date string to parse
     * @return DateRange with start and optional end date, or null if unparseable
     */
    @Nullable
    public DateRange parseDateString(String dateString) {
        if (dateString == null || dateString.isBlank()) {
            return null;
        }

        DateRange result = tryParseShortDateRange(dateString);
        if (result != null) {
            return result;
        }

        result = tryParseFullDateRange(dateString);
        if (result != null) {
            return result;
        }

        return tryParseSingleDate(dateString);
    }

    private String stripDayOfWeek(String dateString) {
        // Remove day of week prefix like "Saturday, " or "Sat "
        String stripped = dateString.replaceAll("^\\w+,\\s*", "");
        if (stripped.equals(dateString)) {
            // Try without comma (e.g., "Sat Dec 15, 2025")
            stripped = dateString.replaceAll("^\\w+\\s+", "");
        }
        return stripped;
    }

    @Nullable
    private LocalDate tryParseDate(String dateString) {
        return dateParser.parse(dateString);
    }

    @Nullable
    private DateRange tryParseFullDateRange(String dateString) {
        Matcher fullRangeMatcher = FULL_DATE_RANGE_PATTERN.matcher(dateString);
        if (!fullRangeMatcher.find()) {
            return null;
        }

        LocalDate startDate = tryParseDate(fullRangeMatcher.group(1).trim());
        LocalDate endDate = tryParseDate(fullRangeMatcher.group(2).trim());
        if (startDate != null) {
            return new DateRange(startDate, endDate);
        }
        return null;
    }

    @Nullable
    private DateRange tryParseShortDateRange(String dateString) {
        Matcher rangeMatcher = DATE_RANGE_PATTERN.matcher(dateString);
        if (!rangeMatcher.find()) {
            return null;
        }

        try {
            String month = rangeMatcher.group(1);
            int startDay = Integer.parseInt(rangeMatcher.group(2));
            int endDay = Integer.parseInt(rangeMatcher.group(3));
            int year = Integer.parseInt(rangeMatcher.group(4));

            String startDateStr = "%s %d, %d".formatted(month, startDay, year);
            String endDateStr = "%s %d, %d".formatted(month, endDay, year);

            LocalDate startDate = tryParseDate(startDateStr);
            LocalDate endDate = tryParseDate(endDateStr);

            if (startDate != null) {
                return new DateRange(startDate, endDate);
            }
        } catch (NumberFormatException e) {
            // empty intentionally
        }
        return null;
    }

    @Nullable
    private DateRange tryParseSingleDate(String dateString) {
        // Try the original string first
        LocalDate startDate = tryParseDate(dateString);
        if (startDate != null) {
            return new DateRange(startDate, null);
        }

        // Remove time and other suffixes for parsing
        String cleanedDate = cleanDateString(dateString);
        startDate = tryParseDate(cleanedDate);
        if (startDate != null) {
            return new DateRange(startDate, null);
        }

        // Try stripping the day of week prefix
        String strippedDate = stripDayOfWeek(cleanedDate);
        startDate = tryParseDate(strippedDate);
        if (startDate != null) {
            return new DateRange(startDate, null);
        }

        return null;
    }

    /**
     * Date range result containing start and optional end date.
     */
    public record DateRange(LocalDate start, @Nullable LocalDate end) {
    }
}
