
# Selenium OCR (AI-Based Element Detection)

This project demonstrates how to use **Selenium WebDriver** in combination with **Tesseract OCR** (via Tess4J) to detect and interact with UI elements visually, instead of relying on brittle XPath/CSS locators.  
It showcases a proof-of-concept of **AI-powered element clicking**: identifying text labels on buttons (e.g., "Login") directly from screenshots, then mapping them back to browser coordinates to perform clicks.

---

## 📂 Project Structure

```

sel-ocr/
├── pom.xml                          # Maven project file
├── .gitignore
├── src/
│   ├── main/
│   │   └── java/com/selocr/
│   │       ├── pages/
│   │       │   ├── AbstractPage.java
│   │       │   └── LoginPage.java
│   │       └── utils/
│   │           └── AIElementClicker.java
│   └── test/
│       ├── java/com/selocr/tests/
│       │   ├── AbstractTest.java
│       │   └── LoginTest.java
│       └── resources/test-suites/
│           └── testng.xml

````

---

## ⚙️ Features

- **AI-driven element detection** using **OpenCV + Tesseract OCR** (via Tess4J).
- Locates button text (e.g., `"Login"`) visually on the screen.
- Maps OCR coordinates → browser coordinates.
- Clicks the element with Selenium’s `elementFromPoint` approach.
- Example page object classes (`LoginPage`, `AbstractPage`) for modular test design.
- Integration with **TestNG** for structured test execution.

---

## 🛠️ Prerequisites

1. **Java 17+** (tested with JDK 21).
2. **Maven 3.8+**
3. **Chrome/Chromedriver** (Selenium-managed).
4. **Tesseract OCR** installed on your system:
    - macOS (Apple Silicon):
      ```bash
      brew install tesseract
      ```
    - Ubuntu/Debian:
      ```bash
      sudo apt-get install tesseract-ocr libtesseract-dev libleptonica-dev
      ```
    - Windows: [Download installer](https://github.com/UB-Mannheim/tesseract/wiki)

---

## 🚀 Setup & Run

1. Clone or unzip the project.
2. Ensure `tesseract` is installed and available in your PATH:
   ```bash
   tesseract --version
   ````

3. Set JVM properties (VM options in IntelliJ or `mvn test`):

   ```bash
   -Djna.library.path=/opt/homebrew/lib
   -DTESSDATA_PREFIX=/opt/homebrew/share/
   ```

   *(adjust paths for your OS installation of Tesseract)*

4. Build the project:

   ```bash
   mvn clean install
   ```

5. Run tests with TestNG:

   ```bash
   mvn test -DsuiteXmlFile=src/test/resources/test-suites/testng.xml
   ```

---

## 📌 Example Flow

1. Selenium opens the browser and navigates to the login page.
2. A screenshot is captured.
3. **Tesseract OCR** detects words from the screenshot.
4. If `"Login"` is found:

   * Calculate center coordinates of the OCR bounding box.
   * Use JavaScript `document.elementFromPoint(x,y).click()` to click the button.
5. Assert login behavior in test cases.

---

## 🔍 Key Classes

* `AIElementClicker.java`
  Core utility that integrates Selenium screenshot capture, OCR, and coordinate mapping for AI-based clicks.

* `AbstractPage.java` / `LoginPage.java`
  Page Object Model (POM) for web page interaction.

* `LoginTest.java`
  TestNG test validating login functionality with AI-driven element detection.

---

## ✅ Improvements & Next Steps

* Enhance OCR accuracy with preprocessing (invert colors, upscale, threshold).
* Add fuzzy matching for synonyms (`Login` / `Sign in` / `Continue`).
* Integrate OpenCV for more robust preprocessing of button text.
* Extend to other UI components (links, labels, dropdowns).

---

## 📖 References

* [Tess4J Documentation](https://tess4j.sourceforge.net/)
* [Tesseract OCR GitHub](https://github.com/tesseract-ocr/tesseract)
* [Selenium WebDriver](https://www.selenium.dev/)

```