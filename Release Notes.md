# Randomizer 1.0.0 — Release Notes

## Sobre o projeto

O **Randomizer** é um aplicativo Android em português para gerar personagens a partir de sete atributos. Inspirado em dinâmicas de RPG, ele pode ser usado individualmente ou em grupo: os dados definem os valores, e os participantes imaginam qual personalidade, aparência e história combinam com o resultado.

O aplicativo funciona **offline**, sem necessidade de conta, servidor ou conexão com um computador depois de instalado.

## Funcionalidades desta versão

- **Dois modos de criação:** Personagem e Vilão.
- **Dados digitais ou físicos:** role D4 e D20 pelo aplicativo ou informe os resultados dos seus próprios dados.
- **Preenchimento guiado:** instruções destacadas, indicação do próximo atributo e correspondência do D4 visível antes da rolagem.
- **Tabuleiro com sete cartões:** quatro atributos na primeira fileira e três na segunda, com botão circular de ação na parte inferior.
- **Seleção por toque:** no modo Personagem, toque em um atributo do grupo ativo para atribuir o valor máximo.
- **Visual medieval:** pergaminho, tipografia clássica, brasão, molduras de bronze e botão com aparência de selo.
- **Dados animados:** giro, salto e alternância de números antes da revelação do resultado. Novas rolagens ficam bloqueadas durante a animação.
- **Transições entre páginas:** deslizamento e mudança de opacidade entre configuração, tabuleiro e resultado, incluindo o retorno à configuração.
- **Exportação em imagem:** ao completar os atributos, o botão principal muda para Exportar e permite salvar um PNG com os nomes e valores, no mesmo estilo medieval da interface.
- **Preservação do progresso:** rotação e restauração da tela pelo Android mantêm os valores e o resultado de uma rolagem em andamento.
- **Reinício com confirmação:** a opção Novo personagem pede confirmação antes de descartar um preenchimento em andamento.

## Atributos

| Grupo | Atributos |
| --- | --- |
| Primeiro | Inteligência, Dinheiro, Saúde e Beleza |
| Segundo | Físico, Altura e Cabelo |

Os atributos do primeiro grupo mostram apenas seus valores. No segundo grupo, as referências podem ser consultadas ao tocar no cartão, quando ele não estiver sendo usado para escolher o máximo:

| Atributo | Valor 0 | Valor 20 |
| --- | --- | --- |
| Físico | Obeso | Corpo escultural |
| Altura | De fadas até anões | 2,10 m ou mais |
| Cabelo | Careca | Cabelos lindos, independentemente do comprimento |

## Modos de criação

### Vilão

Cada um dos sete atributos recebe o resultado de um D20, entre **1 e 20**, seguindo a ordem do tabuleiro. Esse modo não atribui zero.

### Personagem

Cada grupo recebe um atributo escolhido com **20**, um atributo determinado pelo D4 com **0** e os valores restantes por D20.

**Primeiro grupo — quatro atributos:**

1. Escolha o atributo que receberá 20.
2. Os três restantes correspondem a 1, 2 e 3, seguindo a ordem do tabuleiro.
3. Role um D4 para definir qual receberá 0. Se sair 4, role novamente.
4. Role um D20 para cada um dos dois atributos restantes.

**Segundo grupo — três atributos:**

1. Escolha o atributo que receberá 20.
2. Role um D4: 1 ou 3 atribui 0 ao primeiro restante; 2 ou 4, ao segundo.
3. Role um D20 para o último atributo.

Um D20 também pode produzir 20, mesmo que outro atributo do grupo já tenha sido maximizado. As regras são iguais para dados digitais e físicos; a entrada manual aceita apenas resultados válidos para o dado da etapa.

## Exportação do resultado

Ao finalizar, toque em **Exportar**, escolha a pasta de destino na janela do Android e confirme o salvamento.

A imagem gerada tem **1080 × 1280 pixels**, em formato **PNG**, e inclui o modo de criação, os sete atributos e seus valores. A exportação não depende do tamanho da tela ou da posição da rolagem e não inclui os controles do aplicativo.

Salvar ou cancelar não descarta o personagem. A exportação pode ser repetida, e a opção **Novo personagem** permanece no topo da tela.

## Requisitos e instalação

- **Android 6.0 (API 23) ou superior.**
- Copie o APK desta versão para o aparelho e abra-o para instalar.
- Se solicitado pelo Android, autorize a instalação pela origem utilizada para abrir o APK.
- Após a instalação, abra o Randomizer pela lista de aplicativos. Não é necessário manter o celular conectado ao computador.
- O uso do aplicativo e a exportação para uma pasta local funcionam sem internet.

## Validação registrada

- **13 testes de regras e dados de exportação aprovados.**
- Compilação do aplicativo e do pacote de testes Android concluída com sucesso.
- Análise estática com lint concluída sem erros.
- **14 testes Android compilados**, abrangendo interface, restauração de estado, dados animados, exportação PNG e transições de páginas.
- A execução dos testes Android e a conferência visual da versão atual no aparelho permanecem pendentes no ambiente automatizado, devido a uma restrição de permissão do ADB.

## Limitações atuais

- Um personagem por vez, sem histórico interno. Os resultados podem ser preservados por exportação em PNG.
- Não há geração automática de nomes, personalidades ou histórias; a interpretação fica com os participantes.
- A preservação de estado pelo Android não substitui um salvamento permanente. Encerrar a tarefa ou limpar os dados do aplicativo pode descartar o personagem atual.

## Base técnica

Desenvolvido em **Kotlin**, com **Jetpack Compose** e **Material 3**. As regras são separadas da interface e verificadas por testes automatizados. A aplicação não utiliza banco de dados, serviços de autenticação ou backend.
