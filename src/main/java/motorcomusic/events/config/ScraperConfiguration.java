package motorcomusic.events.config;

import java.time.Duration;

/**
 * Configuration interface for web scraping operations.
 * Defines all necessary parameters for scraping a specific website.
 */
public interface ScraperConfiguration {

    /**
     * Gets the base URL of the events page to scrape.
     *
     * @return The base URL
     */
    String getBaseUrl();

    /**
     * Gets the CSS selector for finding individual event containers.
     *
     * @return The event container selector
     */
    String getEventContainerSelector();

    /**
     * Gets the RSS feed description.
     *
     * @return the feed description
     */
    String getFeedDescription();

    /**
     * Gets the RSS feed link.
     *
     * @return the feed link
     */
    String getFeedLink();

    /**
     * Gets the RSS feed title.
     *
     * @return the feed title
     */
    String getFeedTitle();

    /**
     * Gets the CSS selector to wait for when loading pages.
     *
     * @return The CSS selector for page load confirmation
     */
    String getPageLoadSelector();

    /**
     * Gets the timeout duration for page loads.
     *
     * @return the timeout duration
     */
    Duration getPageLoadTimeout();

    /**
     * Gets the number of days to keep events in the feed.
     * Events older than this will be filtered out.
     *
     * @return The number of days to retain events
     */
    int getRetentionDays();

    /**
     * Gets the user agent string for HTTP requests.
     *
     * @return the user agent string
     */
    String getUserAgent();
}
