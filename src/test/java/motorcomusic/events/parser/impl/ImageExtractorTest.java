package motorcomusic.events.parser.impl;

import static org.assertj.core.api.Assertions.assertThat;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ImageExtractorTest {

    private ImageExtractor extractor;

    @Test
    void extract_withAbsoluteUrl_returnsFullUrl() {
        String html = "<div>"
            + "<img src=\"https://example.com/image.jpg\" />"
            + "</div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://example.com/image.jpg");
    }

    @Test
    void extract_withAttachmentAndGeneric_prefersAttachment() {
        String html = "<div>"
            + "<img class=\"attachment-tc_all_events_image\" src=\"/images/attachment.jpg\" />"
            + "<img src=\"/images/generic.jpg\" />"
            + "</div>";
        Element element = Jsoup.parse(html, "https://motorcomusic.com").body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://motorcomusic.com/images/attachment.jpg");
    }

    @Test
    void extract_withAttachmentImage_returnsAbsoluteUrl() {
        String html = "<div>"
            + "<img class=\"attachment-tc_all_events_image\" src=\"/images/event.jpg\" />"
            + "</div>";
        Element element = Jsoup.parse(html, "https://motorcomusic.com").body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://motorcomusic.com/images/event.jpg");
    }

    @Test
    void extract_withBlankAttachment_returnsBaseUrl() {
        String html = "<div>"
            + "<img class=\"attachment-tc_all_events_image\" src=\"\" />"
            + "<img src=\"/images/fallback.jpg\" />"
            + "</div>";
        Element element = Jsoup.parse(html, "https://motorcomusic.com").body();

        String result = extractor.extract(element);

        // When src is blank, abs:src returns the base URL
        assertThat(result).isEqualTo("https://motorcomusic.com");
    }

    @Test
    void extract_withNoAttachment_fallsBackToAnyImg() {
        String html = "<div>"
            + "<img src=\"/images/generic.jpg\" />"
            + "</div>";
        Element element = Jsoup.parse(html, "https://motorcomusic.com").body();

        String result = extractor.extract(element);

        assertThat(result).isEqualTo("https://motorcomusic.com/images/generic.jpg");
    }

    @Test
    void extract_withNoImage_returnsEmptyString() {
        String html = "<div><p>No images</p></div>";
        Element element = Jsoup.parse(html).body();

        String result = extractor.extract(element);

        assertThat(result).isEmpty();
    }

    @BeforeEach
    void setUp() {
        extractor = new ImageExtractor();
    }
}
