package cn.whitrayhb.furbot.config

import net.mamoe.mirai.console.data.AutoSavePluginConfig
import net.mamoe.mirai.console.data.ValueDescription
import net.mamoe.mirai.console.data.value


class PluginConfig {
    object Account : AutoSavePluginConfig("Account") {
        @ValueDescription("账户名")
        var account by value<String>("")
        @ValueDescription("密码")
        var password by value<String>("")
        @ValueDescription("唯一Token")
        var apiToken by value<String>("")
    }
    object CoolDown : AutoSavePluginConfig("CoolDown") {
        @ValueDescription("来只<兽名>命令CD时长 设为-1则禁用CD")
        var furpicCD by value<Int>(10)
        @ValueDescription("随机兽图/来只兽命令CD时长")
        var randomFurpicCD by value<Int>(10)
        @ValueDescription("查只兽/查投稿命令CD时长")
        var queryFurpicCD by value<Int>(10)
        @ValueDescription("投只兽命令CD时长")
        var postFurCD by value<Int>(300)
    }
    object CloudBlacklist : AutoSavePluginConfig("CloudBlacklist") {
        @ValueDescription("趣绮梦云黑 APIKey")
        var apiKey by value<String>("")
    }
}