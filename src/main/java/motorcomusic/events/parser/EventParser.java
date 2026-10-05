package motorcomusic.events.parser;


import java.util.Optional;
import motorcomusic.events.domain.EventItem;
import org.jsoup.nodes.Element;

/**
 * Interface for parsing event information from HTML elements.
 */
public interface EventParser {

    /**
     * Parses an event from an HTML element.
     *
     * @param eventElement The HTML element containing event data
     * @return The parsed EventItem, or empty if parsing fails
     */
    Optional<EventItem> parseEvent(Element eventElement);
}
