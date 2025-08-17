# NutriAI API 🍏

[![Status do Projeto](https://img.shields.io/badge/status-em--desenvolvimento-yellow)](https://shields.io/)
[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Firebase](https://img.shields.io/badge/Firebase-Authentication-orange.svg)](https://firebase.google.com/products/auth)

API backend para a plataforma NutriAI, projetada para atender nutricionistas no gerenciamento de seus pacientes. O projeto utiliza Spring Boot para a construção de uma API RESTful robusta e Firebase Authentication como provedor de identidade.

## 📝 Índice

- [Funcionalidades](#-funcionalidades)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pré-requisitos](#-pré-requisitos)
- [Configuração do Ambiente](#-configuração-do-ambiente)
- [Documentação da API (Endpoints)](#-documentação-da-api-endpoints)
- [Próximos Passos](#-próximos-passos)

## ✨ Funcionalidades

A API implementa um módulo de autenticação completo e seguro, além de endpoints de negócio protegidos.

- ✅ **Registro de Usuários**: Rota pública para criação de novas contas de nutricionistas no Firebase.
- ✅ **Login de Usuários**: Rota pública para autenticação com e-mail e senha, retornando um `idToken` (JWT) e um `refreshToken`.
- ✅ **Renovação de Token**: Rota pública para trocar um `refreshToken` válido por um novo `idToken`.
- ✅ **Validação de Token JWT**: Um filtro de segurança intercepta todas as requisições para validar o `idToken` do Firebase enviado no cabeçalho `Authorization`.
- ✅ **Tratamento de Erros Padronizado**: Respostas de erro claras e consistentes para a API (400, 401, 404, 409).
- ✅ **Validação de Dados**: Validação automática dos dados de entrada nos DTOs.

## 🚀 Tecnologias Utilizadas

- **Java 17**: Versão da linguagem Java.
- **Spring Boot 3.5.3**: Framework principal para construção da API.
- **Spring Web**: Para criar os endpoints RESTful.
- **Spring Security 6**: Para controle de autenticação e autorização.
- **Spring Validation**: Para validação dos dados de entrada.
- **Maven**: Gerenciador de dependências e build do projeto.
- **Firebase Admin SDK**: Para integração backend com o Firebase.
- **Firebase Authentication**: Provedor de identidade para gerenciamento de usuários.

## 📋 Pré-requisitos

Antes de começar, você precisará ter as seguintes ferramentas instaladas em sua máquina:

- JDK 17 ou superior
- Apache Maven 3.8+
- Git
- Uma IDE de sua preferência (IntelliJ, VS Code com extensões Java, Eclipse).
- Uma conta no Firebase.

## ⚙️ Configuração do Ambiente

Siga os passos abaixo para rodar o projeto localmente.

### 1. Clone o Repositório
```bash
git clone https://github.com/Nutri-AI-TCC/nutriai-api.git
cd nutriai-api
 ```


### 2. Configure o Firebase
Você precisará de duas chaves do seu projeto Firebase.

**a) Obtenha a Chave de Serviço (Service Account Key):**
1. Acesse o **Console do Firebase** e vá para **Configurações do projeto** (⚙️).
2. Clique na aba **Contas de serviço**.
3. Clique no botão **"Gerar nova chave privada"**.
4. Um arquivo JSON será baixado. Renomeie este arquivo para `private-key.json`.
5. Mova o arquivo `private-key.json` para a pasta `src/main/resources` do projeto.

> **Atenção:** O arquivo `private-key.json` contém credenciais de administrador. **Nunca** o envie para um repositório Git público. Adicione-o ao seu arquivo `.gitignore`.

**b) Obtenha a Chave da API Web (Web API Key):**
1. Ainda nas **Configurações do projeto**, vá para a aba **Geral**.
2. Em **"Seus apps"**, localize seu aplicativo da Web.
3. No objeto de configuração `firebaseConfig`, copie o valor da chave `apiKey`.
4. Abra o arquivo `src/main/resources/application.properties`.
5. Adicione a seguinte linha, substituindo `<SUA_CHAVE_API_AQUI>` pela chave que você copiou:
   ```properties
   com.nutriai.firebase.web-api-key=<SUA_CHAVE_API_AQUI>
   ```



### 3. Compile e Rode a Aplicação
```bash
# Compile o projeto e baixe as dependências
mvn clean install

# Rode a aplicação
mvn spring-boot:run
 ```


## 🕹️ Documentação da API (Endpoints)

A seguir estão detalhados os endpoints disponíveis na NutriAI API.

---

### 1. Registrar um Novo Usuário

Este endpoint cria uma nova conta de usuário (nutricionista) no sistema. Ele realiza duas operações:
1.  Cria a conta de autenticação no Firebase.
2.  Salva os dados de perfil do usuário no banco de dados da aplicação.

-   **Endpoint:** `/api/v1/auth/register`
-   **Método:** `POST`

#### Requisição

| Atributo      | Descrição                      |
| :------------ | :----------------------------- |
| **URL** | `/api/v1/auth/register`        |
| **Método** | `POST`                         |
| **Cabeçalhos**| `Content-Type: application/json` |

**Corpo da Requisição:**
```json
{
  "email": "nutricionista.novo@email.com",
  "nomeCompleto": "Ana Clara da Silva",
  "password": "senhaSuperForte123",
  "confirmarSenha": "senhaSuperForte123",
  "cpfCnpj": "123.456.789-00",
  "cep": "01311-000",
  "cidade": "São Paulo",
  "rua": "Avenida Paulista",
  "numero": "1578"
}
 ```

#### Respostas

-   **`201 Created`** - Se o usuário for criado com sucesso em ambos os sistemas (Firebase e banco local).
    ```
    Usuário registrado com sucesso!
    ```
-   **`400 Bad Request`** - Se os dados enviados forem inválidos. Isso pode ocorrer por vários motivos:
    -   Campo obrigatório em branco (`@NotBlank`).
    -   E-mail em formato incorreto (`@Email`).
    -   Senha e confirmação de senha não coincidem.

    *Exemplo (senhas divergentes):*
    ```json
    {
        "timestamp": "2025-07-27T12:35:00.123456",
        "status": 400,
        "error": "Dados Inválidos",
        "message": "As senhas não coincidem.",
        "path": "/api/v1/auth/register"
    }
    ```

-   **`409 Conflict`** - Se o e-mail fornecido já estiver em uso.
    ```json
    {
        "timestamp": "2025-07-27T12:36:10.123456",
        "status": 409,
        "error": "Conflito de Recurso",
        "message": "A conta com o e-mail fornecido já existe.",
        "path": "/api/v1/auth/register"
    }
    ```

---

### 2. Autenticar um Usuário (Login)

Este endpoint autentica um usuário com e-mail e senha e retorna tokens de acesso.

-   **Endpoint:** `/api/v1/auth/login`
-   **Método:** `POST`

#### Requisição

| Atributo      | Descrição                      |
| :------------ | :----------------------------- |
| **URL** | `/api/v1/auth/login`           |
| **Método** | `POST`                         |
| **Cabeçalhos**| `Content-Type: application/json` |

**Corpo da Requisição:**
```json
{
  "email": "nutricionista.novo@email.com",
  "password": "senhaForte123"
}
 ```

#### Respostas

-   **`200 OK`** - Se a autenticação for bem-sucedida. O `idToken` deve ser copiado para usar em rotas protegidas.
    ```json
    {
        "idToken": "eyJhbGciOiJSUzI1NiIsImtpZCI6...",
        "refreshToken": "AMf-vBw..."
    }
    ```
-   **`401 Unauthorized`** - Se as credenciais (e-mail ou senha) forem inválidas.
    ```json
    {
        "timestamp": "2025-07-26T11:23:50.123456",
        "status": 401,
        "error": "Não Autorizado",
        "message": "Credenciais de login inválidas.",
        "path": "/api/v1/auth/login"
    }
    ```

---

### 3. Renovar Token de Acesso

Este endpoint troca um `refreshToken` válido por um novo `idToken`.

-   **Endpoint:** `/api/v1/auth/refresh-token`
-   **Método:** `POST`

#### Requisição

| Atributo      | Descrição                      |
| :------------ | :----------------------------- |
| **URL** | `/api/v1/auth/refresh-token`   |
| **Método** | `POST`                         |
| **Cabeçalhos**| `Content-Type: application/json` |

**Corpo da Requisição:**
```json
{
  "refreshToken": "AMf-vBw...o-token-recebido-no-login"
}
 ```

#### Respostas

-   **`200 OK`** - Se o token for renovado com sucesso.
    ```json
    {
        "id_token": "eyJhbGciOi... (um novo idToken)"
    }
    ```
-   **`401 Unauthorized`** - Se o `refreshToken` for inválido ou expirado.
    ```json
    {
        "timestamp": "2025-07-26T11:23:50.123456",
        "status": 401,
        "error": "Não Autorizado",
        "message": "Refresh token inválido ou expirado.",
        "path": "/api/v1/auth/refresh-token"
    }
    ```

---

### 4. Buscar Todas as Dietas (Rota Protegida)

Este endpoint de exemplo retorna uma lista de dietas e exige autenticação.

-   **Endpoint:** `/api/v1/dietas`
-   **Método:** `GET`

#### Requisição

| Atributo      | Descrição                                         |
| :------------ | :------------------------------------------------ |
| **URL** | `/api/v1/dietas`                                  |
| **Método** | `GET`                                             |
| **Cabeçalhos**| `Authorization: Bearer <seu_idToken_obtido_no_login>` |

**Corpo da Requisição:**
- Nenhum

#### Respostas

-   **`200 OK`** - Se o `idToken` for válido.
    ```json
    [
        {
            "id": 1,
            "nome": "Dieta de Baixo Carboidrato",
            "ativo": true
        },
        {
            "id": 2,
            "nome": "Dieta Mediterrânea",
            "ativo": true
        }
    ]
    ```
-   **`401 Unauthorized`** - Se o cabeçalho `Authorization` estiver ausente, ou se o `idToken` for inválido ou expirado.
    ```json
    {
        "title": "User Unauthorized",
        "status": 401,
        "detail": "A autenticação falhou: o token está ausente, é inválido ou expirado."
    }
    ```

---

### 5. Buscar Dados do Usuário Logado (Rota Protegida)

Este endpoint retorna os dados de perfil completos do usuário que está autenticado, com base no `idToken` enviado.

-   **Endpoint:** `/api/v1/users/me`
-   **Método:** `GET`

#### Requisição

| Atributo      | Descrição                                         |
| :------------ | :------------------------------------------------ |
| **URL** | `/api/v1/users/me`                                |
| **Método** | `GET`                                             |
| **Cabeçalhos**| `Authorization: Bearer <seu_idToken_obtido_no_login>` |

**Corpo da Requisição:**
- Nenhum

#### Respostas

-   **`200 OK`** - Se o `idToken` for válido e o usuário for encontrado no banco de dados.
    ```json
    {
        "uid": "C4cMTOgxEHbRyRyx2SjbjChvqAm2",
        "nome": "Ana Clara da Silva",
        "email": "nutricionista.novo@email.com",
        "cnpjCpf": "123.456.789-00",
        "cep": "01311-000",
        "cidade": "São Paulo",
        "rua": "Avenida Paulista",
        "numero": "1578"
    }
    ```
-   **`401 Unauthorized`** - Se o cabeçalho `Authorization` estiver ausente, ou se o `idToken` for inválido ou expirado.
    ```json
    {
        "timestamp": "2025-07-30T22:40:10.123456",
        "status": 401,
        "error": "Não Autorizado",
        "message": "A autenticação falhou: o token está ausente, é inválido ou expirado.",
        "path": "/api/v1/users/me"
    }
    ```
-   **`404 Not Found`** - Se o `idToken` for válido, mas o usuário correspondente não for encontrado no banco de dados local.
    ```json
    {
        "timestamp": "2025-07-30T22:42:15.567890",
        "status": 404,
        "error": "Recurso Não Encontrado",
        "message": "Usuário não encontrado com o ID: C4cMTOgxEHbRyRyx2SjbjChvqAm2",
        "path": "/api/v1/users/me"
    }
    ```
    
---

### 6. Atualizar Perfil do Usuário Logado (Rota Protegida)

Este endpoint permite que o usuário autenticado atualize seus próprios dados de perfil (nome, endereço, etc.).

-   **Endpoint:** `/api/v1/users/me`
-   **Método:** `PUT`

#### Requisição

| Atributo      | Descrição                                         |
| :------------ | :------------------------------------------------ |
| **URL** | `/api/v1/users/me`                                |
| **Método** | `PUT`                                             |
| **Cabeçalhos**| `Content-Type: application/json`<br>`Authorization: Bearer <seu_idToken_obtido_no_login>` |

**Corpo da Requisição:**

```json
{
  "nomeCompleto": "Ana Clara da Silva Santos",
  "cnpjCpf": "111.222.333-44",
  "cep": "01310-200",
  "cidade": "São Paulo",
  "rua": "Avenida Brigadeiro Luís Antônio",
  "numero": "2344"
}

 ```

#### Respostas

-   **`200 OK`** - Se os dados forem válidos e o perfil for atualizado com sucesso.
    ```json
    {
        "uid": "C4cMTOgxEHbRyRyx2SjbjChvqAm2",
        "nome": "Ana Clara da Silva Santos",
        "email": "nutricionista.novo@email.com",
        "cnpjCpf": "111.222.333-44",
        "cep": "01310-200",
        "cidade": "São Paulo",
        "rua": "Avenida Brigadeiro Luís Antônio",
        "numero": "2344"
    }
    ```
-   **`400 Bad Request`** - Se algum campo obrigatório no corpo da requisição estiver em branco.
    ```json
    {
        "timestamp": "2025-07-30T21:15:00.123456",
        "status": 400,
        "error": "Dados Inválidos",
        "message": "O nome completo não pode estar em branco",
        "path": "/api/v1/users/me"
    }
    ```
-   **`401 Unauthorized`** - Se o `idToken` estiver ausente, for inválido ou expirado.
    ```json
    {
        "timestamp": "2025-07-30T21:16:10.567890",
        "status": 401,
        "error": "Não Autorizado",
        "message": "A autenticação falhou: o token está ausente, é inválido ou expirado.",
        "path": "/api/v1/users/me"
    }
    ```
-   **`404 Not Found`** - Se o usuário autenticado não for encontrado no banco de dados local.
    ```json
    {
        "timestamp": "2025-07-30T21:17:15.987654",
        "status": 404,
        "error": "Recurso Não Encontrado",
        "message": "Usuário não encontrado com o ID: C4cMTOgxEHbRyRyx2SjbjChvqAm2",
        "path": "/api/v1/users/me"
    }
    ```

---

### 7. Deletar a Própria Conta (Rota Protegida)

Este endpoint permite que o usuário autenticado exclua permanentemente sua própria conta e todos os dados associados (perfil, pacientes, dietas, etc.), tanto do banco de dados da aplicação quanto do Firebase Authentication. **Esta é uma operação irreversível.**

-   **Endpoint:** `/api/v1/users/me`
-   **Método:** `DELETE`

#### Requisição

| Atributo      | Descrição                                         |
| :------------ | :------------------------------------------------ |
| **URL** | `/api/v1/users/me`                                |
| **Método** | `DELETE`                                          |
| **Cabeçalhos**| `Authorization: Bearer <seu_idToken_obtido_no_login>` |

**Corpo da Requisição:**
- Nenhum

#### Respostas

-   **`204 No Content`** - Se a conta e todos os dados associados forem excluídos com sucesso.
    *O corpo da resposta estará vazio.*

-   **`401 Unauthorized`** - Se o `idToken` estiver ausente, for inválido ou expirado.
    ```json
    {
        "timestamp": "2025-08-03T15:20:10.123456",
        "status": 401,
        "error": "Não Autorizado",
        "message": "A autenticação falhou: o token está ausente, é inválido ou expirado.",
        "path": "/api/v1/users/me"
    }
    ```
-   **`404 Not Found`** - Se o usuário autenticado não for encontrado no banco de dados local para exclusão.
    ```json
    {
        "timestamp": "2025-08-03T15:21:15.567890",
        "status": 404,
        "error": "Recurso Não Encontrado",
        "message": "Usuário não encontrado com o ID: C4cMTOgxEHbRyRyx2SjbjChvqAm2",
        "path": "/api/v1/users/me"
    }
    ```


---

## Endpoints de Pacientes

Endpoints para o gerenciamento completo dos pacientes de um nutricionista. Todas as rotas nesta seção são protegidas e exigem autenticação.

### 8. Criar um Novo Paciente

Este endpoint permite que um nutricionista autenticado registre um novo paciente em sua carteira.

-   **Endpoint:** `/api/v1/pacientes`
-   **Método:** `POST`

#### Requisição

| Atributo      | Descrição                      |
| :------------ | :----------------------------- |
| **URL** | `/api/v1/pacientes`            |
| **Método** | `POST`                         |
| **Cabeçalhos**| `Content-Type: application/json`<br>`Authorization: Bearer <seu_idToken_obtido_no_login>` |

**Corpo da Requisição:**
```json
{
  "nome": "Carlos Andrade",
  "nascimento": "1985-05-20",
  "peso": 85.5,
  "altura": 1.78,
  "cnpjCpf": "111.222.333-44",
  "alergias": "Alergia a amendoim e camarão.",
  "comorbidades": "Hipertensão arterial leve.",
  "medicacoes": "Losartana 50mg, uma vez ao dia.",
  "ativo": true
}
 ```

#### Respostas

-   **`201 Created`** - Se o paciente for criado e associado ao nutricionista com sucesso.
    ```json
    {
        "id": 1,
        "nome": "Carlos Andrade",
        "nascimento": "1985-05-20",
        "peso": 85.50,
        "altura": 1.78,
        "ativo": true,
        "usuario": {
            "uid": "UID_DO_NUTRICIONISTA_LOGADO",
            "nome": "Nome do Nutricionista",
            "email": "email@nutricionista.com"
        }
    }
    ```
-   **`400 Bad Request`** - Se os dados enviados na requisição forem inválidos.
-   **`401 Unauthorized`** - Se o `idToken` for inválido ou ausente.

---

### 9. Listar Pacientes do Nutricionista

Este endpoint retorna uma lista de todos os pacientes associados ao nutricionista autenticado.

-   **Endpoint:** `/api/v1/pacientes`
-   **Método:** `GET`

#### Requisição

| Atributo      | Descrição                                         |
| :------------ | :------------------------------------------------ |
| **URL** | `/api/v1/pacientes`                               |
| **Método** | `GET`                                             |
| **Cabeçalhos**| `Authorization: Bearer <seu_idToken_obtido_no_login>` |

**Corpo da Requisição:**
- Nenhum

#### Respostas

-   **`200 OK`** - Retorna uma lista com os DTOs dos pacientes.
    ```json
    [
        {
            "id": 1,
            "nome": "Carlos Andrade",
            "nascimento": "1985-05-20",
            "peso": 85.50,
            "altura": 1.78,
            "ativo": true,
            "usuario": {
                "uid": "UID_DO_NUTRICIONISTA_LOGADO",
                "nome": "Nome do Nutricionista",
                "email": "email@nutricionista.com"
            }
        },
        {
            "id": 2,
            "nome": "Juliana Ribeiro",
            "nascimento": "1992-11-10",
            "peso": 62.00,
            "altura": 1.65,
            "ativo": true,
            "usuario": {
                "uid": "UID_DO_NUTRICIONISTA_LOGADO",
                "nome": "Nome do Nutricionista",
                "email": "email@nutricionista.com"
            }
        }
    ]
    ```
-   **`200 OK`** - Se o nutricionista não tiver pacientes, retorna uma lista vazia.
    ```json
    []
    ```
-   **`401 Unauthorized`** - Se o `idToken` for inválido ou ausente.

---

### 10. Buscar Paciente Específico por ID (Rota Protegida)

Este endpoint retorna os dados detalhados de um paciente específico, desde que ele pertença ao nutricionista autenticado.

-   **Endpoint:** `/api/v1/pacientes/{id}`
-   **Método:** `GET`

#### Requisição

| Atributo | Descrição |
| :--- | :--- |
| **URL** | `/api/v1/pacientes/{id}` |
| **Parâmetros de URL** | `id` (obrigatório): O ID numérico do paciente que você deseja buscar. |
| **Método** | `GET` |
| **Cabeçalhos**| `Authorization: Bearer <seu_idToken_obtido_no_login>` |

**Corpo da Requisição:**
- Nenhum

#### Respostas

-   **`200 OK`** - Se o paciente for encontrado e pertencer ao usuário.
    ```json
    {
        "id": 82,
        "nome": "Carlos Andrade",
        "nascimento": "1985-05-20",
        "peso": 85.50,
        "altura": 1.78,
        "ativo": true,
        "usuario": {
            "uid": "UID_DO_NUTRICIONISTA_LOGADO",
            "nome": "Nome do Nutricionista",
            "email": "email@nutricionista.com"
        }
    }
    ```
-   **`401 Unauthorized`** - Se o `idToken` estiver ausente, for inválido ou expirado.
    ```json
    {
        "timestamp": "2025-08-17T18:40:00.123456",
        "status": 401,
        "error": "Não Autorizado",
        "message": "A autenticação falhou: o token está ausente, é inválido ou expirado.",
        "path": "/api/v1/pacientes/82"
    }
    ```
-   **`403 Forbidden`** - Se o paciente existir, mas não pertencer ao usuário autenticado.
    ```json
    {
        "timestamp": "2025-08-17T18:41:00.123456",
        "status": 403,
        "error": "Acesso Negado",
        "message": "Você não tem permissão para acessar este paciente.",
        "path": "/api/v1/pacientes/82"
    }
    ```
-   **`404 Not Found`** - Se nenhum paciente for encontrado com o ID fornecido.
    ```json
    {
        "timestamp": "2025-08-17T18:42:00.123456",
        "status": 404,
        "error": "Recurso Não Encontrado",
        "message": "Paciente não encontrado com o ID: 999",
        "path": "/api/v1/pacientes/999"
    }
    ```

---


## ⏭️ Próximos Passos
[ ] Implementar a lógica de negócio no DietaService.

[ ] Desenvolver os endpoints de CRUD para Pacientes, que também serão rotas protegidas.

[ ] Desenvolver os endpoints de CRUD para Planos Alimentares.
