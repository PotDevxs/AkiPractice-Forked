/*
 * This project can	 be redistributed without
 * authorization of the developer
 *
 * Project @ AkiPractice
 * @author saki © 2026
 * Date: 11/02/2026
 */
package aki.saki.practice.prompt

import aki.saki.practice.PracticePlugin
import aki.saki.practice.kit.Kit
import aki.saki.practice.ui.kit.KitCommandMenuEditor
import aki.saki.practice.ui.kit.KitMenu
import aki.saki.practice.utils.CC
import org.bukkit.ChatColor
import org.bukkit.conversations.ConversationContext
import org.bukkit.conversations.Prompt
import org.bukkit.conversations.StringPrompt
import org.bukkit.entity.Player


/*
 * This project can't be redistributed without
 * authorization of the developer
 *
 * Project @ lPractice
 * @author yek4h © 2024
 * Date: 14/06/2024
*/

class KitDisplayNameEditPrompt(
    val kit: Kit,
    val player: Player
) : StringPrompt() {
    override fun getPromptText(p0: ConversationContext?): String {
        return "${ChatColor.YELLOW}Please type the display name to be set ${ChatColor.GRAY}(you can use '&' to set colors)${ChatColor.YELLOW}, or type ${ChatColor.RED} cancel ${ChatColor.YELLOW} to cancel."
    }

    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
        if (input.equals("cancel", false)) {
            context.forWhom.sendRawMessage("${ChatColor.RED}Cancelled creating kit.")

            KitCommandMenuEditor(kit).openMenu(player)
            return Prompt.END_OF_CONVERSATION
        }

        try {
            val kitManager = PracticePlugin.instance.kitManager

            kit.displayName = input
            kitManager.save()
            context.forWhom.sendRawMessage(CC.translate("&aThe kit &a&l${kit.name} has been successfully renamed to $input"))
            KitCommandMenuEditor(kit).openMenu(player)
        } catch (e: Exception) {
            e.printStackTrace()
            context.forWhom.sendRawMessage("${ChatColor.RED}There was an issue changing the display name of this kit.")
        }

        return Prompt.END_OF_CONVERSATION
    }
}
