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
import aki.saki.practice.ui.kit.KitCommandMenuEditor
import aki.saki.practice.ui.kit.KitMenu
import aki.saki.practice.ui.kit.KitMetadataList
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

class KitCreatePrompt(
    val player: Player
) : StringPrompt() {
    override fun getPromptText(context: ConversationContext?): String {
        return "${ChatColor.YELLOW}Please type a name for this kit to be created, or type ${ChatColor.RED} cancel ${ChatColor.YELLOW} to cancel."
    }

    override fun acceptInput(context: ConversationContext, input: String): Prompt? {
        if (input.equals("cancel", true)) {
            context.forWhom.sendRawMessage("${ChatColor.RED}Cancelled creating kit.")

            KitMenu().openMenu(player)
            return END_OF_CONVERSATION
        }

        try {
          val kitManager = PracticePlugin.instance.kitManager
          if (kitManager.getKit(input) != null) {
              kitManager.getKit(input)
              context.forWhom.sendRawMessage("${ChatColor.RED}A kit with that name already exists!")
          } else {
              kitManager.createKit(input)
              kitManager.save()
              val kit = kitManager.getKit(input)
              if (kit == null) {
                  KitMenu().openMenu(player)
              } else {
                  KitCommandMenuEditor(kit).openMenu(player)
              }
              context.forWhom.sendRawMessage("${ChatColor.GREEN}Kit successfully created with the name ${ChatColor.BOLD}${kitManager.getKit(input)!!.name}")
          }
        } catch (e: Exception) {
            e.printStackTrace()
            context.forWhom.sendRawMessage("${ChatColor.RED}There was an issue creating this kit")
        }

        return END_OF_CONVERSATION
    }
}
