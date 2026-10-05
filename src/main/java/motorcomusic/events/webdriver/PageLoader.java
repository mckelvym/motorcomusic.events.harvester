package motorcomusic.events.webdriver;

import static java.util.Objects.requireNonNull;

import java.time.Duration;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Loads web pages using Selenium and parses them with JSoup.
 * Waits for JavaScript rendering before parsing HTML.
 */
public record PageLoader(WebDriver driver, Duration timeout) {

    private static final Logger LOG = LoggerFactory.getLogger(PageLoader.class);

    /**
     * Creates a new PageLoader.
     *
     * @param driver  the WebDriver to use
     * @param timeout the page load timeout
     */
    public PageLoader(final WebDriver driver, final Duration timeout) {
        this.driver = requireNonNull(driver);
        this.timeout = requireNonNull(timeout);
    }

    /**
     * Loads a URL and waits for the specified selector to appear.
     * Returns the parsed HTML document.
     *
     * @param url      The URL to load
     * @param selector The CSS selector to wait for
     * @return The parsed JSoup document
     */
    public Document loadPage(String url, String selector) {
        requireNonNull(url, "url must not be null");
        requireNonNull(selector, "selector must not be null");
        LOG.info("Loading page: {}", url);
        driver.get(url);

        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));

        String pageSource = driver.getPageSource();
        return Jsoup.parse(requireNonNull(pageSource));
    }
}
