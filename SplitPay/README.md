# SplitPay

SplitPay é um aplicativo de divisão de despesas em grupo, criado para organizar contas compartilhadas de forma simples, visual e rápida. A proposta do projeto é transformar o processo de dividir gastos em algo claro, intuitivo e fácil de acompanhar, seja em viagens, casas compartilhadas, eventos, churrascos ou qualquer situação com várias pessoas envolvidas.

## Proposta do projeto

A ideia central do SplitPay é resolver um problema comum: quando várias pessoas dividem despesas, fica difícil entender quem pagou, quanto cada um deve e qual é a melhor forma de acertar as contas.

O app centraliza tudo em um fluxo único:
- criação de grupos;
- cadastro de participantes;
- registro de despesas;
- cálculo de saldos;
- visualização das dívidas para zerar o grupo.

## Objetivo

O objetivo do SplitPay é facilitar a gestão financeira entre grupos, reduzindo confusão e tornando o acerto de contas mais transparente.

Com o aplicativo, o usuário consegue:
- visualizar o saldo geral do grupo;
- entender quanto te devem e quanto você deve;
- adicionar novas despesas;
- dividir valores entre participantes;
- acompanhar dívidas e transações necessárias para zerar o grupo.

## Fluxo da aplicação

O fluxo principal do app segue estas etapas:

1. **Home**
   - mostra um resumo geral das finanças;
   - exibe pendências e grupos cadastrados;
   - permite acessar um grupo existente ou criar um novo grupo.

2. **Criar Grupo**
   - permite escolher um emoji para representar o grupo;
   - cadastrar o nome do grupo;
   - adicionar participantes;
   - confirmar a criação do grupo.

3. **Detalhes do Grupo**
   - mostra o saldo do grupo;
   - lista os participantes;
   - exibe as despesas registradas;
   - oferece ações para adicionar despesa ou ver as dívidas.

4. **Nova Despesa**
   - permite informar categoria, descrição e valor;
   - selecionar quem pagou;
   - definir com quem a despesa será dividida;
   - salvar a despesa no grupo.

5. **Dívidas**
   - mostra as transações necessárias para zerar o grupo;
   - indica quem deve pagar quem;
   - simplifica o acerto final entre os participantes.

## Regras de negócio do SplitPay

A lógica do aplicativo pode ser resumida nas seguintes regras:

### 1. Um grupo possui participantes
Cada grupo reúne uma lista de pessoas que compartilham despesas.

### 2. Toda despesa tem um pagador
Ao criar uma despesa, o usuário define quem arcou com o valor total.

### 3. Toda despesa pode ser dividida
O valor da despesa é distribuído entre os participantes selecionados.

### 4. O saldo é calculado automaticamente
Cada participante pode ter:
- crédito, quando pagou mais do que sua parte;
- débito, quando deve uma parte maior do que pagou.

### 5. O app mostra o saldo líquido
O usuário consegue ver:
- quanto te devem;
- quanto você deve;
- total do grupo.

### 6. A tela de dívidas sugere o acerto ideal
Em vez de mostrar apenas saldos soltos, o app transforma os valores em transações que ajudam a zerar o grupo com menos transferências.

### 7. O acerto deve ser simples e visual
A proposta é apresentar as informações de forma clara para facilitar a tomada de decisão e o fechamento das contas.

### 8. Edição e exclusão são restritas ao admin do grupo
As ações de editar ou excluir um grupo ficam disponíveis somente para quem criou o grupo ou possui permissão de administrador. Essa regra centraliza as configurações do grupo em uma conta responsável, evitando alterações indevidas por outros participantes.

## Diferenciais visuais

O SplitPay foi desenhado com foco em consistência visual e experiência de uso:
- tema escuro moderno;
- cards com cantos arredondados;
- uso de verde como cor de ação e destaque;
- estrutura padronizada entre as telas;
- hierarquia visual clara para leitura rápida.

## Conceitos que o projeto demonstra

Este projeto pode ser apresentado como uma solução que combina:
- **organização de despesas compartilhadas**;
- **cálculo automático de saldos**;
- **visualização de dívidas entre participantes**;
- **navegação intuitiva entre telas**;
- **interface moderna e consistente**.

## Resumo para apresentação

O SplitPay é um app para organizar grupos e despesas compartilhadas, calculando automaticamente saldos e dívidas para facilitar o acerto entre os participantes. A proposta do projeto é tornar o controle financeiro em grupo mais simples, visual e eficiente.

## Possíveis evoluções futuras

O projeto ainda pode evoluir com recursos como:
- persistência de dados;
- edição e exclusão de grupos e despesas com permissões por admin;
- divisão proporcional ou personalizada;
- histórico completo de pagamentos;
- sincronização entre usuários;
- exportação de relatórios.
