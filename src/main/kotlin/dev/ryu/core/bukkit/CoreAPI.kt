/*
 * Stub para compilação sem ryu-bukkit.jar.
 * Se você tiver o JAR original do Ryu Core, coloque em libs/ryu-bukkit.jar
 * e remova este arquivo para usar a API real.
 */
package dev.ryu.core.bukkit

import java.util.*

object CoreAPI {
    val grantSystem = GrantSystemStub
}

object GrantSystemStub {
    val repository = RepositoryStub
    fun findBestRank(ranks: Any?): RankStub = RankStub
}

object RepositoryStub {
    fun findAllByPlayer(uuid: UUID): Any? = null
}

object RankStub {
    const val color: String = "WHITE"
}
