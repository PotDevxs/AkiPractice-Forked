package aki.saki.practice.knockback

import aki.saki.practice.PracticePlugin
import aki.saki.practice.utils.ConfigFile
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object KnockbackService {

    private val profiles = ConcurrentHashMap<String, KnockbackProfile>()
    private val pendingHits = ConcurrentHashMap<UUID, PendingHit>()
    private val serverSprintStates = ConcurrentHashMap<UUID, Boolean>()

    var packetModeEnabled = true
        private set

    var restoreServerMotion = true
        private set

    var trackServerSprint = true
        private set

    fun load(configFile: ConfigFile) {
        profiles.clear()
        pendingHits.clear()
        serverSprintStates.clear()
        packetModeEnabled = configFile.config.getBoolean("KNOCKBACK.SETTINGS.PACKET-MODE", true)
        restoreServerMotion = configFile.config.getBoolean("KNOCKBACK.SETTINGS.RESTORE-SERVER-MOTION", true)
        trackServerSprint = configFile.config.getBoolean("KNOCKBACK.SETTINGS.TRACK-SERVER-SPRINT", true)

        KnockbackProfile.defaults().forEach { profile ->
            profiles[profile.name.lowercase()] = profile
        }

        val section = configFile.getConfigurationSection("KNOCKBACK.PROFILES") ?: return
        section.getKeys(false).forEach { key ->
            val profileSection = section.getConfigurationSection(key) ?: return@forEach
            val defaultProfile = profiles[key.lowercase()] ?: profiles["default"]!!

            profiles[key.lowercase()] = KnockbackProfile(
                name = key,
                horizontal = profileSection.getDouble("HORIZONTAL", defaultProfile.horizontal),
                vertical = profileSection.getDouble("VERTICAL", defaultProfile.vertical),
                extraHorizontal = profileSection.getDouble("EXTRA-HORIZONTAL", defaultProfile.extraHorizontal),
                extraVertical = profileSection.getDouble("EXTRA-VERTICAL", defaultProfile.extraVertical),
                verticalMin = getDouble(profileSection, listOf("VERTICAL-MIN"), defaultProfile.verticalMin),
                verticalMax = getDouble(profileSection, listOf("VERTICAL-MAX", "VERTICAL-LIMIT"), defaultProfile.verticalMax),
                airHorizontalMultiplier = getDouble(profileSection, listOf("AIR-HORIZONTAL-MULTIPLIER", "AIR-HORIZONTAL"), defaultProfile.airHorizontalMultiplier),
                airVerticalMultiplier = getDouble(profileSection, listOf("AIR-VERTICAL-MULTIPLIER", "AIR-VERTICAL"), defaultProfile.airVerticalMultiplier),
                horizontalFriction = getDouble(profileSection, listOf("HORIZONTAL-FRICTION", "FRICTION-HORIZONTAL"), defaultProfile.horizontalFriction),
                verticalFriction = getDouble(profileSection, listOf("VERTICAL-FRICTION", "FRICTION-VERTICAL"), defaultProfile.verticalFriction),
                addHorizontal = getDouble(profileSection, listOf("ADD-HORIZONTAL"), defaultProfile.addHorizontal),
                addVertical = getDouble(profileSection, listOf("ADD-VERTICAL"), defaultProfile.addVertical),
                stopSprint = profileSection.getBoolean("STOP-SPRINT", defaultProfile.stopSprint)
            )
        }

        PracticePlugin.instance.server.onlinePlayers.forEach { player ->
            setServerSprinting(player.uniqueId, player.isSprinting)
        }

        PracticePlugin.instance.logger.info("Loaded ${profiles.size} knockback profiles.")
    }

    fun getProfile(name: String?): KnockbackProfile {
        val normalizedName = name?.trim()?.lowercase()
        return profiles[normalizedName] ?: profiles["default"]!!
    }

    fun hasProfile(name: String?): Boolean {
        if (name.isNullOrBlank()) return false
        return profiles.containsKey(name.trim().lowercase())
    }

    fun getProfileNames(): List<String> {
        return profiles.values.map { it.name }.sorted()
    }

    fun queueHit(victim: Player, attacker: Player, profileName: String?) {
        val profile = getProfile(profileName)
        val attackerSprinting = isServerSprinting(attacker)

        pendingHits[victim.uniqueId] = PendingHit(
            attackerId = attacker.uniqueId,
            profile = profile,
            attackerSprinting = attackerSprinting,
            attackerYaw = attacker.location.yaw,
            deltaX = victim.location.x - attacker.location.x,
            deltaZ = victim.location.z - attacker.location.z,
            victimOnGround = victim.isOnGround,
            victimVelocity = victim.velocity.clone(),
            createdAt = System.currentTimeMillis()
        )

        if (profile.stopSprint && attackerSprinting) {
            attacker.isSprinting = false
            setServerSprinting(attacker.uniqueId, false)
        }
    }

    fun consumePendingHit(victimId: UUID): PendingHit? {
        val pendingHit = pendingHits.remove(victimId) ?: return null
        if (System.currentTimeMillis() - pendingHit.createdAt > 2_000L) {
            return null
        }
        return pendingHit
    }

    fun clear(playerId: UUID) {
        pendingHits.remove(playerId)
        serverSprintStates.remove(playerId)
    }

    fun setServerSprinting(playerId: UUID, sprinting: Boolean) {
        serverSprintStates[playerId] = sprinting
    }

    fun isServerSprinting(player: Player): Boolean {
        if (!trackServerSprint) {
            return player.isSprinting
        }
        return serverSprintStates[player.uniqueId] ?: player.isSprinting
    }

    private fun getDouble(section: org.bukkit.configuration.ConfigurationSection, keys: List<String>, defaultValue: Double): Double {
        for (key in keys) {
            if (section.contains(key)) {
                return section.getDouble(key, defaultValue)
            }
        }
        return defaultValue
    }

    data class PendingHit(
        val attackerId: UUID,
        val profile: KnockbackProfile,
        val attackerSprinting: Boolean,
        val attackerYaw: Float,
        val deltaX: Double,
        val deltaZ: Double,
        val victimOnGround: Boolean,
        val victimVelocity: Vector,
        val createdAt: Long
    )
}
