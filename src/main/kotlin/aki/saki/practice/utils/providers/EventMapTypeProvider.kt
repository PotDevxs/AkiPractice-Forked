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
import aki.saki.practice.event.map.type.EventMapType


/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 17/06/2024
*/

class EventMapTypeProvider : DrinkProvider<EventMapType>() {
    override fun doesConsumeArgument() = true
    override fun isAsync() = false
    override fun allowNullArgument() = false
    override fun defaultNullValue(): EventMapType? = null

    override fun provide(arg: CommandArg, annotations: List<Annotation>): EventMapType {
        val typeName = arg.get().toUpperCase()
        return EventMapType.values().find { it.name == typeName }
            ?: throw IllegalArgumentException("Invalid event map type: $typeName")
    }

    override fun argumentDescription() = "event map type"
    override fun getSuggestions(prefix: String): List<String> {
        return EventMapType.values().map { it.name }.filter { it.startsWith(prefix, ignoreCase = true) }
    }
}
