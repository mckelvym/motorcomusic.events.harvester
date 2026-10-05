package motorcomusic.events.parser.impl;


import static java.util.Objects.requireNonNull;
import static motorcomusic.events.parser.impl.CssSelectors.EVENT_LINK;
import static motorcomusic.events.parser.impl.CssSelectors.H4_LINK_HREF;
import static motorcomusic.events.parser.impl.HtmlConstants.ABS_HREF_ATTR;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Extracts event link from Motorco event HTML.
 */
public final class LinkExtractor {

    /**
     * Extracts the event URL from an event element.
     *
     * @param eventElement The event HTML element
     * @return The extracted URL, or null if not found
     */
    public String extract(Element eventElement) {
        String link = null;

        // Strategy 1: h4 > a tag
        Elements h4Links = eventElement.select(H4_LINK_HREF);
        if (!h4Links.isEmpty()) {
            link = requireNonNull(h4Links.first()).attr(ABS_HREF_ATTR);
        }

        // Strategy 2: Any link with /event/ in href
        if (link == null || link.isBlank()) {
            Elements eventLinks = eventElement.select(EVENT_LINK);
            if (!eventLinks.isEmpty()) {
                link = requireNonNull(eventLinks.first()).attr(ABS_HREF_ATTR);
            }
        }

        return (link != null && !link.isBlank()) ? link : null;
    }
}
