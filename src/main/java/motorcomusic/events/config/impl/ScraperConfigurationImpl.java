package motorcomusic.events.config.impl;

import static motorcomusic.events.parser.impl.CssSelectors.EVENT_CONTAINER_SELECTOR;
import static motorcomusic.events.parser.impl.CssSelectors.PAGE_LOAD_SELECTOR;

import java.time.Duration;
import motorcomusic.events.config.ScraperConfiguration;

/**
 * Configuration for scraping Motorco Music Hall events.
 * Provides all site-specific parameters for the scraper.
 */
public class ScraperConfigurationImpl implements ScraperConfiguration {

    private static final String BASE_URL = "https://motorcomusic.com/";
    private static final String FEED_DESCRIPTION =
        "Upcoming events at Motorco Music Hall in Durham, NC";
    private static final String FEED_TITLE = "Motorco Music Hall Events";
    private static final Duration PAGE_LOAD_TIMEOUT = Duration.ofSeconds(10);
    private static final int RETENTION_DAYS = 7;
    private static final String USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36";

    @Override
    public String getBaseUrl() {
        return BASE_URL;
    }

    @Override
    public String getEventContainerSelector() {
        return EVENT_CONTAINER_SELECTOR;
    }

    @Override
    public String getFeedDescription() {
        return FEED_DESCRIPTION;
    }

    @Override
    public String getFeedLink() {
        return getBaseUrl();
    }

    @Override
    public String getFeedTitle() {
        return FEED_TITLE;
    }

    @Override
    public String getPageLoadSelector() {
        return PAGE_LOAD_SELECTOR;
    }

    @Override
    public Duration getPageLoadTimeout() {
        return PAGE_LOAD_TIMEOUT;
    }

    @Override
    public int getRetentionDays() {
        return RETENTION_DAYS;
    }

    @Override
    public String getUserAgent() {
        return USER_AGENT;
    }
}
