package net.runemc.plugin.scripting;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;

import javax.script.Bindings;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ScriptBindings {
    private final Map<String, Script> scripts;
    private Value sharedBindings;

    public ScriptBindings() {
        this.scripts = new ConcurrentHashMap<>();
        this.sharedBindings = createDefaultBindings();
    }

    public Value sharedBindings() {
        return this.sharedBindings;
    }
    public ScriptBindings setSharedBindings(Value sharedBindings) {
        this.sharedBindings = sharedBindings;
        return this;
    }

    public Script getScript(String name) {
        return scripts.get(name);
    }
    public void addScript(String name, Script script) {
        scripts.put(name, script);
    }
    public Script removeScript(String name) {
        return scripts.remove(name);
    }
    public boolean isScriptLoaded(String name) {
        return scripts.containsKey(name);
    }
    public Map<String, Script> getAllScripts() {
        return scripts;
    }

    private Value createDefaultBindings() {
        Context context = Context.newBuilder("js").allowAllAccess(true).build();

        Value defaultBindings = context.getBindings("js");
        defaultBindings.putMember("default", "Hello from the default bindings!");

        return defaultBindings;
    }
}