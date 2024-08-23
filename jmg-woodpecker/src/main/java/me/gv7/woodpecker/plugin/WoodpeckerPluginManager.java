package me.gv7.woodpecker.plugin;

public class WoodpeckerPluginManager implements IPluginManager {
    public WoodpeckerPluginManager() {
    }

    @Override
    public void registerPluginManagerCallbacks(IPluginManagerCallbacks pluginManagerCallbacks) {
        pluginManagerCallbacks.registerHelperPlugin(new ShellHelperPlugin());
        pluginManagerCallbacks.registerHelperPlugin(new ProxyHelperPlugin());
        pluginManagerCallbacks.registerHelperPlugin(new ExtenderHelperPlugin());
    }
}
