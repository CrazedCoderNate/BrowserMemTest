import com.microsoft.playwright.Page;
import com.microsoft.playwright.PlaywrightException;
import com.microsoft.playwright.options.LoadState;
import lombok.Setter;

import java.util.Objects;
import java.util.concurrent.Callable;

public class BrowserNav implements Callable {


    @Setter
    private String url;

    private CustomBrowser customBrowser;

    public BrowserNav(final CustomBrowser customBrowser) {
        if(Objects.isNull(customBrowser)) {
            // No browser available
            System.exit(1);
        }
        this.customBrowser = customBrowser;
    }

    @Override
    public String call() {
        return pageContent(url);
    }

    public String pageContent(final String url) {
        try (final Page page = this.customBrowser.getBrowserContext().newPage()) { // Try with resources with a new page
            // Go to page, wait for dom content, grab all text within body
            page.navigate(url);
            page.waitForLoadState(LoadState.DOMCONTENTLOADED);
            final String content = String.join("", page.locator("body").allInnerTexts());

            // Recreates all playwright related content. This should "Flush" memory
            synchronized (customBrowser) {
                customBrowser.closeAll();
                customBrowser.recreateAll();
            }
            // Return all body text
            return content;
        } catch (final PlaywrightException pe) {
            pe.printStackTrace();
            return "";
        }
    }

}
