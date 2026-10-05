package motorcomusic.events.parser.impl;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.util.Optional;
import motorcomusic.events.domain.EventItem;
import motorcomusic.events.parser.EventParser;
import motorcomusic.events.parser.impl.DateExtractor.DateRange;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parses and extracts event fields.
 */
public final class EventParserImpl implements EventParser {

    private static final Logger LOG =
        LoggerFactory.getLogger(EventParserImpl.class);
    private final DateExtractor dateExtractor;
    private final DescriptionExtractor descriptionExtractor;
    private final ImageExtractor imageExtractor;
    private final LinkExtractor linkExtractor;
    private final LocationExtractor locationExtractor;
    private final TitleExtractor titleExtractor;

    /**
     * Creates a EventParserImpl with all necessary extractors.
     */
    public EventParserImpl() {
        this.titleExtractor = new TitleExtractor();
        this.linkExtractor = new LinkExtractor();
        this.dateExtractor = new DateExtractor();
        this.locationExtractor = new LocationExtractor();
        this.descriptionExtractor = new DescriptionExtractor();
        this.imageExtractor = new ImageExtractor();
    }

    @Override
    public Optional<EventItem> parseEvent(Element eventElement) {
        requireNonNull(eventElement, "eventElement must not be null");
        String title = titleExtractor.extract(eventElement);
        String link = linkExtractor.extract(eventElement);
        DateRange dateRange = dateExtractor.extractDateRange(eventElement);
        String location = locationExtractor.extract(eventElement);
        String description = descriptionExtractor.extract(eventElement);
        String imageUrl = imageExtractor.extract(eventElement);

        if (title == null || link == null) {
            LOG.warn("Skipping event: missing title or link");
            return Optional.empty();
        }

        if (dateRange == null) {
            String rawDate = dateExtractor.extractRaw(eventElement);
            throw new IllegalStateException(
                "Failed to parse date for event '" + title + "'. "
                    + "Raw date string: '" + rawDate + "'. "
                    + "Event link: " + link);
        }

        LocalDate eventDateStart = dateRange.start();
        LocalDate eventDateEnd = dateRange.end();

        return Optional.of(new EventItem(
            link,  // id - use link as unique identifier
            title,
            link,
            description,
            eventDateStart,
            eventDateEnd,
            imageUrl,
            location
        ));
    }
}
