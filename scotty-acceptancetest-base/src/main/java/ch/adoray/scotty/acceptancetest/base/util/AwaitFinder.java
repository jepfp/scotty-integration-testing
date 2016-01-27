package ch.adoray.scotty.acceptancetest.base.util;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class AwaitFinder {
    
    private AwaitFinder(){
        // Utility Class
    }
    
    public static WebElement awaitFindElement(WebDriver driver, String xpath) {
        WebElement element = (new WebDriverWait(driver, 2)) //
            .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        return element;
    }
    
    public static WebElement awaitFindElement(WebDriver driver, String xpath, int timeoutInSeconds) {
        WebElement element = (new WebDriverWait(driver, timeoutInSeconds)) //
            .until(ExpectedConditions.presenceOfElementLocated(By.xpath(xpath)));
        return element;
    }
}
