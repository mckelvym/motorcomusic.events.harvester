package motorcomusic.events.parser.impl;

import static java.util.Objects.requireNonNull;
import static motorcomusic.events.parser.impl.CssSelectors.EVENT_IMAGE;
import static motorcomusic.events.parser.impl.CssSelectors.IMG;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Extracts event image URL from Motorco event HTML.
 */
public final class ImageExtractor {

    private static final String ABS_SRC = "abs:src";

    /**
     * Extracts the image URL from an event element.
     *
     * @param eventElement The event HTML element
     * @return The extracted image URL, or empty string if not found
     */
    public String extract(Element eventElement) {
        String imageUrl = "";

        // Look for img.attachment-tc_all_events_image
        Elements imageElements =
            eventElement.select(EVENT_IMAGE);
        if (!imageElements.isEmpty()) {
            imageUrl = requireNonNull(imageElements.first()).attr(ABS_SRC);
        }

        // Fallback: any img tag
        if (imageUrl.isBlank()) {
            Elements anyImage = eventElement.select(IMG);
            if (!anyImage.isEmpty()) {
                imageUrl = requireNonNull(anyImage.first()).attr(ABS_SRC);
            }
        }

        return imageUrl;
    }
}
