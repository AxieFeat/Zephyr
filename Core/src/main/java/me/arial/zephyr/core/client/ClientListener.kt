//package me.arial.zephyr.core.client
//
//import me.arial.zephyr.api.ZephyrPlugin
//import me.arial.zephyr.api.client.Packet
//import me.arial.zephyr.api.client.PacketType
//import me.arial.zephyr.api.event.client.ClientDisconnectEvent
//import me.arial.zephyr.api.event.client.ClientReceiveEvent
//import me.arial.zephyr.core.Zephyr
//import org.bukkit.Bukkit
//import org.bukkit.event.EventHandler
//import org.bukkit.event.HandlerList
//import org.bukkit.event.Listener
//
//class ClientListener : Listener {
//
//    init {
//        Bukkit.getPluginManager().registerEvents(
//            this, ZephyrPlugin.instance
//        )
//    }
//
//    @EventHandler
//    fun onPacketReceive(e: ClientReceiveEvent) {
//        if (e.packet is Packet) {
//            val packet = e.packet as Packet
//
//            when(packet.type) {
//                PacketType.KILL -> {
//                    val name = packet.content!![0]
//                    val reason = packet.content!![1] as String
//
//                    if (!Zephyr.instance.zephyrModuleManager!!.unloadModule(
//                        Zephyr.instance.zephyrModuleManager!!.getModule(
//                            if (name is String) name else null
//                        )
//                    )) {
//                        HandlerList.unregisterAll(this)
//                        Zephyr.instance.stop(reason)
//                    } else {
//                        Zephyr.instance.logger.severe(reason)
//                    }
//                }
//                else -> {
//                    val status = packet.content!![0] as Boolean
//
//                    if (!status) {
//                        Zephyr.instance.stop("Stop reason = \"${packet.content!![1]}\"")
//                        HandlerList.unregisterAll(this)
//                    }
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    fun onDisconnect(e: ClientDisconnectEvent) {
//        Zephyr.instance.stop("Disconnected!")
//    }
//}