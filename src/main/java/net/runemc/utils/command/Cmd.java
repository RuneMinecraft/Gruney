package net.runemc.utils.command;

import net.runemc.utils.server.ServerType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Cmd {
    ServerType server() default ServerType.GLOBAL;
    String[] names();
    String[] perms() default "default";
    boolean disabled() default false;
    boolean playerOnly() default false;
}
