package krisapps.onelife.events;

import krisapps.onelife.types.DepletionContext;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class LifeDepleteEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private final DepletionContext depletionContext;
    private final int livesLeft;

    public LifeDepleteEvent(Player player, DepletionContext depletionContext, int livesLeft) {
        this.player = player;
        this.depletionContext = depletionContext;
        this.livesLeft = livesLeft;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public Player getPlayer() {
        return player;
    }

    public DepletionContext getDepletionContext() {
        return depletionContext;
    }

    public int getLivesLeft() {
        return livesLeft;
    }
}
