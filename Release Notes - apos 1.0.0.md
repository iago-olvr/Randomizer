# Randomizer — Novidades após a versão 1.0.0

Esta atualização acrescenta histórico local, nomes editáveis e compartilhamento de personagens, além de melhorias na navegação e na organização da interface.

As alterações abaixo correspondem à comparação do projeto com a tag **v1.0.0**.

## Novas funcionalidades

### Histórico de personagens

- Personagens concluídos são salvos automaticamente no histórico local, mesmo sem nome.
- Os registros mostram o modo de criação e a data e hora, ordenados do mais recente para o mais antigo.
- É possível reabrir um personagem para consultar seus atributos, editar seu nome ou compartilhar novamente.
- O histórico pode ser acessado pela tela inicial ou pelo botão **Ver histórico** no resultado.
- Deslizar um registro para a **direita** compartilha sua imagem; deslizar para a **esquerda** exclui o registro, sem confirmação adicional.
- As ações também estão disponíveis pelos botões **Compartilhar →** e **← Excluir**.
- Fechar o aplicativo preserva o histórico. Personagens incompletos não são incluídos.

### Nome opcional e editável

- Novo botão **Editar nome** na tela de resultado.
- O nome pode ter até **60 caracteres** e pode ser alterado posteriormente pelo histórico.
- Alterar o nome atualiza o mesmo personagem, sem criar um registro duplicado.
- O nome informado também aparece na imagem compartilhada, com ajuste de tamanho para caber na largura disponível.

### Compartilhamento de imagens

- O resultado agora pode ser enviado pelo painel de compartilhamento do Android, usando os aplicativos instalados no aparelho.
- A imagem continua em formato **PNG de 1080 × 1280 pixels**, com o visual de pergaminho, o modo de criação, os sete atributos e seus valores; agora também inclui o nome opcional.
- O compartilhamento pode ser iniciado no resultado ou diretamente no histórico.
- Cancelar o painel de compartilhamento não descarta o personagem.
- O acesso concedido ao aplicativo de destino é temporário e limitado à imagem escolhida.

### Ajuda e dicas

- Nova tela **Como jogar**, acessível pelo botão **?** da tela inicial.
- A ajuda explica os modos Personagem e Vilão, os dados digitais e físicos, as regras do D4 e o uso do histórico.
- Todos os **sete atributos** receberam dicas para os valores extremos, com textos atualizados.
- Cada atributo possui um botão **?** próprio, permitindo consultar a dica inclusive durante a escolha do atributo máximo.

## Melhorias na interface e navegação

- Novo rodapé inicial com acesso à ajuda, botão **+** em destaque para iniciar uma criação e acesso ao histórico.
- Tela de resultado reorganizada em três ações lado a lado: **Editar nome**, **Compartilhar** e **Ver histórico**.
- Instruções da etapa atual reposicionadas **acima dos atributos**.
- A mensagem da última rolagem deixa de aparecer no resultado final.
- Novo botão **X / Cancelar** no canto superior direito da ficha para retornar diretamente à tela inicial. Uma criação incompleta é descartada; personagens já salvos permanecem no histórico.
- O comando **Voltar** do Android mantém a confirmação antes de descartar uma ficha incompleta.
- Botões de retorno com seta para a esquerda e descrição acessível.
- Voltar da ajuda preserva as opções de criação escolhidas.

## Mudanças em relação à versão 1.0.0

- O antigo botão **Exportar**, que abria uma janela para salvar o PNG em uma pasta, foi substituído pelo fluxo de **Compartilhar**. Não há mais um botão separado para salvar a imagem no resultado.
- O histórico passa a preservar personagens concluídos entre aberturas do aplicativo, além da restauração temporária de estado que já existia.
- As regras de rolagem dos modos **Personagem** e **Vilão** permanecem as mesmas.

## Armazenamento e cuidados com os dados

- O histórico fica em um arquivo privado do aplicativo, sem banco de dados, conta ou servidor.
- A gravação do histórico utiliza escrita atômica para proteger os registros em caso de falha durante o salvamento.
- Falhas de leitura não substituem o histórico existente por uma lista vazia.
- Se uma exclusão falhar, o aplicativo informa o erro e mantém o registro. Excluir uma ficha aberta também encerra sua visualização, evitando que seja recriada automaticamente.
- Desinstalar o aplicativo ou limpar seus dados remove o histórico local. Não há sincronização ou recuperação pela nuvem.
- A criação e a consulta ao histórico funcionam offline. O envio de uma imagem pode depender de internet, conforme o aplicativo de destino.

## Validação e testes

- Cobertura ampliada para validação de registros, persistência e exclusão do histórico, gestos, edição de nome, compartilhamento, ajuda e novos controles da interface.
- A documentação do projeto registra **15 testes unitários aprovados** e **24 testes Android compilados**.
- A execução dos testes Android no aparelho permanece pendente no ambiente automatizado, devido à restrição de permissão do ADB já registrada no projeto.

## Compatibilidade

O requisito mínimo permanece **Android 6.0 (API 23)**. O aplicativo continua funcionando sem conta e sem conexão com um computador após a instalação.
