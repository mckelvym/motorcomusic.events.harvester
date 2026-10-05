package motorcomusic.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TitleExtractorTest {

    private TitleExtractor extractor;

    @Test
    void extract_withBlankElements_returnsNull() {
        String html = "<div>"
            + "<h4><a>   </a></h4>"
            + "<h4>   </h4>"
            + "<a href=\"/event/123\">  </a>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isNull();
    }

    @Test
    void extract_withBlankEventLink_returnsNull() {
        String html = "<div><a href=\"/event/123\">   </a></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isNull();
    }

    @Test
    void extract_withBlankH4Link_returnsNull() {
        String html = "<div>"
            + "<h4><a>   </a></h4>"
            + "<h4>Other Title</h4>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isNull();
    }

    @Test
    void extract_withBlankH4_usesEventLink() {
        String html = "<div>"
            + "<h4>   </h4>"
            + "<a href=\"/event/123\">Event Title</a>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Event Title");
    }

    @Test
    void extract_withComplexEventLinkHref_returnsTitle() {
        String html = "<div><a href=\"https://motorcomusic.com/event/view/123\">Complex "
            + "Event</a></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Complex Event");
    }

    @Test
    void extract_withEventLink_returnsTitle() {
        String html = "<div><a href=\"/event/123\">Concert</a></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Concert");
    }

    @Test
    void extract_withH4LinkPriority_ignoresH4AndLink() {
        String html = "<div>"
            + "<h4><a>H4 Link Title</a></h4>"
            + "<h4>H4 No Link</h4>"
            + "<a href=\"/event/123\">Event Link</a>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("H4 Link Title");
    }

    @Test
    void extract_withH4LinkWhitespace_returnsTrimmedTitle() {
        String html = "<div><h4><a>  Music Event  </a></h4></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Music Event");
    }

    @Test
    void extract_withH4Link_returnsTitle() {
        String html = "<div><h4><a>Live Concert</a></h4></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Live Concert");
    }

    @Test
    void extract_withH4NoLink_returnsTitle() {
        String html = "<div><h4>Show Title</h4></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Show Title");
    }

    @Test
    void extract_withMultipleEventLinks_returnsFirstOne() {
        String html = "<div>"
            + "<h4>   </h4>"
            + "<a href=\"/event/123\">First Event</a>"
            + "<a href=\"/event/456\">Second Event</a>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("First Event");
    }

    @Test
    void extract_withMultipleH4Links_returnsFirstOne() {
        String html = "<div>"
            + "<h4><a>First Show</a></h4>"
            + "<h4><a>Second Show</a></h4>"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("First Show");
    }

    @Test
    void extract_withNestedElements_returnsTitle() {
        String html = "<div><h4><a>Show <span>Name</span></a></h4></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("Show Name");
    }

    @Test
    void extract_withNoMatchingElements_returnsNull() {
        String html = "<div><p>Some content</p><span>More content</span></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isNull();
    }

    @BeforeEach
    void setUp() {
        extractor = new TitleExtractor();
    }
}
