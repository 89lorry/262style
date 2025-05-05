import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.*;

public class JarClasses {
    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String jarPath = args[0];
        File jarFile = new File(jarPath);

        // load jar method: get
        URL jarURL = jarFile.toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{jarURL});

        JarFile jf = new JarFile(jarFile);
        Enumeration<JarEntry> entries = jf.entries();

        List<String> classNames = new ArrayList<>();

        // get .class
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();
            if (name.endsWith(".class")) {
                String className = name.replace("/", ".").replace(".class", "");
                classNames.add(className);
            }
        }

        Collections.sort(classNames);

        for (String className : classNames) {
            try {
                Class<?> clazz = classLoader.loadClass(className);
                Method[] methods = clazz.getDeclaredMethods();
                Field[] fields = clazz.getDeclaredFields();

                int publicCount = 0;
                int privateCount = 0;
                int protectedCount = 0;
                int staticCount = 0;

                for (Method method : methods) {
                    int modifiers = method.getModifiers();
                    if (Modifier.isPublic(modifiers)) publicCount++;
                    else if (Modifier.isPrivate(modifiers)) privateCount++;
                    else if (Modifier.isProtected(modifiers)) protectedCount++;
                    if (Modifier.isStatic(modifiers)) staticCount++;
                }

                System.out.println("---" + className + "---");
                System.out.println("Public methods: " + publicCount);
                System.out.println("Private methods: " + privateCount);
                System.out.println("Protected methods: " + protectedCount);
                System.out.println("Static methods: " + staticCount);
                System.out.println("Fields: " + fields.length);
            } catch (Throwable e) {

            }
        }

        jf.close();
    }
}
