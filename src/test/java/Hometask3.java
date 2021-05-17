import io.github.bonigarcia.wdm.WebDriverManager;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.io.File;
import java.io.IOException;
import org.openqa.selenium.WebDriver;

public class Hometask3 {

    Logger logger = LogManager.getLogger(Hometask3.class);
    protected static WebDriver driver;
    protected Actions action;

    public interface ServerConfig extends Config {
        String hostname();
    }

    @Before
    public void startUp(){
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("headless");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        action = new Actions(driver);
    }
    @After
    public void end(){
        if (driver!=null) {
            File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            saveFile(file);
            driver.quit();
            logger.info("Драйвер остановлен. Сделан скриншот.");
        }
    }

    private void saveFile(File data) {
        String fileName = "target/" + System.currentTimeMillis() + ".png";
        try {
            FileUtils.copyFile(data, new File(fileName));
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @Test
    public void internetShopCompare() throws InterruptedException {
        //ServerConfig cfg = ConfigFactory.create(ServerConfig.class);
        //System.out.println("baseUrl: " + cfg.hostname());
        String baseUrl = "https://www.220-volt.ru/";
        driver.get(baseUrl);
        goToPerforators();
        sortMintoMax();
        String item1 = "MAKITA"; String item2 = "ЗУБР";
        selectVendor(item1,item2);
        closeAdvertising();

        String item1Minimal = getItem(item1);
        String item2Minimal = getItem(item2);
        String compareUrl = baseUrl + "compare/";
        driver.get(compareUrl);
        logger.info("Перешли к странице сравнения {}", compareUrl);
        String actual1 = "Перфоратор " + getElement(By.xpath("//*[contains(@data-product-title,'Перфоратор " + item1 + "')]")).getText();
        String actual2 = "Перфоратор " + getElement(By.xpath("//*[contains(@data-product-title,'Перфоратор " + item2 + "')]")).getText();
        Assert.assertEquals("Ошибка: к сравнению отобрано не две позиции", "2", getElement(By.xpath("//*[@id=\"cCountCompare\"]")).getText());
        Assert.assertEquals("Ошибка. Сравниваем не ту позицию, что собирались", item1Minimal, actual1);
        Assert.assertEquals("Ошибка. Сравниваем не ту позицию, что собирались", item2Minimal, actual2);
    }

    public WebElement getElement(By locator){
        logger.info("Получение элемента {}", locator);
        return new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(locator));
    }

    private WebElement selectVendor(String name1, String name2) throws InterruptedException {
        WebElement body = getElement(By.tagName("body"));
        body.sendKeys(Keys.SPACE);
        String customLocator1 = "//*[@title=\"" + name1 + "\"]/../input";
        String customLocator2 = "//*[@title=\"" + name2 + "\"]/../input";
        logger.info("Фильтруемся по производителю {}", name1);
        WebElement selectedItem1 = getElement(By.xpath(customLocator1));
        WebElement selectedItem2 = getElement(By.xpath(customLocator2));
        String countItemsLocator = "//*[@class=\"notifyQuantity rounded5 hide\"]/a";

        action.moveToElement(selectedItem1).pause(900L).click().perform();
        logger.info("Найдено по фильтру {}", getElement(By.xpath(countItemsLocator)).getText());
        action.moveToElement(selectedItem2).pause(400L).click().perform();
        action.moveToElement(selectedItem1).pause(1500L).perform();
        logger.info("Найдено по фильтру {}", getElement(By.xpath(countItemsLocator)).getText());
        String showFoundItemsLocator = "//*[text()=\" Показать: \"]/a[\"#\"]";
        getElement(By.xpath(showFoundItemsLocator)).click();
        String compareLocator = "//*[@title=\"Добавить к сравнению\"]";
        return new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath(compareLocator)));
    }

    private WebElement closeModalCity(){
        String cityLocator = "//*[text()=\"Вы хотите получить товар в городе\"]/../../div[2]/span";
        return new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath(cityLocator)));
    }

    private WebElement closeModalWidget(){
        String widgetLocator = "//*[@class=\"widget__close\"]";
        return new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath(widgetLocator)));
    }

    private WebElement closePromoWidget(){
        String promoCloseLocator = "//*[@class=\"widget__close\"]/span";
        return new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath(promoCloseLocator)));
    }

    private WebElement closeModalUnderstand(){
        String understandLocator = "//*[@class=\"close\"]";
        return new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath(understandLocator)));
    }

    private WebElement sortMintoMax(){
        String sortLocator = "//*[text()=\"Сортировать по:\"]/../../span/span";
        WebElement sort = getElement(By.xpath(sortLocator));
        action.moveToElement(sort).pause(400L).click().perform();
        logger.info("Курсор наведен на Сортировать по. Выполнен клик.");

        String priceMinToMaxLocator = "//*/span/ul/li";
        WebElement priceMinToMax = getElement(By.xpath(priceMinToMaxLocator));
        action.moveToElement(priceMinToMax).pause(400L).click().perform();
        logger.info("Сортируем по увеличению цены");
        String compareLocator = "//*[@title=\"Добавить к сравнению\"]";
        return new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath(compareLocator)));
    }

    private WebElement goToPerforators(){
        WebElement electro = getElement(By.xpath("//*[@title=\"Электроинструменты\"]"));
        action.moveToElement(electro).pause(400L).perform();
        WebElement perforator = getElement(By.xpath("//*[@title=\"Перфораторы\"]"));
        action.moveToElement(perforator).pause(400L).click().perform();
        logger.info("Курсор наведен на Электроинструменты -> Перфораторы. Выполнен клик.");
        String compareLocator = "//*[@title=\"Добавить к сравнению\"]";
        return new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath(compareLocator)));
    }

    public String getItem(String name){
        WebElement addToCompareProduct = getElement(By.xpath("//*[contains(@data-product-title,'Перфоратор " + name + "')]/../..//*[@title=\"Добавить к сравнению\"]"));
        addToCompareProduct.click();
        WebElement titleProduct = getElement(By.xpath("//*[@class=\"infoCompareContainer\"]//span[@class=\"titleProduct\"]"));
        String title = titleProduct.getText();
        logger.info("Добавлен к сравнению {}", title);
        WebElement continueResearchButton = getElement(By.xpath("(//*[@class=\"infoCompareContainer\"]//div/a[\"#\"])[1]"));
        action.moveToElement(continueResearchButton).pause(900L).click().perform();
        action.moveToElement(getElement(By.xpath("//*[@title=\"Добавить к сравнению\"]"))).pause(900L).perform();
        logger.info("Закрыли infoCompareContainer");
        return new String(title);
    }

    //Добавлено от безысходности ввиду плавающей ошибки по типу:
    //org.openqa.selenium.ElementClickInterceptedException: element click intercepted: Element <i title="Добавить к сравнению" class="icon icon-black icon-compare-black">...</i> is not clickable at point (940, 380). Other element would receive the click: <div class="ui-dialog-wrapper ui-dialog-wrapper-visible" style="position: fixed; width: 100%; height: 100%; top: 0px; z-index: 1008; overflow: auto scroll; background: rgba(0, 0, 0, 0.8);">...</div>
    private void closeAdvertising(){
        try {
            closeModalCity().click();
            logger.info("Закрытие модального окна Выбора города");
        } catch (Exception Modal){
            logger.info("Упали на модалке Выбора города");
        }

        try {
            closePromoWidget().click();
            logger.info("Закрытие модального окна Promo");
        } catch (Exception Modal){
            logger.info("Упали на модалке Promo");
        }

        try {
            closeModalWidget().submit();
            logger.info("Закрытие модального окна виджета");
        } catch (Exception Modal){
            logger.info("Упали на модалке виджета");
        }

        try {
            closeModalUnderstand().click();
            logger.info("Закрытие модального Понятно");
        } catch (Exception Modal){
            logger.info("Упали на модалке Понятно");
        }
    }

}