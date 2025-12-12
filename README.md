# **CRM de Empréstimos**

Backend de um sistema de **gestão de empréstimos pessoais**, desenvolvido em **Java com Spring Boot**, focado no controle de **clientes**, **contratos**, **parcelas** e **pagamentos**.

O projeto simula um **cenário real de crédito/financiamento**, aplicando **regras de negócio**, **persistência relacional** e **APIs REST** prontas para integração com frontend ou sistemas externos.

---

## 🎯 **Objetivo do Projeto**

Este projeto foi desenvolvido com foco em:

- **Prática real de Spring Boot + JPA/Hibernate**
- **Modelagem de domínio financeiro**
- **Aplicação de regras de negócio no backend**
- **Organização em camadas (Controller, Service, Repository)**
- **Persistência com banco de dados relacional (PostgreSQL)**
- **Testes manuais de API via Postman**

---

## 🛠️ **Tecnologias Utilizadas**

- **Java 21**
- **Spring Boot**
- **Spring Data JPA**
- **Hibernate**
- **PostgreSQL**
- **Maven**
- **Postman** (testes manuais)
- **Git & GitHub**

---

## 🧱 **Estrutura do Projeto**

```text
src/main/java/br/com/crm/crmemprestimos
├── controller        # Endpoints REST
├── service           # Regras de negócio
├── repository        # Acesso a dados (JPA)
├── model             # Entidades do domínio
├── dto               # Requests e Responses
├── exception         # Tratamento de erros
└── CrmEmprestimosApplication.java
```
