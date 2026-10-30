package com.randomizer.personagens.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.randomizer.personagens.game.CharacterRules

@Composable
internal fun HelpScreen(onBack: () -> Unit, enabled: Boolean) {
    Column(Modifier.widthIn(max = 640.dp).fillMaxSize()) {
        Column(Modifier.weight(1f).fillMaxWidth()
            .verticalScroll(rememberScrollState()).padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Como jogar", style = MaterialTheme.typography.headlineLarge)
            HelpSection("Comece pelas opções",
                "Escolha o modo de criação e quais dados usar na tela inicial. Toque em + para criar um novo personagem com essas opções. O botão de relógio abre o histórico local.")
            HelpSection("Os sete atributos",
                "O primeiro grupo reúne Inteligência, Dinheiro, Saúde e Beleza. O segundo traz Físico, Altura e Cabelo. Os valores vão de 0 a 20; interpretem juntos o que eles significam para a personalidade e a história do personagem.")
            HelpSection("Modo Personagem — primeiro grupo",
                "1. Toque em um dos quatro atributos para receber 20.\n" +
                    "2. Os três restantes correspondem a 1, 2 e 3, na ordem em que aparecem.\n" +
                    "3. Role um D4: o atributo correspondente recebe 0. Se sair 4, role novamente até sair 1, 2 ou 3.\n" +
                    "4. Role um D20 para cada um dos dois atributos restantes, na ordem da tela.")
            HelpSection("Modo Personagem — segundo grupo",
                "1. Escolha um dos três atributos para receber 20.\n" +
                    "2. Role um D4: 1 ou 3 dá 0 ao primeiro atributo restante; 2 ou 4 dá 0 ao segundo.\n" +
                    "3. Role um D20 para o último atributo.\n" +
                    "O D20 pode produzir outro 20, mesmo que já exista um atributo maximizado no grupo.")
            HelpSection("Modo Vilão",
                "Role um D20 para cada um dos sete atributos, na ordem da tela. Cada valor fica entre 1 e 20; este modo não usa D4, não atribui zero e não permite escolher um 20.")
            HelpSection("Dados no aplicativo",
                "Toque em Rolar D4 ou Rolar D20 quando solicitado. Aguarde a animação terminar: o resultado é aplicado ao atributo automaticamente. Durante a animação, novas ações ficam bloqueadas.")
            HelpSection("Dados físicos",
                "Lance o dado indicado, digite o resultado e toque em Confirmar resultado. O D4 aceita de 1 a 4 e o D20 de 1 a 20. Zero não é uma entrada válida: ele é atribuído pela regra do D4.")
            HelpSection("Referências dos atributos",
                CharacterRules.attributes.filter { it.extremes != null }
                    .joinToString("\n") { "${it.name}: ${it.extremes}" } +
                    "\nUse o botão ? de qualquer atributo para consultar sua dica, inclusive durante a escolha do máximo. Tocar no cartão na etapa de escolha atribui 20.")
            HelpSection("Nome, compartilhamento e histórico",
                "Ao preencher todos os atributos, o personagem é salvo automaticamente no aparelho. Use Editar nome para informar um nome opcional de até 60 caracteres, substituindo a frase na faixa do resultado. Apagar o nome restaura a frase. Compartilhar envia uma imagem com o nome e os sete atributos ao aplicativo escolhido. Ver histórico abre os personagens salvos, do mais recente para o mais antigo. Toque em um registro para reabrir ou renomear; editar o nome não duplica o personagem. No histórico, deslize para a direita para compartilhar e para a esquerda para excluir. As setas no cartão indicam os gestos. A exclusão pede confirmação.")
            HelpSection("Recomeçar e guardar seus personagens",
                "O botão X no topo da ficha cancela a criação e volta diretamente à tela inicial. Uma ficha incompleta é descartada; personagens já salvos permanecem no histórico. As setas para a esquerda voltam à tela anterior. Apenas personagens concluídos entram no histórico. O app funciona offline e não exige conta; o envio da imagem depende do aplicativo escolhido. O histórico permanece ao fechar o app, mas é removido ao desinstalar ou limpar os dados.")
        }
        FixedFooter("help-footer") {
            BackButton(onClick = onBack, enabled = enabled, description = "Voltar às opções")
        }
    }
}

@Composable
private fun HelpSection(title: String, body: String) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(title, style = MaterialTheme.typography.titleLarge)
        Text(body, style = MaterialTheme.typography.bodyLarge)
    }
}
