package modulos.produto;

import dataFactory.ProdutoDataFactory;
import dataFactory.UsuarioDataFactory;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Teste de API Rest do módulo Produto")
public class ProdutoTest {


     private String token;
     @BeforeEach
     public void beforeEach() {

         // Configurando os dados da API REST da Lojinha
         baseURI = "http://165.227.93.41";
         basePath = "/lojinha";

         // Obter token do usuário admin
         this.token = given()
                 .contentType(ContentType.JSON)
                 .body(UsuarioDataFactory.CriarUsuarioAdministrador())
        .when()
                 .post("/v2/login")
        .then()
                 .extract()
                 .path("data.token");
     }
    @Test
    @DisplayName("Validar que o valor do produto 0.00 nao e permitido")
    public void TestValidarLimitesZeradosProibidosdoValorProduto() {

        // Tentar inserir um produto com valor 0.00 e validar a mensagem de erro e status code 422

        given()
                .contentType(ContentType.JSON)
                .header("token",this.token)
                .body(ProdutoDataFactory.criarProdutoComumComOValorIgualA(0.00))

            .when()
                .post("/v2/produtos")
            .then()
                .assertThat()
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .statusCode(422);
    }
    @Test
    @DisplayName("Validar que o valor do produto 7000.01 nao e permitido")
    public void TestValidarLimitesMaiorSeteMilProibidosdoValorProduto() {

        // Tentar inserir um produto com valor 7000.01 e validar a mensagem de erro e status code 422
        given()
                .contentType(ContentType.JSON)
                .header("token",this.token)
                .body(ProdutoDataFactory.criarProdutoComumComOValorIgualA(7000.01))
            .when()
                .post("/v2/produtos")
            .then()
                .assertThat()
                .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                .statusCode(422);
    }


}




























