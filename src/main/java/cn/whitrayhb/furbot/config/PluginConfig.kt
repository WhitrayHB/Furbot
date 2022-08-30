package cn.whitrayhb.furbot.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.value


class PluginConfig {
    object Account : AutoSavePluginConfig("Account") {
        var account by value<String>()
        var password by value<String>()
        var apiToken by value<String>()
    }
}