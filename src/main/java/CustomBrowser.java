import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
public class CustomBrowser {
    @Getter
    @Setter
    private Playwright playwright;
    @Getter
    @Setter
    private Browser browser;
    @Getter
    @Setter
    private BrowserContext browserContext;

    public void closeAll() {
        this.browserContext.close();
        this.browser.close();
        this.playwright.close();
        this.playwright = null;
        this.browser = null;
        this.browserContext = null;
    }

    public void recreateAll() {
        this.playwright = Playwright.create();
        this.browser = this.playwright.chromium().launch();
        this.browserContext = this.browser.newContext();
    }
}
