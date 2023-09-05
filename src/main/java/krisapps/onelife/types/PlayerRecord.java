package krisapps.onelife.types;

import java.util.UUID;

public class PlayerRecord {

    private UUID uuid;
    private String playerName;

    public PlayerRecord(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
    }
}
