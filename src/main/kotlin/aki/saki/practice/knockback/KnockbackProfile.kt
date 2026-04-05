package aki.saki.practice.knockback

data class KnockbackProfile(
    val name: String,
    val horizontal: Double,
    val vertical: Double,
    val extraHorizontal: Double,
    val extraVertical: Double,
    val verticalMin: Double,
    val verticalMax: Double,
    val airHorizontalMultiplier: Double,
    val airVerticalMultiplier: Double,
    val horizontalFriction: Double,
    val verticalFriction: Double,
    val addHorizontal: Double,
    val addVertical: Double,
    val stopSprint: Boolean
) {
    companion object {
        fun defaults(): List<KnockbackProfile> {
            return listOf(
                KnockbackProfile(
                    name = "default",
                    horizontal = 0.39,
                    vertical = 0.37,
                    extraHorizontal = 0.12,
                    extraVertical = 0.02,
                    verticalMin = 0.0,
                    verticalMax = 0.40,
                    airHorizontalMultiplier = 1.0,
                    airVerticalMultiplier = 1.0,
                    horizontalFriction = 0.60,
                    verticalFriction = 1.0,
                    addHorizontal = 0.0,
                    addVertical = 0.0,
                    stopSprint = true
                ),
                KnockbackProfile(
                    name = "sumo",
                    horizontal = 0.46,
                    vertical = 0.36,
                    extraHorizontal = 0.16,
                    extraVertical = 0.01,
                    verticalMin = 0.0,
                    verticalMax = 0.40,
                    airHorizontalMultiplier = 1.0,
                    airVerticalMultiplier = 0.95,
                    horizontalFriction = 0.55,
                    verticalFriction = 1.0,
                    addHorizontal = 0.0,
                    addVertical = 0.0,
                    stopSprint = true
                ),
                KnockbackProfile(
                    name = "boxing",
                    horizontal = 0.36,
                    vertical = 0.35,
                    extraHorizontal = 0.08,
                    extraVertical = 0.01,
                    verticalMin = 0.0,
                    verticalMax = 0.39,
                    airHorizontalMultiplier = 0.98,
                    airVerticalMultiplier = 0.95,
                    horizontalFriction = 0.60,
                    verticalFriction = 1.0,
                    addHorizontal = 0.0,
                    addVertical = 0.0,
                    stopSprint = true
                ),
                KnockbackProfile(
                    name = "combo",
                    horizontal = 0.34,
                    vertical = 0.33,
                    extraHorizontal = 0.03,
                    extraVertical = 0.0,
                    verticalMin = 0.0,
                    verticalMax = 0.37,
                    airHorizontalMultiplier = 0.95,
                    airVerticalMultiplier = 0.9,
                    horizontalFriction = 0.65,
                    verticalFriction = 1.0,
                    addHorizontal = 0.0,
                    addVertical = 0.0,
                    stopSprint = false
                )
            )
        }
    }
}
