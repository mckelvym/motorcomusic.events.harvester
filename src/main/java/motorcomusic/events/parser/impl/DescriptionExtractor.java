package motorcomusic.events.parser.impl;

import static java.util.Objects.requireNonNull;
import static motorcomusic.events.parser.impl.CssSelectors.EVENT_EXCERPT;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Extracts event description from event HTML.
 */
public final class DescriptionExtractor {

    /**
     * Extracts the description from an event element.
     *
     * @param element The event HTML element
     * @return The extracted description HTML, or empty string if not found
     */
    public String extract(Element element) {
        requireNonNull(element, "element must not be null");
        String description = "";

        // Look for div.tc-event-excerpt
        Elements excerptElements =
            element.select(EVENT_EXCERPT);
        if (!excerptElements.isEmpty()) {
            description = requireNonNull(excerptElements.first()).html().trim();
        }

        return description;
    }
}
