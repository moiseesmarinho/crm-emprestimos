📌 CRM Empréstimos – Back-end

API REST desenvolvida em Java + Spring Boot para gerenciamento de clientes, empréstimos e parcelas, com foco em regras de negócio reais (pagamento, status, vencimentos e encerramento).

Projeto criado como estudo prático de back-end, seguindo boas práticas de arquitetura, organização de código e persistência de dados.

🛠️ Tecnologias utilizadas

Java 21

Spring Boot

Spring Data JPA

Hibernate

PostgreSQL

Maven

Postman (testes da API)

📂 Estrutura do projeto
src/main/java/br/com/crm/crmemprestimos
├── controller # Camada REST (endpoints)
├── service # Regras de negócio
├── repository # Acesso a dados (JPA)
├── model # Entidades do domínio
├── dto # DTOs de request/response
├── exception # Tratamento de erros
└── CrmEmprestimosApplication.java

🧠 Principais funcionalidades
👤 Clientes

Criar cliente

Listar clientes

💰 Empréstimos

Criar empréstimo vinculado a um cliente

Geração automática das parcelas

Listar empréstimos

Encerrar empréstimo

📆 Parcelas

Listar parcelas por empréstimo

Registrar pagamento de parcela

Atualizar status automaticamente (PENDENTE / PAGA / ATRASADA)

🚀 Endpoints da API
🔹 Clientes

Criar cliente

POST /clientes

{
"nomeCompleto": "Cliente Teste",
"cpf": "12345678900",
"telefone": "48999999999"
}

🔹 Empréstimos

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

🔹 Parcelas

Listar parcelas de um empréstimo

GET /parcelas/emprestimos/{emprestimoId}

Pagar parcela

POST /parcelas/{parcelaId}/pagar

🧪 Testes

Todos os endpoints foram testados manualmente utilizando Postman, validando:

Criação correta de registros

Relacionamentos entre entidades

Regras de negócio

Persistência no banco de dados

Retornos HTTP adequados

🗄️ Banco de dados

PostgreSQL

Mapeamento feito com JPA/Hibernate

Relacionamentos:

Cliente → Empréstimos

Empréstimo → Parcelas

📌 Status do projeto

✅ Back-end finalizado
📌 Próximos passos possíveis:

Autenticação (JWT)

Paginação e filtros

Front-end (Angular / React)

Dockerização

👨‍💻 Autor

Moisés Marinho
Estudante de Análise e Desenvolvimento de Sistemas
Back-end Java | Spring Boot

🔗 GitHub: https://github.com/moiseesmarinho
🔗 GitHub: https://www.linkedin.com/in/moiseesmarinho/
