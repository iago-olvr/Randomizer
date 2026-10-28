# Randomizer — gerador de personagens

Aplicativo Android em português para criar combinações de sete atributos. Uma pessoa ou um grupo interpreta os valores e imagina qual personagem combina com eles.

## Funcionalidades

- Visual medieval com pergaminho, tipografia de livro antigo, brasão, bordas de bronze e botão em forma de selo.
- Transições com deslizamento e mudança de opacidade entre configuração, tabuleiro e resultado, incluindo o retorno à configuração.
- Dois modos: **Vilão** e **Personagem**.
- Dados digitais ou entrada dos resultados de dados físicos.
- Etapas guiadas, com a correspondência do D4 visível antes da rolagem.
- Animação dos D4 e D20 digitais, com giro, salto e alternância de números antes de revelar o resultado.
- Tabuleiro com quatro cartões quadrados na primeira fileira e três na segunda, com o botão circular de rolagem centralizado na parte inferior.
- Toque no cartão do grupo ativo para escolher o atributo máximo. O próximo atributo a receber D20 aparece destacado.
- Toque em Físico, Altura ou Cabelo para consultar as referências dos extremos, quando o cartão não estiver sendo usado para escolher o máximo.
- Resultado final para interpretação pelos jogadores.
- Cabeçalho simplificado com o modo atual e instruções destacadas acima do botão principal.
- Exportação do resultado em PNG, com os sete atributos e seus valores nas duas fileiras.
- Progresso preservado durante rotação e restauração da tela pelo Android.
- Confirmação antes de descartar um personagem em andamento.
- Funciona offline, sem conta, servidor, banco de dados ou permissão de internet.

## Regras

**Grupo 1:** Inteligência, Dinheiro, Saúde e Beleza. Exibem somente os valores.

**Grupo 2:** Físico, Altura e Cabelo. As referências exibidas são:

| Atributo | 0 | 20 |
| --- | --- | --- |
| Físico | Obeso | Corpo escultural |
| Altura | De fadas até anões | 2,10 m ou mais |
| Cabelo | Careca | Cabelos lindos, independentemente do comprimento |

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

Cada rolagem digital anima o D4 ou D20 por aproximadamente 1,1 segundo e exibe o resultado por mais 0,65 segundo antes de preencher o atributo. Durante esse período, novas ações ficam bloqueadas. Recriar a tela retoma a apresentação do mesmo resultado, sem sortear novamente. Os números intermediários são apenas visuais.

## Exportar o resultado

1. Complete os sete atributos, usando dados digitais ou físicos.
2. O botão circular muda para **Exportar**.
3. Toque nele e escolha uma pasta no seletor de arquivos do Android, por exemplo **Downloads**.
4. Confirme o nome do arquivo para salvar a imagem **PNG de 1080 × 1280 pixels**.

A imagem contém o modo (Vilão ou Personagem), os nomes e os valores dos atributos, com o mesmo estilo de pergaminho da interface. Ela não depende do tamanho da tela nem da posição da rolagem. O aplicativo informa se a gravação foi concluída ou falhou. Cancelar ou salvar não descarta o personagem, e é possível tentar novamente. Para iniciar outro, use **Novo personagem** no topo.

A exportação para uma pasta local funciona offline e não pede permissão de acesso geral aos arquivos. A janela de salvamento pode oferecer também provedores de nuvem, conforme os aplicativos instalados no aparelho.

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
  ui/RandomizerApp.kt   Configuração, etapas guiadas e resultado
  ui/CharacterBoard.kt Tabuleiro de atributos e ação de rolagem
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

- **13 testes de regras e dados de exportação passaram** com `testDebugUnitTest` no projeto Android.
- `lintDebug`, `assembleDebug` e `assembleDebugAndroidTest` concluídos com sucesso com o tema medieval e as transições de página.
- XMLs conferidos e disponibilidade das dependências principais verificada.
- Gradle Wrapper conferido pelo SHA256 oficial; o hash da distribuição está fixado nas propriedades do Wrapper.
- Os 14 testes Android incluem entrada física, restauração de estado, animação, disposição dos cartões, maximização por toque, cabeçalho simplificado, exportação PNG e transições entre páginas. Foram compilados, mas sua execução no dispositivo permanece pendente.
- A execução dos testes de interface precisa de um aparelho ou emulador acessível pelo ADB. No ambiente de execução automatizada desta alteração, o ADB encontrou uma restrição de permissão ao acessar sua pasta de preferências.

A pasta local `.tools/` contém as ferramentas temporárias e o relatório dessa validação JVM. Ela é ignorada pelo Git e não faz parte do aplicativo.

## Limitações da primeira versão

- Um personagem por vez, sem histórico interno; o resultado pode ser salvo como PNG.
- Não gera nome, texto de personalidade ou interpretação automática.
- O estado salvo pelo Android não substitui um histórico permanente: encerrar a tarefa ou limpar os dados pode descartá-lo.

## Ferramentas

Kotlin 2.2.21, Jetpack Compose, Material 3, Android Gradle Plugin 8.13.2 e Gradle 8.13. As dependências são fixadas nos arquivos Gradle; o Wrapper baixa a distribuição do Gradle na primeira execução.

Referências: [compatibilidade do AGP](https://developer.android.com/build/releases/agp-8-13-0-release-notes), [Compose BOM](https://developer.android.com/develop/ui/compose/bom) e [configuração do Kotlin](https://kotlinlang.org/docs/gradle-configure-project.html).
