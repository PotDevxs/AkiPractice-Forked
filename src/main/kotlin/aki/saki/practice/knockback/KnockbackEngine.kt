package aki.saki.practice.knockback

import org.bukkit.util.Vector
import kotlin.math.cos
import kotlin.math.sin

object KnockbackEngine {

    fun compute(pendingHit: KnockbackService.PendingHit, fallbackVelocity: Vector): Vector {
        val profile = pendingHit.profile
        val direction = getDirection(pendingHit, fallbackVelocity)
        val result = pendingHit.victimVelocity.clone()

        result.x /= profile.horizontalFriction
        result.y /= profile.verticalFriction
        result.z /= profile.horizontalFriction

        var horizontal = profile.horizontal
        var vertical = profile.vertical

        if (!pendingHit.victimOnGround) {
            horizontal *= profile.airHorizontalMultiplier
            vertical *= profile.airVerticalMultiplier
        }

        result.x -= direction.x * horizontal
        result.z -= direction.z * horizontal

        if (profile.addHorizontal != 0.0) {
            result.x -= direction.x * profile.addHorizontal
            result.z -= direction.z * profile.addHorizontal
        }

        result.y += vertical + profile.addVertical

        if (pendingHit.attackerSprinting) {
            val sprintExtra = getSprintExtra(pendingHit.attackerYaw, profile.extraHorizontal)
            result.x += sprintExtra.x
            result.z += sprintExtra.z
            result.y += profile.extraVertical
        }

        result.y = result.y.coerceIn(profile.verticalMin, profile.verticalMax)
        return result
    }

    private fun getDirection(pendingHit: KnockbackService.PendingHit, fallbackVelocity: Vector): Vector {
        val direction = Vector(pendingHit.deltaX, 0.0, pendingHit.deltaZ)

        if (direction.lengthSquared() > 1.0E-6) {
            return direction.normalize()
        }

        val yawBased = getSprintExtra(pendingHit.attackerYaw, 1.0)
        if (yawBased.lengthSquared() > 1.0E-6) {
            return yawBased.normalize()
        }

        val fallback = fallbackVelocity.clone().setY(0.0)
        return if (fallback.lengthSquared() > 1.0E-6) fallback.normalize() else Vector(0, 0, 0)
    }

    private fun getSprintExtra(attackerYaw: Float, horizontal: Double): Vector {
        val radians = Math.toRadians(attackerYaw.toDouble())
        return Vector(-sin(radians) * horizontal, 0.0, cos(radians) * horizontal)
    }
}
