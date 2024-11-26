package net.runemc.plugin.scripting;

public abstract class Executable {
    public abstract void run();

    public abstract int load();
    public abstract int execute();
    public abstract int unload();
}
