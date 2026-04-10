# Funcionalidades – o que pode ser adicionado

Sugestões de módulos e melhorias para o AkiPractice.

---

## Já implementados (base)

### BanRanked
- **Comando:** `/banranked <jogador> [motivo]`, `/banranked unban <jogador>`, `/banranked list`
- **Permissão:** `practice.command.banranked`
- Jogadores banidos não conseguem entrar na fila **Ranked**; motivo opcional; lista persiste em `rankedbans.yml`.

### Camps (clans)
- **Comando:** `/camp create <nome> <tag>`, `/camp leave`, `/camp disband`, `/camp info`, `/camp invite <jogador>`
- Camp é um “clan” com nome e tag (até 5 caracteres). Líder pode convidar e dissolver.
- **Possíveis expansões:** sistema de convite (aceitar/recusar), persistência em MongoDB, leaderboard por camp, camp vs camp (evento ou fila).

---

## Sugestões de adições

### Punishments / moderação
- **Mute** – `/mute <jogador> [motivo]`, `/mute <jogador> [motivo] 7d`, `/mute unmute`, `/mute list`. Persiste em `mutes.yml`.
- **Ban de FFA** – impedir de entrar em FFA. ✅
- **Ban de eventos** – impedir de participar de eventos. ✅
- **Ban de duelos** – além do BanRanked, banir de duelos por convite. ✅

### Ranked / competitivo
- **BanRanked com tempo** – expiração (ex.: 7 dias, 30 dias).
- **Anti-boost** – detectar boosting (mesmo IP, mesmo oponente repetido) e punir ou anular ELO.
- **Fila com prioridade** – doadores entram na fila antes (ou range maior).
- **Restrição por ping na ranked** – já existe ping no perfil; garantir que o matchmaking respeite.

### Camps (expansão)
- **Persistência** – salvar camps no MongoDB (nome, tag, líder, membros).
- **Convite formal** – `CampInvitation` (como `PartyInvitation`) com aceitar/recusar e tempo de expiração.
- **Leaderboard de camps** – por vitórias totais, ELO médio, etc.
- **Camp war** – evento ou fila camp vs camp (soma de ELO ou vitórias).
- **Tag no chat/scoreboard** – exibir `[TAG]` ao lado do nome.

### Social / perfil
- **Histórico de duelos** – últimas N partidas (oponente, kit, resultado) e botão “revanche”.
- **Rematch rápido** – após fim da partida, botão “jogar de novo” com o mesmo oponente.
- **Lista de espectadores** – comando ou hotbar para ver quem está espectando sua partida.
- **Perfil público** – `/stats <jogador>` com wins/losses/ELO por kit.

### Eventos / torneios
- **Torneio formal** – bracket com inscrição, seeds, premiação (já existe evento Brackets; dá para formalizar).
- **Evento camp vs camp** – dois camps se enfrentam em duels ou em um único mapa.

### Recompensas / progressão
- **Missões diárias/semanais** – ex.: “Vença 5 ranked”, “Jogue 3 FFA”; recompensa (moeda, cosmético).
- **Níveis / XP** – ganhar XP por partida e subir de nível (título, tag, cor no chat).

### Cosméticos
- **Efeitos de kill** – partículas ou mensagem ao matar.
- **Vitória** – efeito ou animação ao ganhar.
- **Scoreboard/tag** – cor ou prefixo por nível/camp.

### Técnico / qualidade
- **Report** – jogador reporta (cheat, toxidade); admins veem fila de reports.
- **Log de partidas** – salvar snapshots ou logs para replay/anti-cheat.
- **Backfill de fila** – após X segundos, ampliar range de ELO para achar partida mais rápido.

---

## Resumo

| Recurso        | Dificuldade | Impacto |
|----------------|-------------|--------|
| BanRanked      | ✅ Feito    | Alto   |
| Camps (base)   | ✅ Feito    | Alto   |
| Ban com tempo  | Baixa       | Médio  |
| Camp persistência + convite | Média | Alto |
| Camp leaderboard / war | Média | Alto |
| Rematch / histórico | Média | Médio |
| Mute / outros bans | Baixa | Médio |
| Anti-boost     | Média       | Alto   |
| Report system  | Média       | Médio  |
| Missões / XP   | Alta        | Alto   |

Se quiser, posso detalhar o passo a passo de implementação de algum item (por exemplo: BanRanked com tempo, persistência de Camp ou camp leaderboard).
