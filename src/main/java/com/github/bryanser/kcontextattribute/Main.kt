package com.github.bryanser.kcontextattribute

import com.github.bryanser.kcontextattribute.attribute.ContextManager
import com.github.bryanser.kcontextattribute.impl.*
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin() {

    override fun onLoad() {
        Plugin = this
        ContextManager.register(PhysicalDamage)
        ContextManager.register(BloodDamage)
        ContextManager.register(FireDamage)
        ContextManager.register(LightningDamage)
        ContextManager.register(TrueDamage)
        ContextManager.register(Crit)
    }

    override fun onEnable() {
        ContextManager.init()
    }

    override fun onDisable() {
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        if (sender is Player && args.isNotEmpty() && args[0].equals("seeme", true)) {
            val ctx = ContextManager.getContext(sender, true)
            sender.sendMessage("§6=====你的属性如下=====")
            for (c in ctx.contexts.values) {
                sender.sendMessage(c.toString())
            }
        }
        return false
    }

    companion object {
        lateinit var Plugin: Main
    }
}