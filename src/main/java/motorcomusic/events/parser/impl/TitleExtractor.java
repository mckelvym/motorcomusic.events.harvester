package motorcomusic.events.parser.impl;


import static java.util.Objects.requireNonNull;
import static motorcomusic.events.parser.impl.CssSelectors.EVENT_LINK;
import static motorcomusic.events.parser.impl.CssSelectors.H4;
import static motorcomusic.events.parser.impl.CssSelectors.H4_LINK;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Extracts event title from Motorco event HTML.
 * Uses multiple fallback strategies for robust extraction.
 */
public final class TitleExtractor {

    /**
     * Extracts the title from an event element.
     *
     * @param eventElement The event HTML element
     * @return The extracted title, or null if not found
     */
    public String extract(Element eventElement) {
        String title = null;

        // Strategy 1: h4 > a tag
        Elements h4Links = eventElement.select(H4_LINK);
        if (!h4Links.isEmpty()) {
            title = requireNonNull(h4Links.first()).text();
        }

        // Strategy 2: Any h4 tag
        if (title == null || title.isBlank()) {
            Elements h4Tags = eventElement.select(H4);
            if (!h4Tags.isEmpty()) {
                title = requireNonNull(h4Tags.first()).text();
            }
        }

        // Strategy 3: Links with href containing /event/
        if (title == null || title.isBlank()) {
            Elements eventLinks = eventElement.select(EVENT_LINK);
            if (!eventLinks.isEmpty()) {
                title = requireNonNull(eventLinks.first()).text();
            }
        }

        return (title != null && !title.isBlank()) ? title.trim() : null;
    }
}
