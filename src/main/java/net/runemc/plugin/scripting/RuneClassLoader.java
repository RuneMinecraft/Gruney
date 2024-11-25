package net.runemc.plugin.scripting;


import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

public final class RuneClassLoader {
    private final static List<Class<?>> loadedClasses = new ArrayList<>();
    private final URLClassLoader classLoader;

    private RuneClassLoader(URL[] urls, ClassLoader parent) {
        this.classLoader = new URLClassLoader(urls, parent);
    }
    public static RuneClassLoader fromPluginDirectory(File directory, ClassLoader parent) throws IOException {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("The provided path is not a valid directory!");
        }
        URL[] urls = {directory.toURI().toURL()};
        return new RuneClassLoader(urls, parent);
    }
    public void compileJavaFiles(File directory) throws IOException {
        if (!directory.exists() || !directory.isDirectory()) {
            throw new IllegalArgumentException("The provided path is not a valid directory!");
        }

        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) {
            throw new IllegalStateException("Java Compiler not available. Ensure you are running on a JDK, not a JRE.");
        }

        List<File> javaFiles = new ArrayList<>();
        findJavaFiles(directory, javaFiles);

        if (javaFiles.isEmpty()) {
            throw new IllegalArgumentException("No .java files found in the directory: " + directory.getAbsolutePath());
        }

        try (StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null, null)) {
            Iterable<? extends javax.tools.JavaFileObject> compilationUnits =
                    fileManager.getJavaFileObjectsFromFiles(javaFiles);
            boolean success = compiler.getTask(null, fileManager, null, null, null, compilationUnits).call();
            if (!success) {
                throw new IOException("Failed to compile .java files.");
            }
        }
    }
    private void findJavaFiles(File directory, List<File> javaFiles) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                findJavaFiles(file, javaFiles);
            } else if (file.getName().endsWith(".java")) {
                javaFiles.add(file);
            }
        }
    }
    public Class<?> loadClass(String className) throws ClassNotFoundException {
        Class<?> clazz = classLoader.loadClass(className);
        loadedClasses.add(clazz);
        return clazz;
    }
    public void unloadClass(String className) {
        Class<?> clazz = loadedClasses.get(getByName(className));
        clazz.
    }
    public Object createInstance(String className) throws ReflectiveOperationException, ClassNotFoundException {
        Class<?> loadedClass = loadClass(className);
        return loadedClass.getDeclaredConstructor().newInstance();
    }

    public void unload() {
        try {
            classLoader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private int getByName(String className) {
        for (int i = 0; i < loadedClasses.size(); i++) {
            if (loadedClasses.get(i).getName().equals(className)) {
                return i;
            }
        }
        return -1;
    }
}