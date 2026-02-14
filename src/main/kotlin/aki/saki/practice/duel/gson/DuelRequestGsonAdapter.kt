/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.duel.gson

import com.google.gson.*
import aki.saki.practice.PracticePlugin
import aki.saki.practice.arena.Arena
import aki.saki.practice.duel.DuelRequest
import aki.saki.practice.kit.Kit
import java.lang.reflect.Type
import java.util.*

object DuelRequestGsonAdapter : JsonSerializer<DuelRequest>, JsonDeserializer<DuelRequest> {

    override fun serialize(duelRequest: DuelRequest, p1: Type?, p2: JsonSerializationContext?): JsonElement {
        val json = JsonObject()

        json.addProperty("uuid", duelRequest.uuid.toString())
        json.addProperty("target", duelRequest.target.toString())
        json.addProperty("arena", duelRequest.arena.name)
        json.addProperty("kit", duelRequest.kit.name)
        json.addProperty("executedAt", duelRequest.executedAt)

        return json
    }

    override fun deserialize(element: JsonElement, p1: Type?, p2: JsonDeserializationContext?): DuelRequest? {
        if (element.isJsonNull) return null

        val json = element.asJsonObject

        val request = DuelRequest(UUID.fromString(json.get("uuid").asString),
            UUID.fromString(json.get("target").asString),
            PracticePlugin.instance.kitManager.getKit(json.get("kit").asString)!!,
            Arena.getByName(json.get("arena").asString)!!
        )

        request.executedAt = json.get("executedAt").asLong

        return request
    }
}
