package org.cf.smalivm.smali;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.cf.util.ClassNameUtils;
import org.jf.dexlib2.iface.ClassDef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SmaliClassLoader extends ClassLoader {
  private static final Logger log = LoggerFactory.getLogger(SmaliClassLoader.class.getSimpleName());

  private static final String FRAMEWORK_STUBS_JAR = "/framework/android-23.jar";

  private static final Map<String, Class<?>> cachedClasses = new HashMap<String, Class<?>>();

  private final ClassBuilder classBuilder;

  private final ClassManager classManager;

  private final URLClassLoader jarLoader;

  public SmaliClassLoader(ClassManager classManager) {
    super(SmaliClassLoader.class.getClassLoader());
    URL jarURL = SmaliClassLoader.class.getResource(FRAMEWORK_STUBS_JAR);
    jarLoader = new URLClassLoader(new URL[] { jarURL });
    this.classBuilder = new ClassBuilder();
    this.classManager = classManager;
  }

  @Override protected Class<?> findClass(String name) throws ClassNotFoundException {
    try {
      return super.findClass(name);
    } catch (ClassNotFoundException e) {
    }
    if (name.startsWith("java.")) {
      log.warn("Unable to load prohibited class name: {}\nThis error is likely the result of using a class which references a java.* class only available on Android. There\'s no work-around at this time since loading protected classes is a huge pain.", name);
      throw new ClassNotFoundException(name);
    }
    Class<?> klazz = cachedClasses.get(name);
    if (klazz != null) {
      return klazz;
    }
    String internalName = ClassNameUtils.binaryToInternal(name);
    if (!classManager.isLocalClass(internalName)) {
      throw new ClassNotFoundException(name);
    }
    ClassDef classDef = classManager.getClass(internalName);
    byte[] b = classBuilder.build(classDef);
    klazz = defineClass(name, b, 0, b.length);
    cachedClasses.put(name, klazz);
    String packageName = getPackageName(name);
    if (packageName != null && getPackage(packageName) == null) {
      definePackage(getPackageName(name), null, null, null, null, null, null, null);
    }
    return klazz;
  }

  private static String getPackageName(String className) {
    int i = className.lastIndexOf('.');
    if (i > 0) {
      return className.substring(0, i);
    } else {
      return null;
    }
  }

  private void filterAvailableClasses(Set<String> classNames) {
    Iterator<String> iter = classNames.iterator();
    while (iter.hasNext()) {
      String className = iter.next();
      String baseName = ClassNameUtils.getComponentBase(className);
      String binaryName = ClassNameUtils.internalToBinary(baseName);
      if (loadClassWithoutBuilding(binaryName) != null) {
        iter.remove();
      }
    }
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  @Override public synchronized Class<?> loadClass(String name) throws ClassNotFoundException {
    Class<?> klazz = loadClassWithoutBuilding(name);
    if (klazz != null) {
      return klazz;
    }
    ClassDef classDef = classManager.getClass(ClassNameUtils.binaryToInternal(name));
    Set<String> classNames = ClassDependencyCollector.collect(classDef);
    filterAvailableClasses(classNames);
    Map<String, Class<?>> newClasses = classBuilder.build(classNames);
    cachedClasses.putAll(newClasses);
    klazz = findClass(name);
    if (klazz == null) {
      throw new ClassNotFoundException(name);
    }
    return klazz;
  }
>>>>>>> right/smalivm/src/main/java/org/cf/smalivm/smali/SmaliClassLoader.java
}