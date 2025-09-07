package com.selocr.pages;

import com.selocr.pages.AbstractPage;
import com.selocr.utils.AIElementClicker;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.io.IOException;

public class LoginPage extends AbstractPage {

    @FindBy(id = "user-name")
    private WebElement usernameInput;

    @FindBy(id = "password")
    private WebElement passwordInput;

    @FindBy(id = "login-button")
    private WebElement loginButton;

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Override
    public boolean isLoaded() {
        this.wait.until(ExpectedConditions.visibilityOf(this.loginButton));
        return this.loginButton.isDisplayed();
    }

    public void goTo(String url){
        this.driver.get(url);
    }

    public void doLogin(String username, String password) throws IOException {
        this.usernameInput.sendKeys(username);
        this.passwordInput.sendKeys(password);
        //this.loginButton.click();
        aiClick("Login");
    }

}
