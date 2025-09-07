package com.selocr.utils;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.io.FileHandler;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.JavascriptExecutor;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

import net.sourceforge.tess4j.*;
import net.sourceforge.tess4j.util.LoadLibs;
import com.sun.jna.Pointer; // provided by tess4j deps
import net.sourceforge.tess4j.ITessAPI;

public class AIElementClicker {

    private static ITesseract makeTesseract() {

        System.setProperty("jna.library.path", "/opt/homebrew/lib");         // where libtesseract.dylib is
        System.setProperty("TESSDATA_PREFIX", "/opt/homebrew/share/");       // parent of tessdata

        Tesseract t = new Tesseract();

        // If you don't have tessdata on PATH, let Tess4J unzip its bundled data:
        //File tessDataFolder = LoadLibs.extractTessResources("tessdata");
        //t.setDatapath(tessDataFolder.getAbsolutePath());
        t.setDatapath("/opt/homebrew/share/tessdata");

        // Optional: set language (ensure the traineddata exists in tessdata)
        t.setLanguage("eng");

        // Optional: improve speed/accuracy trade-offs
        // t.setTessVariable("user_defined_dpi", "300");

        return t;
    }

    public static boolean clickElementUsingOCR(WebDriver driver, String ocrText) throws IOException {
        boolean result = false;

        // 2) Take a viewport screenshot
        File png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        BufferedImage img = ImageIO.read(png);

        // 3) OCR: get words with bounding boxes
        ITesseract tesseract = makeTesseract();
        List<Word> words = tesseract.getWords(img, ITessAPI.TessPageIteratorLevel.RIL_WORD);

        words.stream().forEach(System.out::println);

        // 4) Find a candidate that matches OCR Text supplied (case-insensitive, trims punctuation)
        Word best = null;
        for (Word w : words) {
            String txt = (w.getText() == null ? "" : w.getText()).trim();
            String normalized = txt.toLowerCase(Locale.ROOT);
                    //.replaceAll("[^a-z0-9]+", "");
            if (normalized.equalsIgnoreCase(ocrText)) {
                best = w;
                break;
            }
            // looser fallback: contains "ocrText"
            if (best == null && normalized.contains(ocrText)) {
                best = w;
            }
        }

        if (best == null) {
            System.out.printf("❌ Text %s not detected by OCR.", ocrText);
            return result;
        }

        // 5) Center of OCR box in *image* pixels
        java.awt.Rectangle bb = best.getBoundingBox();
        double imgCenterX = bb.x + bb.width / 2.0;
        double imgCenterY = bb.y + bb.height / 2.0;

        // 6) Map image pixels → CSS pixels (viewport coords)
        //    Selenium screenshot sizes often equal device-pixel dimensions,
        //    whereas JS coordinates use CSS pixels. Use innerWidth/innerHeight
        //    to compute scale factors.
        JavascriptExecutor js = (JavascriptExecutor) driver;
        long innerWidth  = ((Number) js.executeScript("return window.innerWidth;")).longValue();
        long innerHeight = ((Number) js.executeScript("return window.innerHeight;")).longValue();

        int imgW = img.getWidth();
        int imgH = img.getHeight();

        double scaleX = (double) innerWidth  / imgW;
        double scaleY = (double) innerHeight / imgH;

        long cssX = Math.round(imgCenterX * scaleX);
        long cssY = Math.round(imgCenterY * scaleY);

        // 7) Click the element at that point using elementFromPoint
        Object clicked = js.executeScript(
                "const x=arguments[0], y=arguments[1];" +
                        "const el = document.elementFromPoint(x,y);" +
                        "if (el) { el.scrollIntoView({block:'center'}); el.click(); }" +
                        "return el ? el.outerHTML : null;",
                cssX, cssY
        );

        if (clicked != null) {
            System.out.println("✅ Clicked element with text " +ocrText+ " at (" + cssX + "," + cssY + "):\n" + clicked);
            result = true;
        } else {
            System.out.println("⚠️ Nothing at (" + cssX + "," + cssY + ").");
            result = false;
        }

        return result;
    }

    public static void main(String[] args) throws Exception {
        WebDriver driver = new ChromeDriver();
        try {
            // 1) Open a page that has a visible "Login" button
            driver.manage().window().setSize(new Dimension(1280, 800));
            driver.get("https://example.com/"); // <== change to your AUT page

            // (Optional) wait for page to settle
            Thread.sleep(1500);

            // 2) Take a viewport screenshot
            File png = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            BufferedImage img = ImageIO.read(png);

            // 3) OCR: get words with bounding boxes
            ITesseract tesseract = makeTesseract();
            List<Word> words = tesseract.getWords(img, ITessAPI.TessPageIteratorLevel.RIL_WORD);

            // 4) Find a candidate that matches "Login" (case-insensitive, trims punctuation)
            Word best = null;
            for (Word w : words) {
                String txt = (w.getText() == null ? "" : w.getText()).trim();
                String normalized = txt.toLowerCase(Locale.ROOT)
                        .replaceAll("[^a-z0-9]+", "");
                if (normalized.equals("login")) {
                    best = w;
                    break;
                }
                // looser fallback: contains "login"
                if (best == null && normalized.contains("login")) {
                    best = w;
                }
            }

            if (best == null) {
                System.out.println("❌ 'Login' not detected by OCR.");
                return;
            }

            // 5) Center of OCR box in *image* pixels
            java.awt.Rectangle bb = best.getBoundingBox();
            double imgCenterX = bb.x + bb.width / 2.0;
            double imgCenterY = bb.y + bb.height / 2.0;

            // 6) Map image pixels → CSS pixels (viewport coords)
            //    Selenium screenshot sizes often equal device-pixel dimensions,
            //    whereas JS coordinates use CSS pixels. Use innerWidth/innerHeight
            //    to compute scale factors.
            JavascriptExecutor js = (JavascriptExecutor) driver;
            long innerWidth  = ((Number) js.executeScript("return window.innerWidth;")).longValue();
            long innerHeight = ((Number) js.executeScript("return window.innerHeight;")).longValue();

            int imgW = img.getWidth();
            int imgH = img.getHeight();

            double scaleX = (double) innerWidth  / imgW;
            double scaleY = (double) innerHeight / imgH;

            long cssX = Math.round(imgCenterX * scaleX);
            long cssY = Math.round(imgCenterY * scaleY);

            // 7) Click the element at that point using elementFromPoint
            Object clicked = js.executeScript(
                    "const x=arguments[0], y=arguments[1];" +
                            "const el = document.elementFromPoint(x,y);" +
                            "if (el) { el.scrollIntoView({block:'center'}); el.click(); }" +
                            "return el ? el.outerHTML : null;",
                    cssX, cssY
            );

            if (clicked != null) {
                System.out.println("✅ Clicked element at (" + cssX + "," + cssY + "):\n" + clicked);
            } else {
                System.out.println("⚠️ Nothing at (" + cssX + "," + cssY + ").");
            }

        } finally {
            // Keep browser open a bit so you can see the click (optional)
            Thread.sleep(1500);
            driver.quit();
        }
    }
}
