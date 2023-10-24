import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.annotations.*;
import java.util.List;
//import java.util.logging.Logger;
//import java.util.logging.Level;

//import org.apache.logging.log4j.Logger;
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.

public class SearchTest {
    private WebDriver driver;
    private CommonMethods commonMethods;
    String parentWindowHandle;

    @BeforeTest
    public void setupWebDriver() {


        //WebDriverManager.chromedriver().setup();
        WebDriverManager.firefoxdriver().setup();
       // ChromeDriverLocal localDrv = new ChromeDriverLocal();
       // localDrv.setup();
       // driver = localDrv.getWebDriver();
        driver = new FirefoxDriver();
        commonMethods = new CommonMethods(driver);
        driver.get("https://www.webstaurantstore.com");
        // Capture the handle of the parent window
        parentWindowHandle = driver.getWindowHandle();
        System.out.println("Current Handle: " + parentWindowHandle);

    }


    @Test(priority = 1)
    public void testSearchRes() {
//        driver.get("https://www.webstaurantstore.com"); // Replace with your URL
        WebElement searchBox = driver.findElement(By.name("searchval"));
        searchBox.sendKeys(("stainless work table"));
        searchBox.submit();


       try {
            Thread.sleep(2000); // Wait for 1 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        commonMethods.waitForElement(By.cssSelector(".page-header"), 5);
        WebElement pageTitleElement = driver.findElement(By.cssSelector(".page-header"));
        String pageTitleText = pageTitleElement.getText();

        // Show an alert box with the result count
        System.out.println("Page Title Text : " + pageTitleText);
        commonMethods.takeScreenshot("Searched item");

        //Verify search results for text = 'table'
        WebElement searchResCont = driver.findElement(By.id("product_listing"));
        for (WebElement resProds : searchResCont.findElements(By.tagName("li"))) {
           // String itemDes = resProds.getAttribute("data-testid");
            WebElement itemDesEle = resProds.findElement(By.xpath(".//span[@data-testid='itemDescription']"));
            String itemDes = itemDesEle.getText();
            Assert.assertEquals(itemDes.toLowerCase().contains("table"), "Item Description does not contain the word 'table");
        }

    }

    @Test(priority = 2)
    public void addLastToCart() {

        // Scroll to the last page number in pagination
        WebElement pagination = driver.findElement(By.id("paging"));
        WebElement lastPageLink = pagination.findElement(By.cssSelector("a.pagerLink:last-child"));
        // Print the page title
        System.out.println("Page Title Before click: " + driver.getCurrentUrl());

        // Use JavascriptExecutor to scroll to the lastPageLink element
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", lastPageLink);


        // Wait for the lastPageLink to be clickable
        WebDriverWait wait = new WebDriverWait(driver, 2);
        wait.until(ExpectedConditions.elementToBeClickable(lastPageLink));

        // Click on the last page number
        lastPageLink.click();
        commonMethods.takeScreenshot("last page link");
        // Print the page title
        System.out.println("Page Title After click: " + driver.getCurrentUrl());

        try {
            Thread.sleep(1000); // Wait for 1 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        commonMethods.takeScreenshot("last page loaded");
        //  Add the last item to the cart
       List<WebElement> addToCartButtons = driver.findElements(By.cssSelector("[data-testid='itemAddCart']"));
        // Check if there are any buttons
        if (!addToCartButtons.isEmpty()) {
            // Click the last "Add to Cart" button
            WebElement lastAddToCartButton = addToCartButtons.get(addToCartButtons.size() - 1);
            lastAddToCartButton.click();
            js.executeScript("arguments[0].scrollIntoView(true);", lastPageLink);
            commonMethods.takeScreenshot("item from page");
        } else {
            Assert.fail("No 'Add to Cart' buttons found with data-testid='itemAddCart'");
        }
        // Take a screenshot after adding the last item to the cart
        commonMethods.takeScreenshot("item_added_to_cart");
    }

    @Test(priority = 3)
    public void emptyCart() {
        // Step 4: Navigate to the cart
        //driver.findElement(By.cssSelector("[data-testid='cart-button']")).click();

        // Locate the "View Cart" link by link text
        WebElement viewCartLink = driver.findElement(By.linkText("View Cart"));

        // Click the "View Cart" link to open the cart in a popup or new window
        viewCartLink.click();
        commonMethods.takeScreenshot("View Cart");

        // Step 5: Empty the cart
        commonMethods.waitForElement(By.cssSelector(".emptyCartButton"), 5);
        driver.findElement(By.cssSelector(".emptyCartButton")).click();

        // Switch to the modal context
        for (String handle : driver.getWindowHandles()) {
            if (!handle.equals(parentWindowHandle)) {
                driver.switchTo().window(handle);
                break;
            }
        }

        String currentWindowHandle = driver.getWindowHandle();
        System.out.println("Current Handle: " + currentWindowHandle);
        // Now, locate and click the "Empty Cart" button within the modal
       // WebElement emptyCartButton = driver.findElement(By.xpath("//button[contains(text(), 'Empty Cart')]"));


        WebElement emptyCartButton = driver.findElement(By.cssSelector("mr-2"));
        emptyCartButton.click();

        //driver.findElement(By.id("emptyCartButton")).click();

        // Take a screenshot after emptying the cart
        commonMethods.takeScreenshot("cart_empty");
        System.out.println("Current Handle: " + currentWindowHandle);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
