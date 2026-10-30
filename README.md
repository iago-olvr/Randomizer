# Randomizer — gerador de personagens

Aplicativo Android em português para criar combinações de sete atributos. Uma pessoa ou um grupo interpreta os valores e imagina qual personagem combina com eles.

## Funcionalidades

- Visual medieval com pergaminho, vinho nas faixas e ações, tons rosados nos cartões selecionados, tipografia de livro antigo, brasão e bordas de bronze.
- Transições com deslizamento e mudança de opacidade entre configuração, tabuleiro e resultado, incluindo o retorno à configuração.
- Dois modos: **Vilão** e **Personagem**.
- Dados digitais ou entrada dos resultados de dados físicos.
- Etapas guiadas, com a correspondência do D4 visível antes da rolagem.
- Animação dos D4 e D20 digitais, com giro, salto e alternância de números antes de revelar o resultado.
- Tabuleiro com quatro cartões quadrados na primeira fileira e três na segunda, com o botão circular de rolagem centralizado na parte inferior.
- Toque no cartão do grupo ativo para escolher o atributo máximo. O próximo atributo a receber D20 aparece destacado.
- Botão **?** de 48 dp em cada um dos sete atributos, para consultar as dicas mesmo durante a escolha do máximo.
- Resultado final para interpretação pelos jogadores; o nome informado substitui a frase na faixa, e removê-lo restaura a frase original.
- Atributos 20 com borda dourada e partículas de brilho contínuas; atributos 0 com rachaduras de ruína. Cartões preenchidos usam fundo **#39393b**, inclusive no PNG; os vazios mantêm o fundo original. O 20 usa o dourado da borda; o 0 usa cinza-pedra **#B8B4AC**, sem contorno, com uma rachadura atravessando o número e pequenos fragmentos nas bordas; os valores de 1 a 19 usam o mesmo pergaminho claro.
- Grupos próximos entre si, com conteúdo rolável e ações inferiores fixas.
- Instruções da etapa atual destacadas acima dos atributos; a última rolagem deixa de aparecer no resultado final.
- Rodapé fixo e transparente em todas as telas, deixando o pergaminho aparente. Na inicial, reúne ajuda (**?**), criação (**+**) e histórico; o **+** permanece ligeiramente elevado em relação aos botões laterais, com sombra. A ajuda detalha modos, dados e regras.
- Resultado com três botões lado a lado: **Editar nome**, **Compartilhar** e **Ver histórico**.
- Compartilhamento do resultado em PNG, com nome opcional e os sete atributos nas duas fileiras.
- Histórico local dos personagens concluídos, com data de criação e reabertura do resultado.
- No histórico, deslize para a direita para compartilhar a imagem ou para a esquerda para excluir; as setas no cartão são indicações dos gestos, e excluir exige confirmação.
- Progresso preservado durante rotação e restauração da tela pelo Android.
- O botão X cancela diretamente a criação; o comando Voltar do Android mantém a confirmação para descartar uma ficha incompleta.
- Funciona offline, sem conta, servidor, banco de dados ou permissão de internet.

## Regras

**Grupo 1:** Inteligência, Dinheiro, Saúde e Beleza.

**Grupo 2:** Físico, Altura e Cabelo. Todos os atributos têm dicas acessíveis pelo botão **?**:

| Atributo | 0 | 20 |
| --- | --- | --- |
| Inteligência | Pergaminho em branco | Biblioteca ambulante |
| Dinheiro | Depois de pagar os boletos | Antes de pagar os boletos |
| Saúde | Se levantar a mão DEUS puxa | Data de válidade do Mel |
| Beleza | Da medo no espelho | Filho do Michael B. Jordan com a Alessandra Negrini |
| Físico | Alvo de Bullying | Escultura de Michelangelo |
| Altura | Desde Fadas Até Anões | Minímo 2,10 de altura |
| Cabelo | Só o Couro Cabeçudo | Propaganda de Xampu |

### Vilão

Cada um dos sete atributos recebe um D20, de **1 a 20**, na ordem da tela. O modo não produz zero.

### Personagem

No primeiro grupo:

1. Escolha um atributo para receber **20**.
2. Os três restantes correspondem a **1, 2 e 3**, na ordem da tela.
3. Lance um D4. O atributo correspondente recebe **0**. Se sair **4**, lance novamente, sem limite de repetições.
4. Lance um D20 para cada um dos dois restantes, na ordem da tela.

No segundo grupo:

1. Escolha um atributo para receber **20**.
2. Lance um D4: **1 ou 3** atribui **0** ao primeiro restante; **2 ou 4**, ao segundo.
3. Lance um D20 para o último atributo.

Os D20 podem produzir 20, mesmo quando outro atributo já foi maximizado. O zero é atribuído pela regra do D4; não é um resultado válido para digitação de um D20 ou D4. Os dados físicos seguem exatamente as mesmas etapas que os digitais.

### Rolagem animada

Cada rolagem digital anima o D4 ou D20 por aproximadamente 1,1 segundo e exibe o resultado por mais 0,65 segundo antes de preencher o atributo. Quando o D20 revela 20, uma celebração de sucesso crítico destaca o dado com pulso, borda dourada e uma explosão de brilhos; a revelação dura 1,5 segundo. Nos demais resultados, a revelação continua durando 0,65 segundo. Durante esse período, novas ações ficam bloqueadas. Recriar a tela retoma a apresentação do mesmo resultado, sem sortear novamente. Os números intermediários são apenas visuais.

## Finalizar, nomear e compartilhar

1. Complete os sete atributos, usando dados digitais ou físicos.
2. O personagem é salvo automaticamente no histórico local, mesmo sem nome.
3. Se quiser, toque em **Editar nome** e depois em **Salvar nome**. O nome é opcional, pode ter até 60 caracteres e pode ser editado mais tarde.
4. Toque em **Compartilhar** para escolher um aplicativo no painel do Android. A imagem é um **PNG de 1080 × 1280 pixels**, incluindo o nome informado.

A imagem contém o modo (Vilão ou Personagem), o nome opcional e os valores dos atributos, com o mesmo estilo de pergaminho da interface. Ela não depende do tamanho da tela nem da posição da rolagem. Cancelar o compartilhamento não descarta o personagem, e é possível tentar novamente. O botão **X / Cancelar**, no canto superior direito da ficha, retorna diretamente à tela inicial e descarta uma criação incompleta. Personagens já salvos permanecem no histórico. Para iniciar outra criação, use o botão **+** maior da tela inicial.

O painel de compartilhamento oferece os aplicativos instalados no aparelho. Não há botão separado para salvar PNG no resultado.

O histórico pode ser aberto na tela inicial em **Histótico**, ou no resultado em **Ver histórico**. Toque em um registro para reabrir seus atributos, editar o nome ou compartilhar novamente. Os registros aparecem do mais recente para o mais antigo; editar o nome atualiza o mesmo registro.

Em cada item, deslizar da esquerda para a direita compartilha a imagem sem remover o personagem. Deslizar da direita para a esquerda abre uma confirmação para excluir somente aquele registro. Os textos **Compartilhar →** e **← Excluir** indicam as direções; as ações são executadas pelo arrasto. Cancelar a confirmação preserva o personagem. Se a exclusão falhar, o app informa o erro e mantém o registro. Excluir uma ficha que estava aberta também encerra sua visualização para que ela não seja recriada automaticamente.

Os dados ficam em um arquivo privado do app, sem banco de dados ou servidor. Fechar o aplicativo preserva o histórico; desinstalar ou limpar os dados do app o remove. O compartilhamento concede acesso temporário somente à imagem escolhida. O envio pode depender de internet conforme o aplicativo de destino.

Para consultar as regras, toque em **? / Como jogar** no rodapé inicial. A ajuda explica o D4 de cada grupo, os dois modos, os dados físicos e digitais, as referências dos atributos e o uso do histórico. Os botões de voltar são setas para a esquerda (**←**), com descrição acessível. Voltar da ajuda mantém as opções escolhidas.

## Abrir e executar

1. Instale o [Android Studio](https://developer.android.com/studio).
2. Na tela inicial, escolha **Open** e selecione a pasta deste projeto.
3. Aguarde a sincronização do Gradle. A primeira sincronização precisa de internet.
4. Se solicitado, instale a plataforma **Android SDK 36** pelo SDK Manager.
5. Em **Settings > Build, Execution, Deployment > Build Tools > Gradle**, selecione um JDK compatível, versão **17 ou 21**. O JDK incluído no Android Studio pode ser usado; não use Java 6.
6. Crie um emulador no Device Manager ou conecte um celular com depuração USB habilitada.
7. Selecione a configuração **app** e clique em **Run**.

O aplicativo suporta Android **6.0 (API 23)** ou superior.

O Android Studio cria `local.properties` com o caminho do SDK da sua máquina. Esse arquivo não deve ser compartilhado ou versionado.

### Comandos no Windows

Com um JDK compatível disponível em `JAVA_HOME` ou no `PATH`, e o SDK configurado:

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat lintDebug assembleDebug
```

O APK de depuração é gerado em `app/build/outputs/apk/debug/app-debug.apk`. Ele pode ser instalado para testes; a publicação na loja requer uma etapa posterior de assinatura e preparação da versão de distribuição.

Para os testes de interface, conecte um celular ou inicie um emulador:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

## Organização

```text
app/src/main/java/com/randomizer/personagens/
  MainActivity.kt       Entrada do aplicativo
  game/Game.kt          Atributos, estado e regras, sem dependência do Android
  game/CharacterSummary.kt Dados completos para exportação e nomes dos modos
  export/CharacterImageExporter.kt Geração do PNG
  export/CharacterSharing.kt Compartilhamento por URI temporária
  history/CharacterHistory.kt Histórico privado com escrita atômica
  ui/RandomizerApp.kt   Configuração, etapas guiadas e resultado
  ui/CharacterBoard.kt Tabuleiro de atributos e ação de rolagem
  ui/HomeActions.kt    Rodapé inicial com ajuda, criação e histórico
  ui/HelpScreen.kt     Explicação detalhada dos modos e das regras
  ui/HistoryScreen.kt  Histórico com gestos e confirmação de exclusão
  ui/DiceRollDialog.kt  Animação e revelação dos dados digitais
  ui/Theme.kt           Cores e tema
  ui/MedievalDecoration.kt Textura vetorial, brasão, molduras e ornamentos
  design/MedievalColors.kt Paleta compartilhada pela interface e pela imagem
app/src/test/           Testes das regras
app/src/androidTest/    Testes de interface e restauração de estado
```

As regras usam transições imutáveis. O D4 escolhe apenas entre os atributos restantes, respeitando a ordem original. O gerador permite injetar resultados nos testes, sem depender de sorte para verificar os casos.

As transições de página duram aproximadamente 380 ms e respeitam a escala de animação do Android. Cada página usa um retrato do estado correspondente, para que a página de saída não receba valores da próxima durante a animação. Mudanças de atributos dentro do tabuleiro não reiniciam a transição de página. As decorações são vetoriais e usam fontes do aparelho, sem baixar recursos externos.

## Validação desta entrega

- **15 testes unitários passaram** com `testDebugUnitTest`, incluindo regras, dados de exportação e validação dos registros do histórico.
- `lintDebug`, `assembleDebug` e `assembleDebugAndroidTest` concluídos com sucesso com o tema medieval e as transições de página.
- XMLs conferidos e disponibilidade das dependências principais verificada.
- Gradle Wrapper conferido pelo SHA256 oficial; o hash da distribuição está fixado nas propriedades do Wrapper.
- Os 32 testes Android incluem entrada física, restauração de estado, animação, disposição dos cartões, maximização por toque, instruções acima dos atributos, três ações no resultado, sete dicas, ajuda, rodapé inicial, exportação PNG, transições entre páginas, persistência e exclusão do histórico, gestos sem compartilhamento duplicado, confirmação e cancelamento da exclusão, troca da frase pelo nome, rodapé fixo com fonte ampliada e destaques dos extremos na ficha e no PNG, fundo dos cartões preenchidos e celebração exclusiva do 20 no D20 e movimento das partículas somente enquanto o atributo contém 20. Foram compilados, mas sua execução no dispositivo permanece pendente.
- A execução dos testes de interface precisa de um aparelho ou emulador acessível pelo ADB. No ambiente de execução automatizada desta alteração, o ADB encontrou uma restrição de permissão ao acessar sua pasta de preferências.

A pasta local `.tools/` contém as ferramentas temporárias e o relatório dessa validação JVM. Ela é ignorada pelo Git e não faz parte do aplicativo.

## Limitações da primeira versão

- Não gera nome, texto de personalidade ou interpretação automática; o nome é informado pela pessoa.
- O histórico é apenas local, sem sincronização ou recuperação após desinstalação ou limpeza dos dados.
- Personagens que ficaram incompletos não entram no histórico.

## Ferramentas

Kotlin 2.2.21, Jetpack Compose, Material 3, Android Gradle Plugin 8.13.2 e Gradle 8.13. As dependências são fixadas nos arquivos Gradle; o Wrapper baixa a distribuição do Gradle na primeira execução.

Referências: [compatibilidade do AGP](https://developer.android.com/build/releases/agp-8-13-0-release-notes), [Compose BOM](https://developer.android.com/develop/ui/compose/bom) e [configuração do Kotlin](https://kotlinlang.org/docs/gradle-configure-project.html).
