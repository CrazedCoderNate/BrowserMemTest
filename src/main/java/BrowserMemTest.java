import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Playwright;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

public class BrowserMemTest {
    public static final String EXAMPLE_PAGE = "https://hmpg.net/";
    public static final int MAX_BROWSER_NUM = 10;
    private static final ThreadPoolExecutor executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(MAX_BROWSER_NUM);
    private static final CompletionService completionService = new ExecutorCompletionService(executor);
    private static final List<BrowserNav> browserNavPool = new ArrayList<>();

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        // Initializes our pool of Callable objects.
        for(int i = 0; i < MAX_BROWSER_NUM; i++) {
            final Playwright playwright = Playwright.create();
            final Browser browser = playwright.chromium().launch();
            final BrowserContext browserContext = browser.newContext();
            final CustomBrowser customBrowser = new CustomBrowser(playwright, browser, browserContext);
            browserNavPool.add(new BrowserNav(customBrowser));
        }

        // Run scrape jobs.
        while(true) {
            System.out.println(String.join("", runBrowserThreads()));
        }
    }

    // Run our MAX_BROWSER_NUM threads to get text content from EXAMPLE_PAGE and return
    private static List<String> runBrowserThreads() throws InterruptedException, ExecutionException {
        final List<Future<String>> results = new ArrayList<>();
        final List<String> contents = new ArrayList<>();
        for(final BrowserNav browserNav : browserNavPool) {
            browserNav.setUrl(EXAMPLE_PAGE);
            results.add(completionService.submit(browserNav));
        }
        for(int i = 0; i < MAX_BROWSER_NUM; i++) {
            // Get response from thread call method
            final Future<String> futureResult = completionService.take();
            contents.add(futureResult.get());
        }
        return contents;
    }
}
