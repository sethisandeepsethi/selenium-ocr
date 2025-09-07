package com.selocr.pages;

import com.selocr.utils.AIElementClicker;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.time.Duration;

public abstract class AbstractPage {
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    public AbstractPage(WebDriver driver){
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(30));
        PageFactory.initElements(driver,this);
    }

    public abstract boolean isLoaded();

    public void aiClick( String elementText) throws IOException {
            AIElementClicker.clickElementUsingOCR(driver,elementText);
    }
}
