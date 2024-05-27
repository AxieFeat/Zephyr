//package me.arial.zephyr.api.event.client;
//
//import com.esotericsoftware.kryonet.Connection;
//import me.arial.zephyr.api.client.KryoClient;
//import org.bukkit.event.Event;
//import org.bukkit.event.HandlerList;
//import org.jetbrains.annotations.NotNull;
//
//public class ClientReceiveEvent extends Event {
//
//    private static final HandlerList HANDLERS_LIST = new HandlerList();
//    private KryoClient client;
//    private Connection connection;
//    private Object packet;
//
//    public ClientReceiveEvent(KryoClient client, Connection connection, Object packet) {
//        super(true);
//        this.client = client;
//        this.connection = connection;
//        this.packet = packet;
//    }
//
//    @Override
//    public @NotNull HandlerList getHandlers() {
//        return HANDLERS_LIST;
//    }
//
//    public static HandlerList getHandlerList() {
//        return HANDLERS_LIST;
//    }
//
//    public KryoClient getClient() {
//        return client;
//    }
//
//    public Connection getConnection() {
//        return connection;
//    }
//
//    public Object getPacket() {
//        return packet;
//    }
//}
