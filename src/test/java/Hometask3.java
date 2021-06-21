import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Hometask3 {

    Logger logger = LogManager.getLogger(Hometask3.class);
    protected static WebDriver driver;
    protected Actions action;
    private Object InterruptedException;

    @Before
    public void startUp() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        //options.addArguments("headless");
        options.addArguments("--window-size=1920,1080");
        driver = new ChromeDriver(options);
        action = new Actions(driver);
        logger.info("Драйвер запущен.");
    }

    @After
    public void end() {
        if (driver != null) {
            File file = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            saveFile(file);
            driver.quit();
            logger.info("Драйвер остановлен. Сделан скриншот.\n\n\n");
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
    public void internetShopCompare() throws Exception {
        String baseUrl = getProperties("baseUrl220volt");
        String item1 = getProperties("brand1");
        String item2 = getProperties("brand2");

        driver.get(baseUrl);
        goToPerforators();
        sortMintoMax();

        getElement(By.tagName("body")).sendKeys(Keys.SPACE);
        getElement(By.xpath("//*[@title='MAKITA']/../input")).click();
        getElement(By.xpath("//*[text()=' Показать: ']/a['#']")).click();
        getElement(By.xpath("//*[@title='ЗУБР']/../input")).click();
        getElement(By.xpath("//*[text()=' Показать: ']/a['#']")).click();
        logger.info("Отображаем найденные позиции");

        Thread.sleep(5000);
        String item1Minimal = getItem(item1);
        driver.get(baseUrl + "catalog/perforatory/");
        getElement(By.tagName("body")).sendKeys(Keys.SPACE);
        String item2Minimal = getItem(item2);
        driver.get(baseUrl + "compare/");
        logger.info("Перешли к странице сравнения {}", baseUrl + "compare/");

        String actual1 = "Перфоратор " + getElement(By.xpath("//*[contains(@data-product-title,'Перфоратор " + item1 + "')]")).getText();
        String actual2 = "Перфоратор " + getElement(By.xpath("//*[contains(@data-product-title,'Перфоратор " + item2 + "')]")).getText();
        Assert.assertEquals("Ошибка: к сравнению отобрано не две позиции", "2", getElement(By.xpath("//*[@id=\"cCountCompare\"]")).getText());
        Assert.assertEquals("Ошибка. Сравниваем не ту позицию, что собирались", item1Minimal, actual1);
        Assert.assertEquals("Ошибка. Сравниваем не ту позицию, что собирались", item2Minimal, actual2);
    }

    public WebElement getElement(By locator) {
        logger.info("Получение элемента {}", locator);
        return new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(locator));
    }

    private WebElement sortMintoMax() {
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

    private WebElement goToPerforators() {
        WebElement electro = getElement(By.xpath("//*[@title=\"Электроинструменты\"]"));
        action.moveToElement(electro).pause(400L).perform();
        WebElement perforator = getElement(By.xpath("//*[@title=\"Перфораторы\"]"));
        action.moveToElement(perforator).pause(400L).click().perform();
        logger.info("Курсор наведен на Электроинструменты -> Перфораторы. Выполнен клик.");
        String compareLocator = "//*[@title=\"Добавить к сравнению\"]";
        return new WebDriverWait(driver, 10).until(ExpectedConditions.elementToBeClickable(By.xpath(compareLocator)));
    }

    public String getItem(String name){
        getElement(By.xpath("//*[contains(@data-product-title,'Перфоратор " + name + "')]")).click(); ///../..//*[@title='Добавить к сравнению']
        getElement(By.xpath("//*[text()='К сравнению']")).click();
        String titleLocator = "//*[@class='infoCompareContainer']//span[@class='titleProduct']";
        String continueLocator = titleLocator + "/../../../..//*[@class=\"activeButton\"]";
        String title = getElement(By.xpath(titleLocator)).getText();
        logger.info("Добавлен к сравнению {}", title);
        getElement(By.xpath(continueLocator)).click();
        logger.info("Закрыли infoCompareContainer");
        return title;
    }

    public String getProperties(String name) throws Exception{
        String configfile = "src/main/resources/config.properties";
        FileInputStream propFile = new FileInputStream(configfile);
        Properties p = new Properties(System.getProperties());
        p.loadFromXML(propFile);
        // set the system properties
        System.setProperties(p);
        logger.info("Получение переменной " + name + " из файла " + configfile);
        return System.getProperties().getProperty(name);
    }

}