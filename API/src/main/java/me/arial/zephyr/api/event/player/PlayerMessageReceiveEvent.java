package me.arial.zephyr.api.event.player;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Этот ивент срабатывает каждый раз, когда игрок получает любое сообщение
 */
public class PlayerMessageReceiveEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();

    private Boolean isCancelled = false;

    private Player player;
    private WrappedChatComponent wrappedChatComponent;

    public PlayerMessageReceiveEvent(Player player, WrappedChatComponent wrappedChatComponent) {
        this.player = player;
        this.wrappedChatComponent = wrappedChatComponent;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public WrappedChatComponent getWrappedChatComponent() {
        return wrappedChatComponent;
    }

    public void setWrappedChatComponent(WrappedChatComponent wrappedChatComponent) {
        this.wrappedChatComponent = wrappedChatComponent;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }
}
