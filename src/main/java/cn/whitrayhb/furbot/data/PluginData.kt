package cn.whitrayhb.furbot.data

import net.mamoe.mirai.console.data.AutoSavePluginData
import net.mamoe.mirai.console.data.value

class PluginData {
    object Cookie : AutoSavePluginData("Cookie") {
        var phpsessionid by value<String>()
        var token by value<String>()
        var user by value<String>()
    }
}
