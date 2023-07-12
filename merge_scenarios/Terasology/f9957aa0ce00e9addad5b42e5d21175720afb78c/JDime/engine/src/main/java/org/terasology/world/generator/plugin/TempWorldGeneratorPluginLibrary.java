package org.terasology.world.generator.plugin;
import org.terasology.context.Context;
import org.terasology.module.ModuleEnvironment;

/**
 * A fake environment so that plugins can be loaded for configuration.
 * @author Immortius
 */
public class TempWorldGeneratorPluginLibrary extends DefaultWorldGeneratorPluginLibrary {
  public TempWorldGeneratorPluginLibrary(
<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/left/engine/src/main/java/org/terasology/world/generator/plugin/TempWorldGeneratorPluginLibrary.java
  ModuleEnvironment environment
=======
  Context context
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/right/engine/src/main/java/org/terasology/world/generator/plugin/TempWorldGeneratorPluginLibrary.java
  ) {
    super(
<<<<<<< /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/left/engine/src/main/java/org/terasology/world/generator/plugin/TempWorldGeneratorPluginLibrary.java
    environment
=======
    getEnv(context)
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/right/engine/src/main/java/org/terasology/world/generator/plugin/TempWorldGeneratorPluginLibrary.java
    , context);
  }


<<<<<<< Unknown file: This is a bug in JDime.
=======
  private static ModuleEnvironment getEnv(Context context) {
    ModuleManager moduleManager = context.get(ModuleManager.class);
    AssetManager assetManager = context.get(AssetManager.class);
    Config config = context.get(Config.class);
    Set<Module> selectedModules = Sets.newHashSet();
    for (Name moduleName : config.getDefaultModSelection().listModules()) {
      Module module = moduleManager.getRegistry().getLatestModuleVersion(moduleName);
      if (module != null) {
        selectedModules.add(module);
        for (DependencyInfo dependencyInfo : module.getMetadata().getDependencies()) {
          selectedModules.add(moduleManager.getRegistry().getLatestModuleVersion(dependencyInfo.getId()));
        }
      }
    }
    ModuleEnvironment environment = moduleManager.loadEnvironment(selectedModules, false);
    assetManager.setEnvironment(environment);
    return environment;
  }
>>>>>>> /home/ppp/Research_Projects/Merge_Conflicts/Resource/workspace/right/engine/src/main/java/org/terasology/world/generator/plugin/TempWorldGeneratorPluginLibrary.java
}