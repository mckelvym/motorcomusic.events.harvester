package motorcomusic.events.parser.impl;

/**
 * Constants for CSS selectors used in HTML parsing.
 *
 * <p>This class centralizes all CSS selector strings used throughout the
 * parser implementation to avoid magic strings and improve maintainability.
 */
public final class CssSelectors {

    // TitleExtractor selectors
    public static final String H4_LINK = "h4 > a";
    public static final String H4 = "h4";
    public static final String EVENT_LINK = "a[href*=/event/]";

    // DateExtractor selectors
    public static final String EVENT_DATE = "div.tc-event-date > span";

    // DescriptionExtractor selectors
    public static final String EVENT_EXCERPT = "div.tc-event-excerpt";

    // ImageExtractor selectors
    public static final String EVENT_IMAGE = "img.attachment-tc_all_events_image";
    public static final String IMG = "img";

    // LinkExtractor selectors
    public static final String H4_LINK_HREF = "h4 > a[href]";

    // LocationExtractor selectors
    public static final String EVENT_LOCATION = "div.tc-event-location > span";

    // Page loading selectors
    public static final String EVENT_CONTAINER_SELECTOR = "div.tc-single-event";
    public static final String PAGE_LOAD_SELECTOR = "div.tc-single-event";

    private CssSelectors() {
        // Utility class - prevent instantiation
    }
}
