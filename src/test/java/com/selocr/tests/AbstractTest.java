package com.selocr.tests;

import com.google.common.util.concurrent.Uninterruptibles;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.BeforeTest;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public abstract class AbstractTest {

    protected WebDriver driver;
    //public static Logger

    @BeforeSuite
    public void initializeConfigs(){
        System.out.println("Config initializations goes");
    }

    @BeforeTest
    public void setDriver() throws MalformedURLException {
        this.driver = Boolean.parseBoolean("isRemote") ? getRemoteDriver() : getLocalDriver();
    }

    private WebDriver getRemoteDriver() throws MalformedURLException {
        Capabilities capabilities = new ChromeOptions();

        if (System.getProperty("browser").equalsIgnoreCase("firefox"))
            capabilities = new FirefoxOptions();

        String urlFormat = "http://%s:4444/wd/hub";
        String hubHost = "localhost";
        String url = String.format(urlFormat, hubHost);

        return new RemoteWebDriver(new URL(url), capabilities);
    }

    private WebDriver getLocalDriver() {
        System.out.println("System property browser = " +System.getProperty("browser") );
        if (System.getProperty("browser").equalsIgnoreCase("firefox")) {
            System.out.println("Initializing FF");
            WebDriverManager.firefoxdriver().setup();
            return new FirefoxDriver();
        }else if (System.getProperty("browser").equalsIgnoreCase("safari")) {
            System.out.println("Initializing safari");
            WebDriverManager.safaridriver().setup();
            return new SafariDriver();
        }

        System.out.println("Initializing chrome");
        WebDriverManager.chromedriver().setup();
        ChromeOptions opts = new ChromeOptions();
        opts.addArguments("--force-device-scale-factor=2");
        return new ChromeDriver(opts);
    }

    @AfterTest
    public void quitDriver(){
        this.driver.quit();
    }

    @AfterMethod
    public void sleep(){
        Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(3));
    }


}
