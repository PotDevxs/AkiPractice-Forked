/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.database

import org.bson.Document
import java.util.concurrent.CompletableFuture

/**
 * Camada de persistência: MongoDB (produção) ou arquivos JSON (testes em localhost sem Mongo).
 */
interface PracticeDatabase : AutoCloseable {

    fun initialize(): CompletableFuture<Void>

    fun profileFindById(id: String): Document?

    fun profileUpsert(document: Document, async: Boolean)

    fun profileDelete(id: String)

    fun profileListAll(): List<Document>

    /**
     * Mongo: sort nativo no campo (notação com pontos, ex.: kitStatistics.Kit.elo).
     * Flat-file: carrega todos os perfis e ordena em memória.
     */
    fun profileFindSorted(sortKey: String, descending: Boolean, limit: Int): List<Document>

    fun arenaRatingUpsert(document: Document)

    fun arenaRatingListAll(): List<Document>

    fun campUpsert(document: Document)

    fun campDelete(id: String)

    fun campListAll(): List<Document>

    override fun close() {}
}
