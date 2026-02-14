/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.utils.providers

import com.jonahseguin.drink.argument.CommandArg
import com.jonahseguin.drink.parametric.DrinkProvider
import aki.saki.practice.arena.type.ArenaType


/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 17/06/2024
*/

class ArenaTypeProvider : DrinkProvider<ArenaType>() {
    override fun doesConsumeArgument() = true
    override fun isAsync() = false
    override fun allowNullArgument() = false
    override fun defaultNullValue(): ArenaType? = null

    override fun provide(arg: CommandArg, annotations: List<Annotation>): ArenaType {
        val typeName = arg.get().toUpperCase()
        return ArenaType.valueOf(typeName) ?: throw IllegalArgumentException("Invalid arena type: $typeName")
    }

    override fun argumentDescription() = "arena type"
    override fun getSuggestions(prefix: String): List<String> {
        return ArenaType.values().map { it.name }.filter { it.startsWith(prefix, ignoreCase = true) }
    }
}
