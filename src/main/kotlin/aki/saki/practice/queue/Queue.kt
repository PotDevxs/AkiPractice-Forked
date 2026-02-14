/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.queue

import aki.saki.practice.kit.Kit

class Queue(var kit: Kit, var type: QueueType) {

    val queuePlayers: MutableList<QueuePlayer> = mutableListOf()
    var requiredPlayers: Int = 2

    fun addPlayer(queuePlayer: QueuePlayer) {
        queuePlayers.add(queuePlayer)
    }

    fun removePlayer(queuePlayer: QueuePlayer) {
        queuePlayers.remove(queuePlayer)
    }

    fun getQueueingPlayers(): List<QueuePlayer> {
        return queuePlayers.toList()
    }

    fun tickAllRanges() {
        queuePlayers.forEach { it.tickRange() }
    }

    fun getPlayerCount(): Int {
        return queuePlayers.size
    }
}
