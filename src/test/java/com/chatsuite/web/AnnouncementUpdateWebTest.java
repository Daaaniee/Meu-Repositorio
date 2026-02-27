package com.chatsuite.web;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testes web (E2E) para atualização de comunicados na ChatSuite.
 *
 * Pré-requisitos:
 * - Aplicação web rodando localmente (padrão: http://localhost:3000)
 * - Página de edição com data-testid para os elementos utilizados
 *
 * Pode customizar a URL base via -Dweb.baseUrl=http://host:porta
 */
@TestMethodOrder(MethodOrderer.DisplayName.class)
class AnnouncementUpdateWebTest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    private static final String BASE_URL = System.getProperty("web.baseUrl", "http://localhost:3000");
    private static final String EDIT_PATH = "/announcements/123/edit";

    @BeforeAll
    static void setupBrowser() {
        WebDriverManager.chromedriver().setup();

        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--disable-gpu", "--window-size=1920,1080", "--no-sandbox");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(12));
    }

    @AfterAll
    static void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    @DisplayName("01 - Deve atualizar comunicado com dados válidos")
    void shouldUpdateAnnouncementWithValidData() {
        openEditPage();

        selectByValue("type", "GENERAL");
        type("title", "Comunicado atualizado via teste web");
        type("message", "Mensagem atualizada por automação Selenium");
        type("startDate", "2030-10-01");
        type("endDate", "2030-10-31");
        selectByValue("status", "ACTIVE");

        click("saveButton");

        WebElement toast = waitFor("successToast");
        assertTrue(toast.getText().toLowerCase().contains("atualizado"));
    }

    @Test
    @DisplayName("02 - Deve validar período inválido")
    void shouldShowValidationForInvalidDateRange() {
        openEditPage();

        type("startDate", "2030-10-31");
        type("endDate", "2030-10-01");

        click("saveButton");

        WebElement periodError = waitFor("periodError");
        assertTrue(periodError.getText().contains("Período informado é inválido"));
    }

    @Test
    @DisplayName("03 - Deve bloquear edição sem autenticação")
    void shouldRedirectUnauthenticatedUser() {
        driver.get(BASE_URL + EDIT_PATH + "?unauthorized=true");

        wait.until(ExpectedConditions.urlContains("/login"));
        assertTrue(driver.getCurrentUrl().contains("/login"));
        assertEquals("Login - ChatSuite", driver.getTitle());
    }

    private void openEditPage() {
        driver.get(BASE_URL + EDIT_PATH);
        waitFor("announcementForm");
    }

    private void type(String fieldTestId, String value) {
        WebElement field = waitFor(fieldTestId);
        field.clear();
        field.sendKeys(value);
    }

    private void click(String testId) {
        waitFor(testId).click();
    }

    private void selectByValue(String fieldTestId, String optionValue) {
        click(fieldTestId);
        waitFor(fieldTestId + "-option-" + optionValue).click();
    }

    private WebElement waitFor(String testId) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("[data-testid='" + testId + "']")));
    }
}
