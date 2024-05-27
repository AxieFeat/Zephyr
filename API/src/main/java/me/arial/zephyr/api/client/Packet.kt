//package me.arial.zephyr.api.client
//
//import me.arial.zephyr.api.ZephyrPlugin
//import org.bukkit.Bukkit
//import java.io.Serializable
//import java.util.UUID
//
//data class Packet(
//    val key: String? = ZephyrPlugin.instance!!.key,
//    val hashSum: String = ZephyrPlugin.instance!!.hash,
//    val zephyrVersion: String = ZephyrPlugin.instance!!.zephyrVersion,
//    val session: String = ZephyrPlugin.instance!!.session.toString(),
//    val server: String = ZephyrPlugin.instance!!.server.version,
//    val hwid: String = ZephyrPlugin.instance!!.hwid,
//    val uuid: String = UUID.randomUUID().toString(),
//    val type: PacketType? = null,
//    val content: Array<Any?>? = emptyArray()
//) : Serializable {
//
//    fun send(): Packet {
//
//        Bukkit.getScheduler().runTaskAsynchronously(ZephyrPlugin.instance!!, Runnable {
//            ZephyrPlugin.instance!!.kryoClient!!.sendPacket(this)
//        })
//
//        return this
//    }
//
//    override fun equals(other: Any?): Boolean {
//        if (this === other) return true
//        if (javaClass != other?.javaClass) return false
//
//        other as Packet
//
//        if (key != other.key) return false
//        if (zephyrVersion != other.zephyrVersion) return false
//        if (session != other.session) return false
//        if (server != other.server) return false
//        if (hwid != other.hwid) return false
//        if (uuid != other.uuid) return false
//        if (type != other.type) return false
//        if (content != null) {
//            if (other.content == null) return false
//            if (!content.contentEquals(other.content)) return false
//        } else if (other.content != null) return false
//
//        return true
//    }
//
//    override fun hashCode(): Int {
//        var result = key?.hashCode() ?: 0
//        result = 31 * result + zephyrVersion.hashCode()
//        result = 31 * result + session.hashCode()
//        result = 31 * result + server.hashCode()
//        result = 31 * result + hwid.hashCode()
//        result = 31 * result + uuid.hashCode()
//        result = 31 * result + (type?.hashCode() ?: 0)
//        result = 31 * result + (content?.contentHashCode() ?: 0)
//        return result
//    }
//
//
//}