//package me.arial.zephyr.api.client
//
//import com.esotericsoftware.kryonet.Connection
//import com.esotericsoftware.kryonet.FrameworkMessage.KeepAlive
//import com.esotericsoftware.kryonet.Listener
//import me.arial.zephyr.api.event.client.ClientConnectEvent
//import me.arial.zephyr.api.event.client.ClientDisconnectEvent
//import me.arial.zephyr.api.event.client.ClientReceiveEvent
//import org.bukkit.Bukkit
//
//class KryoListener(
//    private val client: KryoClient
//) : Listener() {
//    override fun connected(connection: Connection?) {
//        val event = ClientConnectEvent(client, connection)
//        Bukkit.getPluginManager().callEvent(event)
//    }
//
//    override fun disconnected(connection: Connection?) {
//        val event = ClientDisconnectEvent(client, connection)
//        Bukkit.getPluginManager().callEvent(event)
//    }
//
//    override fun received(connection: Connection?, `object`: Any?) {
//        if (`object` is KeepAlive) {
//            return
//        }
//        val event = ClientReceiveEvent(client, connection, `object`)
//        Bukkit.getPluginManager().callEvent(event)
//    }
//}