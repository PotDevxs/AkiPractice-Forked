/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.database

import org.bson.Document
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.nio.charset.StandardCharsets
import java.util.concurrent.CompletableFuture

/**
 * Persistência em JSON por entidade (pasta dentro do data folder do plugin).
 * Indicado para testes locais sem MongoDB; não escala para muitos jogadores online.
 */
class FlatFilePracticeDatabase(
    private val plugin: JavaPlugin,
    private val root: File
) : PracticeDatabase {

    private val lock = Any()
    private val profilesDir = File(root, "profiles")
    private val arenaRatingsDir = File(root, "arena-ratings")
    private val campsDir = File(root, "camps")

    override fun initialize(): CompletableFuture<Void> = CompletableFuture.runAsync {
        profilesDir.mkdirs()
        arenaRatingsDir.mkdirs()
        campsDir.mkdirs()
        plugin.logger.info("[Database] Modo FLAT_FILE ativo em: ${root.absolutePath}")
    }

    override fun profileFindById(id: String): Document? {
        val file = File(profilesDir, "$id.json")
        if (!file.isFile) return null
        return synchronized(lock) {
            try {
                Document.parse(file.readText(StandardCharsets.UTF_8))
            } catch (_: Exception) {
                null
            }
        }
    }

    override fun profileUpsert(document: Document, async: Boolean) {
        val id = document.getString("_id") ?: return
        val file = File(profilesDir, "$id.json")
        val json = document.toJson()
        fun write() = synchronized(lock) {
            profilesDir.mkdirs()
            file.writeText(json, StandardCharsets.UTF_8)
        }
        if (async) CompletableFuture.runAsync { write() } else write()
    }

    override fun profileDelete(id: String) {
        synchronized(lock) {
            File(profilesDir, "$id.json").delete()
        }
    }

    override fun profileListAll(): List<Document> = synchronized(lock) {
        profilesDir.listFiles()?.filter { it.isFile && it.name.endsWith(".json", ignoreCase = true) }
            ?.mapNotNull { f ->
                try {
                    Document.parse(f.readText(StandardCharsets.UTF_8))
                } catch (_: Exception) {
                    null
                }
            } ?: emptyList()
    }

    override fun profileFindSorted(sortKey: String, descending: Boolean, limit: Int): List<Document> {
        val all = profileListAll()
        val sorted = if (descending) {
            all.sortedWith(
                compareByDescending<Document> { ProfileSortKeyExtractor.asDouble(it, sortKey) }
                    .thenBy { it.getString("_id") ?: "" }
            )
        } else {
            all.sortedWith(
                compareBy<Document> { ProfileSortKeyExtractor.asDouble(it, sortKey) }
                    .thenBy { it.getString("_id") ?: "" }
            )
        }
        return sorted.take(limit)
    }

    override fun arenaRatingUpsert(document: Document) {
        val uuid = document.getString("uuid") ?: return
        CompletableFuture.runAsync {
            synchronized(lock) {
                arenaRatingsDir.mkdirs()
                File(arenaRatingsDir, "$uuid.json").writeText(document.toJson(), StandardCharsets.UTF_8)
            }
        }
    }

    override fun arenaRatingListAll(): List<Document> = synchronized(lock) {
        arenaRatingsDir.listFiles()?.filter { it.isFile && it.name.endsWith(".json", ignoreCase = true) }
            ?.mapNotNull { f ->
                try {
                    Document.parse(f.readText(StandardCharsets.UTF_8))
                } catch (_: Exception) {
                    null
                }
            } ?: emptyList()
    }

    override fun campUpsert(document: Document) {
        val id = document.getString("_id") ?: return
        synchronized(lock) {
            campsDir.mkdirs()
            File(campsDir, "$id.json").writeText(document.toJson(), StandardCharsets.UTF_8)
        }
    }

    override fun campDelete(id: String) {
        synchronized(lock) {
            File(campsDir, "$id.json").delete()
        }
    }

    override fun campListAll(): List<Document> = synchronized(lock) {
        campsDir.listFiles()?.filter { it.isFile && it.name.endsWith(".json", ignoreCase = true) }
            ?.mapNotNull { f ->
                try {
                    Document.parse(f.readText(StandardCharsets.UTF_8))
                } catch (_: Exception) {
                    null
                }
            } ?: emptyList()
    }
}
