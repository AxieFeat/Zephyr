package me.arial.zephyr.api.event.module;

import me.arial.zephyr.api.module.LoadedModule;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * Этот ивент происходит в момент выгрузки модуля Zephyr
 * <p>
 * Ивент выполняется в асинхронном потоке.
 */
public class ModuleUnloadEvent extends Event {
    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private LoadedModule module;

    public ModuleUnloadEvent(LoadedModule module) {
        super(true);
        this.module = module;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    /**
     * Получение выгружаемого модуля
     *
     * @return Объект LoadedModule
     */
    public LoadedModule getModule() {
        return module;
    }
}
