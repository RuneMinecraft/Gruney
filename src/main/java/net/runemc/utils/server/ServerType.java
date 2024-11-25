package net.runemc.utils.server;

public enum ServerType {
    GLOBAL,
    HUB,
    SURVIVAL,
    SKYBLOCK;

    @Override
    public String toString() {
        return this.name();
    }
}
