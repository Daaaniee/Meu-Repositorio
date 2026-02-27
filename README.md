# ChatSuite - Testes de Automação

Este projeto contém automações de API e Web para o fluxo de atualização de comunicados.

## Stack
- Java 17
- JUnit 5
- RestAssured + WireMock (API)
- Selenium WebDriver + WebDriverManager (Web)

## Execução
```bash
mvn test
```

## Testes Web
A suíte web está em:
- `src/test/java/com/chatsuite/web/AnnouncementUpdateWebTest.java`

Por padrão, ela tenta abrir:
- `http://localhost:3000/announcements/123/edit`

Você pode sobrescrever a URL base:
```bash
mvn -Dweb.baseUrl=http://localhost:4173 test
```

## Contrato esperado na tela (data-testid)
Para o teste funcionar, a tela deve expor os elementos:
- `announcementForm`
- `type`
- `type-option-GENERAL`
- `title`
- `message`
- `startDate`
- `endDate`
- `status`
- `status-option-ACTIVE`
- `saveButton`
- `successToast`
- `periodError`

