# Desafio sistema de gerenciamento de tarefas (To-Do List) - Simplify

## Repositório Original
https://github.com/simplify-tec/desafio-junior-backend-simplify

## Visão Geral

O sistema deve manter um cadastro de usuários e suas respectivas tarefas. 
Cada usuário possui nome, e-mail e senha de acesso. Os dados das tarefas são:
nome, descrição, realizado e prioridade. O usuário pode incluir, editar, visualizar e
excluir tarefas que pertencem a ele.

## Modelo Conceitual

- Cada tarefa pertence a um único usuário, enquanto que um usuário pode ter uma ou mais tarefas.
- Um usuário tem apenas um único perfil no sistema (***user***)

![uml](https://github.com/aplaraujo/desafio-tecnico-simplify-backend-junior/blob/main/assets/todo_uml.png)

## Casos de Uso

O escopo do sistema consiste nos seguinte casos de uso:

| Caso de Uso        | Visão Geral                                        | Acesso              |
|--------------------|----------------------------------------------------|---------------------|
| ***Sign up***      | Cadastrar-se no sistema                            | Público             |
| ***Login***        | Entrar no sistema (desde que já esteja cadastrado) | Público             |
| Criar tarefas      | Cadastro das próprias tarefas                      | Usuário autenticado |
| Visualizar tarefas | Visualização das próprias tarefas                  | Usuário autenticado |
| Editar tarefas     | Alteração total ou parcial das próprias tarefas    | Usuário autenticado |
| Excluir tarefas    | Exclusão das próprias tarefas                      | Usuário autenticado |

### Atores

| Ator            | Responsabilidade                                                        |
|-----------------|-------------------------------------------------------------------------|
| Usuário anônimo | Pode acessar as áreas públicas do sistema (***login*** e ***sign up***) |
| Usuário         | Responsável por manter as próprias tarefas no sistema                   |

## Casos de Uso (Detalhes)

### Consultar tarefas

| Atores  | Precondições        | Pós-condições | Visão geral                                                                            |
|---------|---------------------|---------------|----------------------------------------------------------------------------------------|
| Usuário | Usuário autenticado | -             | Visualizar tarefas disponíveis<br/>podendo filtrar tarefas por número de identificação |

### Cenário de sucesso

1. [Saída] O sistema informa uma lista de tarefas, ordenadas por número de identificação.
2. [Entrada] O usuário informa o número de identificação de uma tarefa.
3. [Saída] O sistema informa a tarefa que corresponde ao número de identificação fornecido pelo usuário.

### Manter tarefas

| Atores  | Precondições        | Pós-condições | Visão geral     |
|---------|---------------------|---------------|-----------------|
| Usuário | Usuário autenticado | -             | CRUD de tarefas |

### Cenário de sucesso

1. Executar caso de uso: **Consultar tarefas**
2. O usuário seleciona uma das opções:
   1. Variante Inserir
   2. Variante Atualizar
   3. Variante Excluir

### Cenário alternativo: variantes

2.1\. Variante Inserir</br>
2.1.1. [Entrada] O usuário informa nome, descrição, realização e prioridade da respectiva tarefa.

2.2\. Variante Atualizar </br>
2.2.1. [Entrada] O usuário seleciona uma tarefa para editar. </br>
2.2.2. [Saída] O sistema informa nome, descrição, realização e prioridade da respectiva tarefa</br>
2.2.3. [Entrada] O usuário informa novos valores para nome, descrição, realizado e prioridade da tarefa.

2.3\. Variante Excluir</br>
2.3.1. [Entrada] O usuário seleciona uma tarefa para excluir.

### Cenário alternativo: exceções

2.1.1a. Dados inválidos</br>
2.1.1a.1. [Saída] O sistema informa os erros nos campos inválidos [1]</br>
2.1.1a.2. Vai para o passo 2.1.1

2.2.3a. Dados inválidos</br>
2.2.3a.1. [Saída] O sistema informa os erros nos campos inválidos [1]</br>
2.2.3a.2. Vai para o passo 2.2.1

2.2.3b. Número de identificação não encontrado</br>
2.2.3b.1. [Saída] O sistema informa que o número de identificação não existe
2.2.3b.2. Vai para o passo 2.2.1

2.3.1a. Número de identificação não encontrado</br>
2.3.1a.1. [Saída] O sistema informa que o número de identificação não existe
2.3.1a.2. Vai para o passo 2.3.1

### Informações complementares

[1] Validação dos dados
- Nome: não deve ser um campo vazio
- Descrição: não deve ser um campo vazio
- Realizado: não deve ser um campo nulo
- Prioridade: não deve ser um campo nulo

### ***Login***

| Atores          | Precondições | Pós-condições       | Visão geral                  |
|-----------------|--------------|---------------------|------------------------------|
| Usuário anônimo | -            | Usuário autenticado | Efetuar a entrada no sistema |

### Cenário de sucesso

1. [Entrada] O usuário anônimo informa as credenciais (e-mail e senha).
2. [Saída] O sistema informa um ***token*** válido.

### Cenário alternativo: exceções

1a. Credenciais inválidas</br>
1a.1. [Saída] O sistema informa que as credenciais são inválidas</br>
1a.2. Vai para o passo 1

## Testes

### Consulta de tarefas

1. Consulta de tarefas deve retornar 200 quando o usuário estiver autenticado.
2. Consulta de tarefas deve retornar 401 quando o usuário não estiver autenticado.

### Consulta de tarefas por número de identificação

1. Consulta de tarefas por número de identificação deve retornar 200 quando o usuário estiver autenticado e o número de identificação da tarefa existir.
2. Consulta de tarefas por número de identificação deve retornar 401 quando o usuário não estiver autenticado.
3. Consulta de tarefas por número de identificação deve retornar 403 quando o usuário estiver autenticado, porém a tarefa não pertence a esse usuário.
4. Consulta de tarefas por número de identificação deve retornar 404 quando o usuário estiver autenticado e o número de identificação da tarefa não existir.

### Cadastrar tarefas

1. Cadastrar tarefas deve retornar 201 quando o usuário estiver autenticado e todos os dados forem válidos.
2. Cadastrar tarefas deve retornar 422 quando o usuário estiver autenticado e o campo "nome" estiver vazio.
3. Cadastrar tarefas deve retornar 422 quando o usuário estiver autenticado e o campo "descrição" estiver vazio.
4. Cadastrar tarefas deve retornar 422 quando o usuário estiver autenticado e o campo "realizado" for nulo.
5. Cadastrar tarefas deve retornar 422 quando o usuário estiver autenticado e o campo "prioridade" for nulo.
6. Cadastrar tarefas deve retornar 401 quando o usuário não estiver autenticado.

### Atualizar tarefas

1. Atualizar tarefas deve retornar 200 quando o usuário estiver autenticado e todos os dados forem válidos.
2. Atualizar tarefas deve retornar 422 quando o usuário estiver autenticado e o campo "nome" estiver vazio.
3. Atualizar tarefas deve retornar 422 quando o usuário estiver autenticado e o campo "descrição" estiver vazio.
4. Atualizar tarefas deve retornar 422 quando o usuário estiver autenticado e o campo "realizado" for nulo.
5. Atualizar tarefas deve retornar 422 quando o usuário estiver autenticado e o campo "prioridade" for nulo.
6. Atualizar tarefas deve retornar 401 quando o usuário não estiver autenticado.

### Excluir tarefas

1. Excluir tarefas deve retornar 204 quando o usuário estiver autenticado e o número de identificação da tarefa existir na base de dados.
2. Excluir tarefas deve retornar 404 quando o usuário estiver autenticado e o número de identificação da tarefa não existir na base de dados.
3. Excluir tarefas deve retornar 401 quando o usuário não estiver autenticado.

## Tecnologias utilizadas

- Java
- Spring Boot
- JPA / Hibernate
- Maven
- JUnit
- Mockito

## Como executar o projeto

```
# Clone do repositório
https://github.com/aplaraujo/desafio-tecnico-simplify-backend-junior

# Execução do projeto
./mvn spring-boot:run
```

## Autora
Ana Paula Lopes Araujo

https://linkedin.com/in/ana-paula-lopes-araujo