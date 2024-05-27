//package me.arial.zephyr.api.client
//
//import com.esotericsoftware.kryo.Kryo
//import com.esotericsoftware.kryonet.Client
//import com.esotericsoftware.minlog.Log
//import me.arial.zephyr.api.ZephyrPlugin
//import org.bukkit.Bukkit
//import java.io.IOException
//import java.util.UUID
//
//class KryoClient(
//    val host: String,
//    val port: Int
//) {
//
//    var client: Client? = null
//
//    val ping: Int
//        get() = client!!.returnTripTime
//
//    init {
//        try {
//            client = Client()
//            Log.set(6)
//            register(client!!.kryo)
//            client!!.start()
//            client!!.addListener(KryoListener(this))
//            client!!.connect(5000, host, port, port)
//        } catch (ignore: IOException) {}
//
////        client!!.sendTCP(Packet(type = PacketType.AUTH, content = arrayOf(System.currentTimeMillis())))
////
////        Bukkit.getScheduler().runTaskTimerAsynchronously(ZephyrPlugin.instance!!, Runnable {
////            client!!.sendTCP(Packet(type = PacketType.PING, content = arrayOf(System.currentTimeMillis())))
////        }, 1L, 1200L)
//    }
//
//    private fun register(kryo: Kryo) {
//        kryo.register(String::class.java)
//        kryo.register(Boolean::class.java)
//        kryo.register(Int::class.java)
//        kryo.register(Packet::class.java)
//        kryo.register(Array::class.java)
//        kryo.register(List::class.java)
//        kryo.register(PacketType::class.java)
//        kryo.register(Array<out String>::class.java)
//    }
//
//    /**
//     * Отправляет пакет по протоколу TCP
//     *
//     * @param obj Объект для отправки
//     */
//    fun sendPacket(obj: Any) {
////        if (client == null || !client!!.isConnected) {
////            ZephyrPlugin.instance!!.stop("Unknown packet!")
////            return
////        }
////
////        client!!.sendTCP(obj)
//    }
//}