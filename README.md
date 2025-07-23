# NutriAI API 🍏

[![Status do Projeto](https'img.shields.io/badge/status-em--desenvolvimento-yellow')](https://shields.io/)
[![Java](https'img.shields.io/badge/Java-17-blue.svg')](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html)
[![Spring Boot](https'img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg')](https://spring.io/projects/spring-boot)
[![Firebase](https'img.shields.io/badge/Firebase-Authentication-orange.svg')](https://firebase.google.com/products/auth)

API backend para a plataforma NutriAI, projetada para atender nutricionistas no gerenciamento de seus pacientes. O projeto utiliza Spring Boot para a construção de uma API RESTful robusta e Firebase Authentication como provedor de identidade.

## 📝 Índice

- [Funcionalidades](#-funcionalidades)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Pré-requisitos](#-pré-requisitos)
- [Configuração do Ambiente](#-configuração-do-ambiente)
- [Uso da API](#-uso-da-api)
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
- **Spring Boot 3.x**: Framework principal para construção da API.
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
bash
`git clone <url-do-seu-repositorio>
cd nutriai-api`

2. Configure o Firebase
Você precisará de duas chaves do seu projeto Firebase.

a) Obtenha a Chave de Serviço (Service Account Key):

Acesse o Console do Firebase e vá para Configurações do projeto (⚙️).

Clique na aba Contas de serviço.

Clique no botão "Gerar nova chave privada".

Um arquivo JSON será baixado. Renomeie este arquivo para private-key.json.

Mova o arquivo private-key.json para a pasta src/main/resources do projeto.

b) Obtenha a Chave da API Web (Web API Key):

Ainda nas Configurações do projeto, vá para a aba Geral.

Em "Seus apps", localize seu aplicativo da Web.

No objeto de configuração firebaseConfig, copie o valor da chave apiKey.

Abra o arquivo src/main/resources/application.properties.

Adicione a seguinte linha, substituindo <SUA_CHAVE_API_AQUI> pela chave que você copiou:

Properties

com.nutriai.firebase.web-api-key=<SUA_CHAVE_API_AQUI>
3. Compile e Rode a Aplicação
Bash

# Compile o projeto e baixe as dependências
mvn clean install

# Rode a aplicação
mvn spring-boot:run
A API estará rodando em http://localhost:8080.

🕹️ Uso da API
Recomenda-se o uso do Postman para testar os endpoints.

Endpoints Públicos (Autenticação)
1. Registrar um Novo Usuário
Método: POST

URL: http://localhost:8080/api/v1/auth/register

Body (raw, JSON):

JSON

{
  "email": "nutricionista.novo@email.com",
  "password": "senhaForte123"
}
Resposta de Sucesso (201 Created): Usuário registrado com sucesso!

2. Fazer Login
Método: POST

URL: http://localhost:8080/api/v1/auth/login

Body (raw, JSON):

JSON

{
  "email": "nutricionista.novo@email.com",
  "password": "senhaForte123"
}
Resposta de Sucesso (200 OK): Copie o idToken para usar nos endpoints protegidos.

JSON

{
    "idToken": "eyJhbGciOi...",
    "refreshToken": "AMf-vBw..."
}
Endpoints Protegidos (Exemplo)
Para acessar qualquer endpoint que não seja /auth/**, você precisa se autenticar.

1. Buscar Todas as Dietas
Método: GET

URL: http://localhost:8080/api/v1/dietas

Autenticação:

No Postman, vá para a aba Authorization.

Selecione o tipo Bearer Token.

No campo Token, cole o idToken que você recebeu do endpoint de login.

Resposta de Sucesso (200 OK): Uma lista de dietas (ex: [] se estiver vazia).

Resposta de Falha (sem token): 401 Unauthorized.

⏭️ Próximos Passos
[ ] Implementar a lógica de negócio no DietaService.

[ ] Desenvolver os endpoints de CRUD para Pacientes, que também serão rotas protegidas.

[ ] Desenvolver os endpoints de CRUD para Planos Alimentares.
