# AkiPractice

Feito por saki

Forked por Faastyzin

## Como compilar (gerar o .jar)

1. **Requisitos**
   - **JDK 8** (não use só JRE; o Kotlin precisa do JDK para compilar).
   - JARs na pasta `libs/`:
     - `FAWE.jar` (já incluso)
     - `ryu-bukkit.jar` (Ryu Core – obtenha do servidor ou do autor)
     - `katto.jar` (Katto API – idem)
     - `samurai.jar` (opcional)

2. No diretório do projeto, execute:
   ```bash
   .\gradlew.bat shadowJar
   ```
   (No Linux/Mac: `./gradlew shadowJar`)

3. O plugin será gerado em:
   ```
   build/libs/AkiPractice-1.0.jar
   ```
   Esse JAR inclui as dependências (Drink, Assemble, Gson, MongoDB driver, XSeries, etc.).

# Novidades
- RankedBan
- EventBan
- TournamentBan
- Holograms
- New LeaderBoards
- Kill Effects
- Queues
- Duels
- Party
- Party duels
- Events (Sumo, TNT tag, TNT Run & Brackets)
- Kit editor
- Bed Fights
- Fireball Fights
- Hypixel TheBridge
- MLG Rush
- FFA
- Arena duplication
- Modo Spectador

# Bugs
- *(nenhum conhecido no momento)*