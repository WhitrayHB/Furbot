package cn.whitrayhb.furbot.command

import cn.whitrayhb.furbot.FurbotMain
import net.mamoe.mirai.console.command.SimpleCommand
import net.mamoe.mirai.console.command.descriptor.ExperimentalCommandDescriptors
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

object LoginAPI : SimpleCommand(
    FurbotMain.INSTANCE,
    primaryName = "login-api" ,
    secondaryNames = arrayOf("登录接口"),
    description = "登录接口"
){
    @ExperimentalCommandDescriptors
    @ConsoleExperimentalApi
    override val prefixOptional = false;
}