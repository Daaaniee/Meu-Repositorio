package com.chatsuite.announcements;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

/**
 * Suíte de automação de atualização de comunicado (PATCH /v1/announcements/{id})
 * usando RestAssured + WireMock para simulação de API.
 */
class AnnouncementUpdateChatSuiteTest {

    private static final String ADMIN_TOKEN = "Bearer valid-admin-token";
    private static final String TENANT_A = "tenant-a";
    private static final String TENANT_B = "tenant-b";
    private static final String ANNOUNCEMENT_ID = "123";
    private static WireMockServer wireMockServer;
    private static String baseUrl;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(0);
        wireMockServer.start();
        configureFor("localhost", wireMockServer.port());
        baseUrl = "http://localhost:" + wireMockServer.port();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    @DisplayName("Scenario 01 - Atualizar comunicado preenchendo todos os campos válidos")
    void shouldUpdateAllValidFields() {
        stubFor(patch(urlEqualTo("/v1/announcements/" + ANNOUNCEMENT_ID))
                .withHeader("Authorization", equalTo(ADMIN_TOKEN))
                .withHeader("If-Match", equalTo("v3"))
                .willReturn(okJson("""
                        {
                          "id":"123",
                          "type":"GENERAL",
                          "title":"Novo título",
                          "message":"Nova mensagem",
                          "startDate":"%s",
                          "endDate":"%s",
                          "status":"ACTIVE"
                        }
                        """.formatted(LocalDate.now().plusDays(1), LocalDate.now().plusDays(10)))));

        givenBase("v3", TENANT_A)
                .body(Map.of(
                        "type", "GENERAL",
                        "title", "Novo título",
                        "message", "Nova mensagem",
                        "startDate", LocalDate.now().plusDays(1).toString(),
                        "endDate", LocalDate.now().plusDays(10).toString(),
                        "status", "ACTIVE"
                ))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(200)
                .body("title", equalTo("Novo título"))
                .body("status", equalTo("ACTIVE"));
    }

    @Test
    @DisplayName("Scenario 02 - Alterar apenas o título")
    void shouldUpdateOnlyTitle() {
        stubSuccessPatch("v3", "{" +
                "\"id\":\"123\",\"title\":\"Título alterado\",\"message\":\"Mensagem antiga\"}");

        givenBase("v3", TENANT_A)
                .body(Map.of("title", "Título alterado"))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(200)
                .body("title", equalTo("Título alterado"))
                .body("message", equalTo("Mensagem antiga"));
    }

    @Test
    @DisplayName("Scenario 03 - Modificar somente o período de exibição")
    void shouldUpdateOnlyPeriod() {
        stubSuccessPatch("v3", "{" +
                "\"id\":\"123\",\"startDate\":\"2030-01-01\",\"endDate\":\"2030-01-20\"}");

        givenBase("v3", TENANT_A)
                .body(Map.of("startDate", "2030-01-01", "endDate", "2030-01-20"))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(200)
                .body("startDate", equalTo("2030-01-01"))
                .body("endDate", equalTo("2030-01-20"));
    }

    @Test
    @DisplayName("Scenario 04 - Atualizar tipo com opção válida")
    void shouldUpdateTypeWithValidCatalogOption() {
        stubSuccessPatch("v3", "{" +
                "\"id\":\"123\",\"type\":\"SECURITY\"}");

        givenBase("v3", TENANT_A)
                .body(Map.of("type", "SECURITY"))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(200)
                .body("type", equalTo("SECURITY"));
    }

    @Test
    @DisplayName("Scenario 05 - Editar conteúdo da mensagem")
    void shouldUpdateOnlyMessage() {
        stubSuccessPatch("v3", "{" +
                "\"id\":\"123\",\"message\":\"Mensagem atualizada\"}");

        givenBase("v3", TENANT_A)
                .body(Map.of("message", "Mensagem atualizada"))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(200)
                .body("message", equalTo("Mensagem atualizada"));
    }

    @Test
    @DisplayName("Scenario 06 - Alterar status para inativo")
    void shouldUpdateStatusToInactive() {
        stubSuccessPatch("v3", "{" +
                "\"id\":\"123\",\"status\":\"INACTIVE\"}");

        givenBase("v3", TENANT_A)
                .body(Map.of("status", "INACTIVE"))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(200)
                .body("status", equalTo("INACTIVE"));
    }

    @Test
    @DisplayName("Scenario 07 - EndDate menor que StartDate")
    void shouldRejectInvalidDateRange() {
        stubValidationError(400, "Período informado é inválido");

        givenBase("v3", TENANT_A)
                .body(Map.of("startDate", "2030-01-20", "endDate", "2030-01-01"))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(400)
                .body("message", equalTo("Período informado é inválido"));
    }

    @Test
    @DisplayName("Scenario 08 - StartDate no passado")
    void shouldRejectPastStartDate() {
        stubValidationError(400, "O período não pode iniciar no passado");

        givenBase("v3", TENANT_A)
                .body(Map.of("startDate", LocalDate.now().minusDays(1).toString()))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(400)
                .body("message", equalTo("O período não pode iniciar no passado"));
    }

    @Test
    @DisplayName("Scenario 09 - Type inexistente")
    void shouldRejectUnknownType() {
        stubValidationError(400, "Tipo de comunicado inválido");

        givenBase("v3", TENANT_A)
                .body(Map.of("type", "NOT_EXISTS"))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(400)
                .body("message", equalTo("Tipo de comunicado inválido"));
    }

    @Test
    @DisplayName("Scenario 10 - Título obrigatório")
    void shouldRequireTitle() {
        stubValidationError(400, "O título é obrigatório");

        givenBase("v3", TENANT_A)
                .body(Map.of("title", ""))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(400)
                .body("message", equalTo("O título é obrigatório"));
    }

    @Test
    @DisplayName("Scenario 11 - Mensagem obrigatória")
    void shouldRequireMessage() {
        stubValidationError(400, "A mensagem é obrigatória");

        givenBase("v3", TENANT_A)
                .body(Map.of("message", ""))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(400)
                .body("message", equalTo("A mensagem é obrigatória"));
    }

    @Test
    @DisplayName("Scenario 12 - Formato de data inválido")
    void shouldRejectInvalidDateFormat() {
        stubValidationError(400, "Formato de data inválido");

        givenBase("v3", TENANT_A)
                .body(Map.of("startDate", "31-12-2030", "endDate", "2030/12/31"))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(400)
                .body("message", equalTo("Formato de data inválido"));
    }

    @Test
    @DisplayName("Scenario 13 - Rejeitar atualização sem autenticação")
    void shouldRejectUnauthenticatedRequest() {
        stubFor(patch(urlEqualTo("/v1/announcements/" + ANNOUNCEMENT_ID))
                .atPriority(1)
                .withoutHeader("Authorization")
                .willReturn(unauthorized()));

        given()
                .baseUri(baseUrl)
                .contentType("application/json")
                .body(Map.of("title", "Qualquer"))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(401);
    }

    @Test
    @DisplayName("Scenario 14 - Restrição entre tenants")
    void shouldForbidCrossTenantUpdate() {
        stubFor(patch(urlEqualTo("/v1/announcements/" + ANNOUNCEMENT_ID))
                .withHeader("Authorization", equalTo(ADMIN_TOKEN))
                .withHeader("X-Tenant-Id", equalTo(TENANT_B))
                .willReturn(forbidden()));

        givenBase("v3", TENANT_B)
                .body(Map.of("title", "Tentativa sem permissão"))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(403);
    }

    @Test
    @DisplayName("Scenario 15 - Comunicado inexistente")
    void shouldReturnNotFoundForUnknownId() {
        stubFor(patch(urlEqualTo("/v1/announcements/999999"))
                .withHeader("Authorization", equalTo(ADMIN_TOKEN))
                .willReturn(notFound().withBody("{\"message\":\"Comunicado não encontrado\"}")
                        .withHeader("Content-Type", "application/json")));

        givenBase("v3", TENANT_A)
                .body(Map.of("title", "Novo título"))
                .when()
                .patch("/v1/announcements/{id}", "999999")
                .then()
                .statusCode(404)
                .body("message", equalTo("Comunicado não encontrado"));
    }

    @Test
    @DisplayName("Scenario 16 - Detectar conflito de versão (If-Match desatualizado)")
    void shouldReturnPreconditionFailedForStaleVersion() {
        stubFor(patch(urlEqualTo("/v1/announcements/" + ANNOUNCEMENT_ID))
                .withHeader("Authorization", equalTo(ADMIN_TOKEN))
                .withHeader("If-Match", equalTo("v1"))
                .willReturn(status(412)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"Divergência de versão do recurso\"}")));

        givenBase("v1", TENANT_A)
                .body(Map.of("title", "Título com versão antiga"))
                .when()
                .patch("/v1/announcements/{id}", ANNOUNCEMENT_ID)
                .then()
                .statusCode(412)
                .body("message", equalTo("Divergência de versão do recurso"));
    }

    private io.restassured.specification.RequestSpecification givenBase(String ifMatch, String tenantId) {
        return given()
                .baseUri(baseUrl)
                .header("Authorization", ADMIN_TOKEN)
                .header("If-Match", ifMatch)
                .header("X-Tenant-Id", tenantId)
                .contentType("application/json");
    }

    private void stubSuccessPatch(String ifMatch, String responseJson) {
        stubFor(patch(urlEqualTo("/v1/announcements/" + ANNOUNCEMENT_ID))
                .withHeader("Authorization", equalTo(ADMIN_TOKEN))
                .withHeader("If-Match", equalTo(ifMatch))
                .willReturn(okJson(responseJson)));
    }

    private void stubValidationError(int statusCode, String message) {
        stubFor(patch(urlEqualTo("/v1/announcements/" + ANNOUNCEMENT_ID))
                .withHeader("Authorization", equalTo(ADMIN_TOKEN))
                .willReturn(status(statusCode)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"message\":\"" + message + "\"}")));
    }
}
