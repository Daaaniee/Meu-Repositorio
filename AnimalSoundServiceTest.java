/**
 * Testes simples sem dependências externas para validar comportamento do serviço.
 *
 * Execução:
 *   javac AnimalSoundService.java AnimalSoundServiceTest.java
 *   java AnimalSoundServiceTest
 */
public class AnimalSoundServiceTest {

    public static void main(String[] args) {
        AnimalSoundService service = new AnimalSoundService();

        assertEquals("au au", service.getSoundByAnimal("cachorro"), "deve retornar som de cachorro");
        assertEquals("miau", service.getSoundByAnimal("gato"), "deve retornar som de gato");
        assertEquals("miau", service.getSoundByAnimal(" GATO "), "deve normalizar espaços e caixa");
        assertEquals("Informe gato ou cachorro", service.getSoundByAnimal("papagaio"), "deve rejeitar animal não suportado");
        assertEquals("Informe gato ou cachorro", service.getSoundByAnimal(""), "deve rejeitar string vazia");
        assertEquals("Informe gato ou cachorro", service.getSoundByAnimal(null), "deve rejeitar valor nulo");

        System.out.println("Todos os testes passaram com sucesso.");
    }

    private static void assertEquals(String expected, String actual, String context) {
        if (!expected.equals(actual)) {
            throw new AssertionError(
                    "Falha: " + context + " | esperado='" + expected + "' recebido='" + actual + "'");
        }
    }
}
