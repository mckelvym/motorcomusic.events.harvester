package motorcomusic.events;

import java.util.List;
import java.util.Set;
import motorcomusic.events.config.ScraperConfiguration;
import motorcomusic.events.config.impl.ScraperConfigurationImpl;
import motorcomusic.events.domain.EventItem;
import motorcomusic.events.feed.RssFeedManager;
import motorcomusic.events.feed.RssFeedManagerImpl;
import motorcomusic.events.parser.EventParser;
import motorcomusic.events.parser.impl.EventParserImpl;
import motorcomusic.events.scraper.EventScraper;
import motorcomusic.events.scraper.impl.EventScraperImpl;
import motorcomusic.events.webdriver.ChromeDriverManager;
import motorcomusic.events.webdriver.PageLoader;
import motorcomusic.events.webdriver.WebDriverManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

/**
 * Main application for scraping and generating RSS feed.
 */
public final class EventsHarvesterApplication {

    private static final String DEFAULT_OUTPUT_FILE = "events.xml";
    private static final Logger LOG =
        LoggerFactory.getLogger(EventsHarvesterApplication.class);

    private EventsHarvesterApplication() {
        // utility
    }

    private static void configureLogging() {
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
    }

    /**
     * Application entry point.
     *
     * @param args Command-line arguments (optional output file path)
     */
    public static void main(String[] args) {
        configureLogging();

        String outputFilePath = DEFAULT_OUTPUT_FILE;
        if (args.length > 0) {
            outputFilePath = args[0];
        }

        LOG.info("Starting Motorco Events Harvester");
        LOG.info("Output file: {}", outputFilePath);

        ScraperConfiguration config = new ScraperConfigurationImpl();

        try (WebDriverManager webDriverManager = new ChromeDriverManager(config)) {
            PageLoader pageLoader = new PageLoader(
                webDriverManager.getDriver(),
                config.getPageLoadTimeout()
            );

            EventParser eventParser = new EventParserImpl();

            EventScraper scraper = new EventScraperImpl(
                config,
                pageLoader,
                eventParser
            );

            RssFeedManager feedManager = new RssFeedManagerImpl(config);

            LOG.info("Loading existing feed");
            Set<String> existingGuids = feedManager.loadExistingGuids(outputFilePath);
            LOG.info("Found {} existing events", existingGuids.size());

            List<EventItem> newEvents = scraper.scrapeEvents(existingGuids);
            LOG.info("Scraped {} new events", newEvents.size());

            feedManager.generateFeed(
                outputFilePath,
                newEvents,
                outputFilePath
            );

            LOG.info("Harvesting completed successfully");
        } catch (Exception e) {
            LOG.error("Application error", e);
            System.exit(1);
        }
    }
}
