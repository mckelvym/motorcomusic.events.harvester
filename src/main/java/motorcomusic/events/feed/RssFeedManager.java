package motorcomusic.events.feed;

import java.util.List;
import java.util.Set;
import motorcomusic.events.domain.EventItem;

/**
 * Interface for managing RSS feed operations.
 */
public interface RssFeedManager {

    /**
     * Generates an RSS feed with new and existing events.
     *
     * @param filePath         Output file path for the RSS feed
     * @param newEvents        List of newly scraped events
     * @param existingFilePath Path to existing RSS file to merge with
     * @throws Exception if generation fails
     */
    void generateFeed(
        String filePath,
        List<EventItem> newEvents,
        String existingFilePath
    )
        throws Exception;

    /**
     * Loads existing event GUIDs from an RSS file.
     *
     * @param filePath The path to the RSS file
     * @return Set of existing GUIDs
     * @throws Exception if loading fails
     */
    Set<String> loadExistingGuids(String filePath)
        throws Exception;
}
