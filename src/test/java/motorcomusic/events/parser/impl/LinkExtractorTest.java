package motorcomusic.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LinkExtractorTest {

    private LinkExtractor extractor;

    @Test
    void extract_withAbsoluteUrl_returnsFullUrl() {
        String html = "<div>"
            + "<h4><a href=\"https://motorcomusic.com/event/show\">Event</a></h4>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://motorcomusic.com/event/show");
    }

    @Test
    void extract_withBlankH4Href_returnsBaseUrl() {
        String html = "<div>"
            + "<h4><a href=\"\">Empty</a></h4>"
            + "<a href=\"/event/fallback\">Fallback</a>"
            + "</div>";
        Element element = Jsoup.parse(html, "https://motorcomusic.com").body();

        String result = extractor.extract(element);

        // When href is blank, abs:href returns the base URL
        assertThat(result).isEqualTo("https://motorcomusic.com");
    }

    @Test
    void extract_withBothStrategies_prefersH4Link() {
        String html = "<div>"
            + "<h4><a href=\"/event/h4-link\">H4 Event</a></h4>"
            + "<a href=\"/event/other-link\">Other Event</a>"
            + "</div>";
        Element element = Jsoup.parse(html, "https://motorcomusic.com").body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://motorcomusic.com/event/h4-link");
    }

    @Test
    void extract_withEventLink_returnsAbsoluteUrl() {
        String html = "<div>"
            + "<a href=\"/event/concert-456\">Concert</a>"
            + "</div>";
        Element element = Jsoup.parse(html, "https://motorcomusic.com").body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://motorcomusic.com/event/concert-456");
    }

    @Test
    void extract_withH4Link_returnsAbsoluteUrl() {
        String html = "<div>"
            + "<h4><a href=\"/event/show-123\">Event</a></h4>"
            + "</div>";
        Element element = Jsoup.parse(html, "https://motorcomusic.com").body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://motorcomusic.com/event/show-123");
    }

    @Test
    void extract_withNoH4Link_fallsBackToEventLink() {
        String html = "<div>"
            + "<a href=\"/event/fallback\">Event</a>"
            + "</div>";
        Element element = Jsoup.parse(html, "https://motorcomusic.com").body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://motorcomusic.com/event/fallback");
    }

    @Test
    void extract_withNoLinks_returnsNull() {
        String html = "<div><p>No links</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isNull();
    }

    @BeforeEach
    void setUp() {
        extractor = new LinkExtractor();
    }
}
