package net.runemc.plugin.scripting;

import java.lang.reflect.Method;
import java.util.Arrays;

public class StaticWrapper {

    public Object callStatic(String className, String methodName, Object... args) {
        try {
            Class<?> clazz = Class.forName(className);
            Method method = Arrays.stream(clazz.getMethods())
                    .filter(m -> m.getName().equals(methodName))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchMethodException("Method not found: " + methodName));

            return method.invoke(null, args);
        } catch (Exception e) {
            throw new RuntimeException("Error invoking static method: " + className + "." + methodName, e);
        }
    }
}