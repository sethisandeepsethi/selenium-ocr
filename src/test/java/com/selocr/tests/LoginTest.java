package com.selocr.tests;

import com.selocr.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class LoginTest extends AbstractTest {
    private LoginPage loginPage;

    @BeforeTest
    public void setPageObjects() {
        loginPage = new LoginPage(driver);
    }

    @Test
    public void validLoginTest() throws IOException {
        loginPage.goTo("https://www.saucedemo.com/");
        Assert.assertTrue(loginPage.isLoaded());
        loginPage.doLogin("standard_user","secret_sauce");
        sleep();
    }


}
