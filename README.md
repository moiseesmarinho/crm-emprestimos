CRM Empréstimos

Backend de um sistema de gestão de empréstimos, desenvolvido em Java com Spring Boot, focado no controle de clientes, contratos, parcelas e pagamentos.

O projeto simula um cenário real de crédito/financiamento, com regras de negócio, persistência relacional e APIs REST para integração com frontend ou sistemas externos.

🧠 Objetivo do projeto

Este projeto foi desenvolvido com foco em:

Prática real de Spring Boot + JPA/Hibernate

Modelagem de domínio (Clientes, Empréstimos, Parcelas)

Regras de negócio aplicadas no backend

Organização em camadas (Controller, Service, Repository)

Testes manuais de API via Postman

Uso de banco relacional (PostgreSQL)

🛠️ Tecnologias utilizadas

Java 21

Spring Boot

Spring Web

Spring Data JPA

Hibernate

PostgreSQL

Maven

Postman (testes de API)

Git & GitHub

📦 Estrutura do projeto
src/main/java/br/com/crm/crmemprestimos
│
├── controller
│ ├── ClienteController
│ ├── EmprestimoController
│ └── ParcelaController
│
├── service
│ ├── ClienteService
│ ├── EmprestimoService
│ └── ParcelaService
│
├── repository
│ ├── ClienteRepository
│ ├── EmprestimoRepository
│ └── ParcelaRepository
│
├── model
│ ├── Cliente
│ ├── Emprestimo
│ ├── Parcela
│ ├── StatusEmprestimo
│ └── ParcelaStatus
│
├── dto
│ └── (Requests e Responses da API)
│
├── exception
│ ├── ApiExceptionHandler
│ ├── BusinessRuleException
│ └── ResourceNotFoundException
│
└── CrmEmprestimosApplication

🗄️ Modelo de domínio (resumo)

Cliente

Nome

CPF/CNPJ

Telefone

Dados cadastrais

Empréstimo

Cliente associado

Valor principal

Taxa de juros mensal

Quantidade de parcelas

Status do empréstimo

Parcela

Empréstimo associado

Valor

Data de vencimento

Status (PAGA / PENDENTE)

🚀 Como executar o projeto
Pré-requisitos

Java 21

Maven

PostgreSQL em execução

1️⃣ Clonar o repositório
git clone https://github.com/moiseesmarinho/crm-emprestimos.git
cd crm-emprestimos

2️⃣ Configurar o banco

No application.properties (ou application.yml):

spring.datasource.url=jdbc:postgresql://localhost:5432/crm_emprestimos
spring.datasource.username=postgres
spring.datasource.password=senha
spring.jpa.hibernate.ddl-auto=update

3️⃣ Rodar a aplicação
mvn spring-boot:run

A API ficará disponível em:

http://localhost:8080

🔌 Endpoints principais
📌 Clientes

Criar cliente

POST /clientes

{
"nomeCompleto": "Cliente Teste",
"cpf": "12345678900",
"telefone": "48999999999"
}

📌 Empréstimos

Criar empréstimo

POST /emprestimos

{
"clienteId": 1,
"valorPrincipal": 1000,
"taxaJurosMensal": 5,
"quantidadeParcelas": 5,
"dataPrimeiroVencimento": "2025-01-10",
"observacoes": "Teste inicial"
}

Listar empréstimos

GET /emprestimos

📌 Parcelas

Listar parcelas de um empréstimo

GET /parcelas/emprestimos/{emprestimoId}

Pagar parcela

POST /parcelas/{parcelaId}/pagar

🧪 Testes

Todos os endpoints foram testados manualmente via Postman

Testes realizados após reset do banco (TRUNCATE TABLE ... CASCADE)

Validação de regras de negócio e integridade relacional

📌 Status do projeto

✔ Backend funcional
✔ Persistência com PostgreSQL
✔ Regras de negócio implementadas
✔ API testada manualmente

🔜 Próximos passos possíveis:

Autenticação (Spring Security)

Paginação e filtros

Testes automatizados

Frontend (Web ou Mobile)

👤 Autor

Moisés Marinho
Estudante de Análise e Desenvolvimento de Sistemas
Back-end Java | Spring Boot

🔗 GitHub: https://github.com/moiseesmarinho

🔗 LinkedIn: https://www.linkedin.com/in/moiseesmarinho/
