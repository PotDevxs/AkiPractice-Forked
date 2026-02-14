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
import aki.saki.practice.arena.Arena

/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 17/06/2024
*/

class ArenaProvider : DrinkProvider<Arena>() {
    override fun doesConsumeArgument() = true
    override fun isAsync() = false
    override fun allowNullArgument() = false
    override fun defaultNullValue(): Arena? = null

    override fun provide(arg: CommandArg, annotations: List<Annotation>): Arena {
        val arenaName = arg.get()
        return Arena.getByName(arenaName) ?: throw IllegalArgumentException("Arena not found: $arenaName")
    }

    override fun argumentDescription() = "arena"
    override fun getSuggestions(prefix: String): List<String> {
        return Arena.arenas.map { it.name }.filter { it.startsWith(prefix, ignoreCase = true) }
    }
}
