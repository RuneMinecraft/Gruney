package net.runemc.plugin.scripting;

import net.runemc.plugin.Main;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import java.io.File;
import java.nio.file.Files;

public class Script extends Executable {
    private final String name;
    private final String content;
    private final Value sharedBindings;
    private boolean loaded;

    public Script(String name, Value sharedBindings) throws Exception {
        this.name = name;
        File scriptFile = new File(Main.get().getDataFolder(), name);
        this.content = Files.readString(scriptFile.toPath());
        this.sharedBindings = sharedBindings;
        this.loaded = false;
    }

    @Override
    public void run() {
        if (!loaded) {
            throw new IllegalStateException("Script must be loaded before execution.");
        }

        try {
            Context context = Context.getCurrent();
            context.getBindings("js").putMember("Shared", sharedBindings);
            context.eval("js", content);
        } catch (Exception e) {
            throw new RuntimeException("Script execution failed: " + e.getMessage(), e);
        }
    }

    @Override
    public int load() {
        if (loaded) {
            return ErrorCode.SCRIPT_ALREADY_LOADED;
        }
        try {
            sharedBindings.putMember("Script_" + this.name, content);
            loaded = true;
            return ErrorCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.ERROR_DURING_LOADING;
        }
    }

    @Override
    public int execute() {
        if (!loaded) {
            return ErrorCode.SCRIPT_NOT_LOADED;
        }
        try {
            run();
            return ErrorCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.ERROR_DURING_EXECUTION;
        }
    }

    @Override
    public int unload() {
        if (!loaded) {
            return ErrorCode.SCRIPT_NOT_LOADED;
        }
        try {
            sharedBindings.removeMember("Script_" + name);
            loaded = false;
            return ErrorCode.SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ErrorCode.ERROR_DURING_UNLOADING;
        }
    }

    public void runThreaded() {
        if (!loaded) {
            throw new IllegalStateException("Script must be loaded before execution.");
        }
        run();
    }

    public String name() {
        return this.name;
    }
    public String content() {
        return this.content;
    }
    public Value sharedBindings() {
        return this.sharedBindings;
    }
}