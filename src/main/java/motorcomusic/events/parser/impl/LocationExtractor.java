package motorcomusic.events.parser.impl;

import static java.util.Objects.requireNonNull;
import static motorcomusic.events.parser.impl.CssSelectors.EVENT_LOCATION;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Extracts event location/organizer from Motorco event HTML.
 */
public final class LocationExtractor {

    /**
     * Extracts the location/organizer from an event element.
     *
     * @param eventElement The event HTML element
     * @return The extracted location, or empty string if not found
     */
    public String extract(Element eventElement) {
        String location = "";

        // Look for div.tc-event-location > span
        Elements locationElements =
            eventElement.select(EVENT_LOCATION);
        if (!locationElements.isEmpty()) {
            location = requireNonNull(locationElements.first()).text().trim();
        }

        return location;
    }
}
