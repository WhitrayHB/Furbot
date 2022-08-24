package org.example.mirai.plugin

import cn.whitrayhb.furbot.FurbotMain
import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader

suspend fun main() {
    MiraiConsoleTerminalLoader.startAsDaemon()

    FurbotMain.INSTANCE.load()
    FurbotMain.INSTANCE.enable()

    MiraiConsole.job.join()
}