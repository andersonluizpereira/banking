# Banking System

## Descrição

API RESTful para gerenciamento de clientes e transferências bancárias.

## Tecnologias Utilizadas

- Java 21
- Spring Boot
- Spring Data JPA
- H2 Database (in-memory)
- Maven
- Lombok
- Springdoc OpenAPI
- JUnit & Mockito
- GitHub Action Hook de testes

## Requisitos

- Java 21
- Maven

## Como Executar

1. **Clonar o Repositório**

   ```bash
   git clone https://github.com/seu-usuario/banking-system.git
   cd banking-system
   ```
2. **Documentação do Projeto**
  Entre no navegador -> http://localhost:8080/swagger-ui/index.html#/

3. **Instalando as dependencias**
  ```bash
  mvn clean install
   ```
4. **Rodando os testes**
  ```bash
  mvn test
   ```
5. **Rodando o projeto**
  ```bash
  mvn spring-boot:run
   ```

6. **Tarefa**

### Desenvolva um projeto que exponha APIs no padrão RESTful e atenda às seguintes funcionalidades:
#### ✅ Cadastro de Clientes - o Um endpoint para cadastrar um cliente com as seguintes informações: ID (único), nome, número da conta (único) e saldo em conta.
#### ✅ Listagem de Clientes - o Um endpoint para listar todos os clientes cadastrados.
#### ✅ Busca de Cliente por Número da Conta - o Um endpoint para buscar um cliente pelo número da conta.
#### ✅ Transferência entre Contas - o Um endpoint para realizar transferências entre duas contas. A conta de origem precisa ter saldo suficiente para a realização da transferência, e o valor da transferência deve ser de no máximo R$ 10.000,00.
#### ✅ Histórico de Transferências - o endpoint para buscar as transferências relacionadas a uma conta, ordenadas por data decrescente. Lembre-se de que transferências sem sucesso também devem ser armazenadas.