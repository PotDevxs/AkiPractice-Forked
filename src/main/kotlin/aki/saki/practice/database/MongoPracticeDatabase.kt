/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 */
package aki.saki.practice.database

import com.mongodb.client.model.Filters
import com.mongodb.client.model.ReplaceOptions
import com.mongodb.client.model.UpdateOptions
import org.bson.Document
import java.util.concurrent.CompletableFuture

class MongoPracticeDatabase(
    private val mongo: MongoManager
) : PracticeDatabase {

    override fun initialize(): CompletableFuture<Void> = mongo.initialize()

    override fun profileFindById(id: String): Document? =
        mongo.profileCollection.find(Filters.eq("_id", id)).firstOrNull()

    override fun profileUpsert(document: Document, async: Boolean) {
        val id = document.getString("_id") ?: return
        val op = Runnable {
            mongo.profileCollection.updateOne(
                Filters.eq("_id", id),
                Document("\$set", document),
                UpdateOptions().upsert(true)
            )
        }
        if (async) CompletableFuture.runAsync(op) else op.run()
    }

    override fun profileDelete(id: String) {
        mongo.profileCollection.deleteOne(Filters.eq("_id", id))
    }

    override fun profileListAll(): List<Document> {
        val list = mutableListOf<Document>()
        mongo.profileCollection.find().into(list)
        return list
    }

    override fun profileFindSorted(sortKey: String, descending: Boolean, limit: Int): List<Document> {
        val dir = if (descending) -1 else 1
        val list = mutableListOf<Document>()
        mongo.profileCollection.find()
            .sort(Document(sortKey, dir))
            .limit(limit)
            .into(list)
        return list
    }

    override fun arenaRatingUpsert(document: Document) {
        CompletableFuture.runAsync {
            mongo.arenaRatingsCollection.replaceOne(
                Filters.eq("uuid", document.getString("uuid")),
                document,
                ReplaceOptions().upsert(true)
            )
        }
    }

    override fun arenaRatingListAll(): List<Document> {
        val list = mutableListOf<Document>()
        mongo.arenaRatingsCollection.find().into(list)
        return list
    }

    override fun campUpsert(document: Document) {
        mongo.campCollection.replaceOne(
            Filters.eq("_id", document.getString("_id")),
            document,
            ReplaceOptions().upsert(true)
        )
    }

    override fun campDelete(id: String) {
        mongo.campCollection.deleteOne(Filters.eq("_id", id))
    }

    override fun campListAll(): List<Document> {
        val list = mutableListOf<Document>()
        mongo.campCollection.find().into(list)
        return list
    }

    override fun close() {
        mongo.close()
    }
}
