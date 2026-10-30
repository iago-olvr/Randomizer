# Randomizer — Novidades após a versão 1.1.0

Esta atualização aprimora o visual medieval, acrescenta efeitos para os valores extremos e reorganiza a navegação com ações fixas na parte inferior das telas. Também traz confirmação de exclusão no histórico e ajustes no tratamento dos gestos de compartilhamento.

As novidades abaixo correspondem à comparação do projeto com a tag **v1.1.0**.

## Destaques visuais dos atributos

- Atributos com valor **20** recebem borda e número dourados, acompanhados de partículas de brilho contínuas na ficha.
- As partículas permanecem ativas somente enquanto o atributo contém 20.
- Atributos com valor **0** recebem acabamento de ruína, com rachaduras e pequenos fragmentos nas bordas do cartão.
- O número zero aparece em cinza-pedra, sem contorno, com uma rachadura atravessando o próprio número.
- Todos os cartões preenchidos passam a ter fundo cinza-escuro; valores de 1 a 19 usam o tom claro de pergaminho para facilitar a leitura.
- Cartões ainda vazios preservam o fundo do tema e a indicação do próximo atributo a preencher.

## Celebração de sucesso crítico

- Revelar **20 em uma rolagem digital de D20** agora apresenta a mensagem **Sucesso crítico!**.
- O dado recebe um pulso de destaque, borda dourada e uma explosão de brilhos.
- A revelação do sucesso crítico dura **1,5 segundo**, após a animação de rolagem. Os demais resultados mantêm a revelação de **0,65 segundo**.
- A celebração é exclusiva do resultado 20 no D20; não é acionada pelo resultado máximo do D4.

## Interface e navegação

- Rodapés **fixos e transparentes** na tela inicial, na ficha, na ajuda e no histórico, mantendo o pergaminho aparente.
- O conteúdo pode rolar sem levar os controles inferiores para fora da tela.
- Na tela inicial, o botão **+** continua em destaque, ligeiramente elevado em relação aos botões laterais e com sombra.
- Os dois grupos de atributos ficam mais próximos, com menos espaços vazios entre eles.
- Refinamento da paleta medieval, com tons de vinho nas faixas e ações e tons rosados nos cartões selecionados.
- No resultado, o nome informado passa a ocupar a faixa que antes mostrava a frase de interpretação. Remover o nome restaura a frase original.
- A entrada de resultados de dados físicos também respeita o bloqueio de interação enquanto as ações da ficha estão indisponíveis.

## Histórico e gestos

- Deslizar um personagem para a **esquerda** agora abre uma confirmação antes de excluí-lo.
- A opção **Cancelar** preserva o personagem no histórico.
- Deslizar para a **direita** continua compartilhando a imagem sem remover o registro.
- Os textos **Compartilhar →** e **← Excluir** passam a funcionar como indicações dos gestos, em vez de botões separados; as ações são executadas pelo arrasto do cartão.
- O tratamento dos gestos foi ajustado para evitar compartilhamentos duplicados e reposicionar o cartão antes de abrir o painel de compartilhamento ou a confirmação de exclusão.

## Imagens compartilhadas

- O PNG passa a reproduzir o fundo escuro dos cartões preenchidos e as cores dos números exibidas na ficha.
- Valores **20** recebem borda dourada mais espessa e detalhes estáticos de brilho na imagem.
- Valores **0** incluem rachaduras, fragmentos e a fratura sobre o número, seguindo o acabamento de ruína da interface.
- O compartilhamento mantém o formato **PNG de 1080 × 1280 pixels**, o nome opcional, o modo de criação e os sete atributos.
- Os efeitos animados ficam na interface; a imagem compartilhada registra seus destaques visuais em uma composição estática.

## Validação e testes

- Cobertura ampliada para destaques dos extremos na ficha e no PNG, fundo dos cartões preenchidos, celebração exclusiva do 20 no D20 e movimento das partículas.
- Novas verificações de confirmação e cancelamento da exclusão, gestos sem compartilhamento duplicado, troca da frase pelo nome e rodapés fixos com fonte ampliada.
- A documentação do projeto registra **15 testes unitários aprovados** e **32 testes Android compilados**.
- A execução dos testes Android no aparelho permanece pendente no ambiente automatizado, devido à restrição de permissão do ADB já registrada no projeto.

## Compatibilidade

As regras dos modos **Personagem** e **Vilão** permanecem as mesmas. O requisito mínimo continua sendo **Android 6.0 (API 23)**, com funcionamento offline para criação e consulta ao histórico. O envio de imagens pode depender de internet conforme o aplicativo de destino.
