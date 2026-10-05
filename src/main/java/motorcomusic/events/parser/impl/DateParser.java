package motorcomusic.events.parser.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Centralized date parsing utility
 */
public final class DateParser {

    private static final List<DateTimeFormatter> DATE_FORMATTERS = List.of(
        DateTimeFormatter.ofPattern("MMMM d, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMMM dd, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("EEE MMM d, yyyy h:mm a", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("EEE MMM dd, yyyy h:mm a", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMMM d, yyyy 'at' h:mm a", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' h:mm a", Locale.ENGLISH),
        DateTimeFormatter.ofPattern("M/d/yyyy", Locale.US),
        DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.US),
        DateTimeFormatter.ISO_LOCAL_DATE,
        DateTimeFormatter.RFC_1123_DATE_TIME
    );
    private static final Logger LOG = LoggerFactory.getLogger(DateParser.class);

    /**
     * Parses a date string to LocalDate using multiple format strategies.
     *
     * @param dateStr the date string to parse
     * @return the parsed LocalDate, or null if parsing fails
     */
    @Nullable
    public LocalDate parse(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) {
            return null;
        }

        String trimmed = dateStr.trim();
        for (DateTimeFormatter formatter : DATE_FORMATTERS) {
            try {
                return LocalDate.parse(trimmed, formatter);
            } catch (DateTimeParseException e) {
                // Try next formatter
            }
        }

        LOG.debug("Could not parse date '{}' with any known format", dateStr);
        return null;
    }
}
