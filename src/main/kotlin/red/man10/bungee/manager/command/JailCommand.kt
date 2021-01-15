package red.man10.bungee.manager.command

import net.md_5.bungee.api.CommandSender
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.chat.ComponentBuilder
import net.md_5.bungee.api.plugin.Command
import red.man10.bungee.manager.Man10BungeePlugin.Companion.playerDataDic
import red.man10.bungee.manager.Man10BungeePlugin.Companion.plugin
import java.text.SimpleDateFormat

object JailCommand : Command("mjail","bungeemanager.jail"){

    override fun execute(sender: CommandSender?, args: Array<out String>?) {

        if (sender==null)return

        //mjail <forest611> <100d> <reason>
        if (!args.isNullOrEmpty() && args.size == 3){

            val p = args[0]

            val pData = ProxyServer.getInstance().getPlayer(args[0])

            val data = playerDataDic[pData.uniqueId]

            if (data ==null){
                sender.sendMessage(*ComponentBuilder("§4オフラインのユーザーです").create())
                return
            }

            val unit = args[1][args[1].length - 1]

            val time: Int
            try {
                time = args[1].replace(unit.toString(), "").toInt()
            } catch (e: Exception) {
                sender.sendMessage(*ComponentBuilder("§c§l時間の指定方法が不適切です").create())
                return
            }

            if (!data.isJailed() && time <0){
                sender.sendMessage(*ComponentBuilder("§c§lこのユーザーは既に釈放されています！").create())
                return
            }

            when(unit){

                'd' ->data.addJailTime(0,0,time)
                'h' ->data.addJailTime(0,time,0)
                'm' ->data.addJailTime(time,0,0)
                'k' ->data.addJailTime(0,0,278250)

                else -> {
                    sender.sendMessage(*ComponentBuilder("§c§l時間の指定方法が不適切です").create())
                    return
                }

            }

            if (!data.isJailed()){
                ProxyServer.getInstance().broadcast(*ComponentBuilder("§c§l${p}は釈放されました").create())
                playerDataDic[data.uuid] = data
                return
            }

            ProxyServer.getInstance().broadcast(*ComponentBuilder("§c§l${p}は「${args[2]}」の理由により、Jailされました！").create())
            ProxyServer.getInstance().broadcast(*ComponentBuilder("§c§l釈放日:${SimpleDateFormat("yyyy/MM/dd").format(data.jailUntil)}").create())
            if (unit == 'k'){
                ProxyServer.getInstance().broadcast(*ComponentBuilder("§c1050年地下行きっ・・・・・・・・！").create())
            }
            playerDataDic[data.uuid] = data

            plugin.sendToJail(pData)

            return

        }

        sender.sendMessage(*ComponentBuilder("§d§l/mjail <mcid> <期間+(d/h/m/0k)> <Jail理由>").create())
        sender.sendMessage(*ComponentBuilder("§d§l期間をマイナスにすると期間が縮みます").create())

    }

}