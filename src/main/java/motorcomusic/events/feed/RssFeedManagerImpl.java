package motorcomusic.events.feed;

import static java.util.Objects.requireNonNull;
import static motorcomusic.events.feed.RssElementNames.CHANNEL;
import static motorcomusic.events.feed.RssElementNames.DESCRIPTION;
import static motorcomusic.events.feed.RssElementNames.ENCLOSURE;
import static motorcomusic.events.feed.RssElementNames.ENCODING_UTF8;
import static motorcomusic.events.feed.RssElementNames.EVENT_NAMESPACE_URI;
import static motorcomusic.events.feed.RssElementNames.EV_ENDDATE;
import static motorcomusic.events.feed.RssElementNames.EV_STARTDATE;
import static motorcomusic.events.feed.RssElementNames.GUID;
import static motorcomusic.events.feed.RssElementNames.IMAGE_JPEG_TYPE;
import static motorcomusic.events.feed.RssElementNames.INDENT_AMOUNT;
import static motorcomusic.events.feed.RssElementNames.IS_PERMALINK_ATTR;
import static motorcomusic.events.feed.RssElementNames.ITEM;
import static motorcomusic.events.feed.RssElementNames.LAST_BUILD_DATE;
import static motorcomusic.events.feed.RssElementNames.LINK;
import static motorcomusic.events.feed.RssElementNames.PUB_DATE;
import static motorcomusic.events.feed.RssElementNames.RSS;
import static motorcomusic.events.feed.RssElementNames.RSS_VERSION;
import static motorcomusic.events.feed.RssElementNames.TITLE;
import static motorcomusic.events.feed.RssElementNames.TRUE_VALUE;
import static motorcomusic.events.feed.RssElementNames.TYPE_ATTR;
import static motorcomusic.events.feed.RssElementNames.URL_ATTR;
import static motorcomusic.events.feed.RssElementNames.VERSION_ATTR;
import static motorcomusic.events.feed.RssElementNames.XMLNS_EV_ATTR;
import static motorcomusic.events.feed.RssElementNames.XSLT_INDENT_PROPERTY;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import motorcomusic.events.config.ScraperConfiguration;
import motorcomusic.events.domain.EventItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Implementation of RSS feed management.
 * Handles loading existing feeds and generating new ones with XXE protection.
 */
public class RssFeedManagerImpl implements RssFeedManager {

    // Anything outside XML 1.0's Char production: #x9 | #xA | #xD | [#x20-#xD7FF] |
    // [#xE000-#xFFFD] | [#x10000-#x10FFFF]
    private static final Pattern INVALID_XML_CHARACTERS = Pattern.compile(
        "[^\\x09\\x0A\\x0D\\x{20}-\\x{D7FF}\\x{E000}-\\x{FFFD}\\x{10000}-\\x{10FFFF}]");
    private static final Logger LOG =
        LoggerFactory.getLogger(RssFeedManagerImpl.class);
    private final ScraperConfiguration config;
    private final EventFilter eventFilter;
    private final XmlSecurityConfigurer securityConfigurer;

    /**
     * Creates an RssFeedManagerImpl with configuration.
     *
     * @param config the scraper configuration
     */
    public RssFeedManagerImpl(ScraperConfiguration config) {
        this.config = requireNonNull(config, "config must not be null");
        this.securityConfigurer = new XmlSecurityConfigurer();
        this.eventFilter = new EventFilter(config);
    }

    private void addChannelMetadata(Document document, Element channel) {
        Element title = document.createElement(TITLE);
        title.setTextContent(config.getFeedTitle());
        channel.appendChild(title);

        Element link = document.createElement(LINK);
        link.setTextContent(config.getFeedLink());
        channel.appendChild(link);

        Element description = document.createElement(DESCRIPTION);
        description.setTextContent(config.getFeedDescription());
        channel.appendChild(description);

        Element lastBuildDate = document.createElement(LAST_BUILD_DATE);
        lastBuildDate.setTextContent(
            ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)
        );
        channel.appendChild(lastBuildDate);
    }

    /**
     * Adds a description element wrapped in CDATA, omitting it when empty.
     *
     * @param document         the XML document
     * @param item        the item element to add to
     * @param description the description HTML or text
     */
    private void addDescriptionElement(Document document, Element item,
                                       String description) {
        if (description.isEmpty()) {
            return;
        }
        Element element = document.createElement(DESCRIPTION);
        element.appendChild(document.createCDATASection(description));
        item.appendChild(element);
    }

    /**
     * Adds the machine-readable event dates (RSS Event module) used for retention.
     *
     * @param document   the XML document
     * @param item  the item element to add to
     * @param event the event whose dates to add
     */
    private void addEventDateElements(Document document, Element item,
                                      EventItem event) {
        Element startDate = document.createElement(EV_STARTDATE);
        startDate.setTextContent(event.eventDateStart().toString());
        item.appendChild(startDate);
        if (event.eventDateEnd() != null) {
            Element endDate = document.createElement(EV_ENDDATE);
            endDate.setTextContent(event.eventDateEnd().toString());
            item.appendChild(endDate);
        }
    }

    private void addEventItem(
        Document document,
        Element channel,
        EventItem event
    ) {
        Element item = document.createElement(ITEM);

        Element title = document.createElement(TITLE);
        String dateDisplay = formatDateForDisplay(event);
        String titleText = "%s (%s)".formatted(event.title(), dateDisplay);
        title.setTextContent(titleText);
        item.appendChild(title);

        Element link = document.createElement(LINK);
        link.setTextContent(event.link());
        item.appendChild(link);

        addGuidElement(document, item, event);

        addEventDateElements(document, item, event);

        Element pubDateElement = document.createElement(PUB_DATE);
        pubDateElement.setTextContent(
            ZonedDateTime.now().format(DateTimeFormatter.RFC_1123_DATE_TIME)
        );
        item.appendChild(pubDateElement);

        if (event.hasImage()) {
            Element enclosure = document.createElement(ENCLOSURE);
            enclosure.setAttribute(URL_ATTR, event.imageUrl());
            enclosure.setAttribute(TYPE_ATTR, IMAGE_JPEG_TYPE);
            item.appendChild(enclosure);
        }

        String sanitizedDescription = event.sanitizedDescription();
        if (!sanitizedDescription.isEmpty() || event.location() != null
            || event.hasImage()) {
            StringBuilder descHtml = new StringBuilder();

            descHtml.append("<p><strong>Date:</strong> ")
                .append(dateDisplay)
                .append("</p>");

            if (event.location() != null && !event.location().isBlank()) {
                descHtml.append("<p><strong>Presented by:</strong> ")
                    .append(event.location())
                    .append("</p>");
            }

            if (event.hasImage()) {
                descHtml.append("<p><img src=\"")
                    .append(event.imageUrl())
                    .append("\" alt=\"Event image\" /></p>");
            }

            if (!sanitizedDescription.isEmpty()) {
                descHtml.append(sanitizedDescription);
            }

            addDescriptionElement(document, item, descHtml.toString());
        }

        channel.appendChild(item);
    }

    /**
     * Adds the item GUID, which is always the event URL and therefore a permalink.
     *
     * @param document   the XML document
     * @param item  the item element to add to
     * @param event the event whose GUID to add
     */
    private void addGuidElement(Document document, Element item, EventItem event) {
        Element guid = document.createElement(GUID);
        guid.setAttribute(IS_PERMALINK_ATTR, TRUE_VALUE);
        guid.setTextContent(event.guid());
        item.appendChild(guid);
    }

    private String formatDateForDisplay(EventItem event) {
        LocalDate start = event.eventDateStart();
        LocalDate end = event.eventDateEnd();

        if (end != null && !end.equals(start)) {
            return "%s - %s".formatted(start, end);
        }
        return start.toString();
    }

    @Override
    public void generateFeed(
        String filePath,
        List<EventItem> newEvents,
        String existingFilePath
    )
        throws Exception {
        requireNonNull(filePath, "filePath must not be null");
        requireNonNull(newEvents, "newEvents must not be null");
        requireNonNull(existingFilePath, "existingFilePath must not be null");

        final File outputFile = new File(filePath);
        final File existingRssFile = new File(existingFilePath);

        DocumentBuilderFactory factory =
            securityConfigurer.createSecureDocumentBuilderFactory();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element rss = document.createElement(RSS);
        rss.setAttribute(VERSION_ATTR, RSS_VERSION);
        rss.setAttribute(XMLNS_EV_ATTR, EVENT_NAMESPACE_URI);
        document.appendChild(rss);

        Element channel = document.createElement(CHANNEL);
        rss.appendChild(channel);

        addChannelMetadata(document, channel);

        // Add new events (sorted by eventDateStart descending), skipping any past retention
        List<EventItem> sortedEvents = new ArrayList<>(newEvents);
        sortedEvents.sort(Comparator.comparing(EventItem::eventDateStart).reversed());

        for (EventItem event : sortedEvents) {
            if (eventFilter.shouldKeep(event)) {
                addEventItem(document, channel, event);
            }
        }

        importExistingEvents(document, channel, existingRssFile);

        writeXmlToFile(document, outputFile);

        LOG.info("RSS feed written to {}", filePath);
    }

    /**
     * Imports items from the existing feed, dropping those past the retention period.
     *
     * <p>Errors are logged rather than thrown so a scheduled run still publishes new events.
     *
     * @param document              the new feed document
     * @param channel          the channel to append items to
     * @param existingFeedFile the existing feed file (may not exist)
     */
    private void importExistingEvents(Document document, Element channel,
                                      File existingFeedFile) {
        if (!existingFeedFile.exists()) {
            return;
        }
        try {
            DocumentBuilder builder =
                securityConfigurer.createSecureDocumentBuilderFactory().newDocumentBuilder();
            NodeList items = builder.parse(existingFeedFile).getElementsByTagName(ITEM);
            int imported = 0;
            for (int i = 0; i < items.getLength(); i++) {
                Element item = (Element) items.item(i);
                if (eventFilter.shouldKeep(item)) {
                    Node importedNode = document.importNode(item, true);
                    removeWhitespaceNodes(importedNode);
                    channel.appendChild(importedNode);
                    imported++;
                }
            }
            LOG.info("Imported {} existing events, dropped {} past retention",
                imported, items.getLength() - imported);
        } catch (Exception e) {
            LOG.error("Failed to import existing events from {}: {}",
                existingFeedFile, e.getMessage(), e);
        }
    }

    @Override
    public Set<String> loadExistingGuids(String filePath)
        throws Exception {
        requireNonNull(filePath, "filePath must not be null");
        File rssFile = new File(filePath);
        Set<String> guids = new HashSet<>();

        if (!rssFile.exists()) {
            LOG.info("No existing RSS file found at {}", filePath);
            return guids;
        }

        DocumentBuilderFactory factory =
            securityConfigurer.createSecureDocumentBuilderFactory();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(rssFile);

        NodeList guidNodes = document.getElementsByTagName(GUID);
        for (int i = 0; i < guidNodes.getLength(); i++) {
            String guid = guidNodes.item(i).getTextContent();
            guids.add(guid);
        }

        LOG.info("Loaded {} existing GUIDs from {}", guids.size(), filePath);
        return guids;
    }

    /**
     * Removes characters that are not allowed in XML 1.0 from all text, CDATA and attributes.
     *
     * <p>Scraped text can contain control characters (e.g. U+0002). The serializer writes them
     * as character references such as {@code &#2;}, which no XML parser accepts, so the next
     * run could not read the feed.
     *
     * @param root the root node to clean
     */
    private void removeInvalidXmlCharacters(Node root) {
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Node current = stack.pop();
            short type = current.getNodeType();
            if (type == Node.TEXT_NODE || type == Node.CDATA_SECTION_NODE) {
                stripInvalidXmlCharacters(current);
            }
            NamedNodeMap attributes = current.getAttributes();
            if (attributes != null) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    stripInvalidXmlCharacters(attributes.item(i));
                }
            }
            NodeList children = current.getChildNodes();
            for (int i = 0; i < children.getLength(); i++) {
                stack.push(children.item(i));
            }
        }
    }

    /**
     * Removes whitespace-only text nodes from a DOM tree.
     *
     * <p>This is necessary to ensure proper indentation when writing XML.
     *
     * @param node The root node to clean
     */
    private void removeWhitespaceNodes(Node node) {
        Deque<Node> stack = new ArrayDeque<>();
        stack.push(node);

        while (!stack.isEmpty()) {
            Node current = stack.pop();
            NodeList children = current.getChildNodes();

            for (int i = children.getLength() - 1; i >= 0; i--) {
                Node child = children.item(i);
                if (child.getNodeType() == Node.TEXT_NODE) {
                    if (child.getTextContent().trim().isEmpty()) {
                        current.removeChild(child);
                    }
                } else if (child.getNodeType() == Node.ELEMENT_NODE) {
                    stack.push(child);
                }
            }
        }
    }

    private void stripInvalidXmlCharacters(Node node) {
        String value = node.getNodeValue();
        String cleaned = INVALID_XML_CHARACTERS.matcher(value).replaceAll("");
        if (!cleaned.equals(value)) {
            node.setNodeValue(cleaned);
        }
    }

    private void writeXmlToFile(Document document, File outputFile)
        throws TransformerException, IOException {
        removeInvalidXmlCharacters(document);
        TransformerFactory transformerFactory =
            securityConfigurer.createSecureTransformerFactory();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(XSLT_INDENT_PROPERTY, INDENT_AMOUNT);
        transformer.setOutputProperty(OutputKeys.ENCODING, ENCODING_UTF8);

        DOMSource source = new DOMSource(document);

        try (OutputStreamWriter writer = new OutputStreamWriter(
            new FileOutputStream(outputFile), StandardCharsets.UTF_8)) {
            StreamResult result = new StreamResult(writer);
            transformer.transform(source, result);
        }
    }
}
