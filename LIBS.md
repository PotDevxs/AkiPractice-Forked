# JARs necessários para compilação (quando não usar stubs)

O projeto referencia bibliotecas que **não estão em repositórios Maven públicos**. Resumo do que foi pesquisado:

---

## 1. `ryu-bukkit.jar` (Ryu Core – `dev.ryu.core:bukkit`)

- **Uso no código:** `dev.ryu.core.bukkit.CoreAPI` para sistema de ranks (cores de nome no leaderboard, filas, duelos).
- **Onde procurar:** Não há repositório Maven público nem release oficial encontrado. O lPractice original ([Zowpy/lPractice](https://github.com/Zowpy/lPractice)) **não** usa Ryu Core; essa dependência foi adicionada em um fork (ex.: AkiPractice).
- **Recomendações:**
  - Se você tem o servidor/plugin de onde saiu este fork, copie o JAR do Ryu Core de lá (ex.: `plugins/` ou pasta do core) para `libs/ryu-bukkit.jar`.
  - Ou use a compilação com **stubs** (veja abaixo): o projeto inclui stubs que permitem compilar sem este JAR; no servidor você precisará do Ryu Core real se quiser ranks corretos.

---

## 2. `katto.jar` (Katto API – `rip.katz.api:Katto`)

- **Uso no código:** `rip.katz.api.Katto` para criação/destruição de hologramas no comando `/leaderboard setup` (leaderboards globais).
- **Onde procurar:** Nenhum repositório Maven público ou GitHub com essa API foi encontrado. É uma API de hologramas proprietária.
- **Recomendações:**
  - Se você tem o plugin/servidor de origem, procure por um plugin com nome tipo “Katto” ou “Katz” e use o JAR em `libs/katto.jar`.
  - Ou use a compilação com **stubs**: o stub redireciona para o `HologramManager` do próprio AkiPractice, assim o projeto compila e o `/leaderboard setup` usa o sistema de hologramas interno.

---

## 3. `samurai.jar`

- **Uso no código:** Não há referências no código-fonte a classes de `samurai.jar`.
- **Conclusão:** Provavelmente dependência legada ou opcional. Não é obrigatória para compilar; pode ser removida do `build.gradle` se não for usada em outro lugar.

---

## Compilação sem os JARs (stubs incluídos)

O projeto inclui **stubs** em código-fonte para `CoreAPI` (Ryu) e `Katto`:

- **Ryu:** `src/main/kotlin/dev/ryu/core/bukkit/CoreAPI.kt` — fornece cor padrão `WHITE` para nomes nos leaderboards/filas.
- **Katto:** `src/main/kotlin/rip/katz/api/Katto.kt` — redireciona hologramas para o `HologramManager` do AkiPractice.

Com isso:

- **Não é necessário** ter `ryu-bukkit.jar` nem `katto.jar` em `libs/` para compilar.
- Use **JDK 8** (não JRE) e execute: `.\gradlew.bat shadowJar`
- No servidor, sem os JARs reais: ranks usam cor branca; `/leaderboard setup` usa o sistema de hologramas do plugin.

Para usar os JARs originais (comportamento “full”):

1. Coloque em `libs/`: `ryu-bukkit.jar` e `katto.jar`
2. Remova os stubs: apague as pastas `src/main/kotlin/dev` e `src/main/kotlin/rip`
3. No `build.gradle`, descomente/adicione de volta:  
   `compileOnly files('libs/ryu-bukkit.jar')` e `compileOnly files('libs/katto.jar')`
4. Execute `.\gradlew.bat shadowJar`

---

## Resumo

| JAR             | Encontrado em repositório público? | Ação recomendada                                      |
|-----------------|-------------------------------------|--------------------------------------------------------|
| `ryu-bukkit.jar`| Não                                 | Obter do servidor/autor ou compilar com stubs         |
| `katto.jar`     | Não                                 | Obter do servidor/autor ou compilar com stubs         |
| `samurai.jar`   | Não referenciado no código          | Opcional; pode ser ignorado ou removido do build      |
