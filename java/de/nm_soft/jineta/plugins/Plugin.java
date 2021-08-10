package de.nm_soft.jineta.plugins;

/**
  * Most of the special functions for jineta are implemented as plugins.
  * This file defines the interface of a plugin so jineta recognizes the class
  * implementing this interface as a jineta plugin.
  * Nonetheless all plugins must be contained within package
  * de.nm_soft.jineta.plugins.
  */


/** Interface definition for jineta plugins.
  */
public interface Plugin {
  /** The Package name: "de.nm_soft.jineta.plugins" */
  String PACKAGE = "de.nm_soft.jineta.plugins";
  String INTERFACE = PACKAGE.concat(".Plugin");

  /** This Plugin may only be used on Windows platform */
  boolean WINDOWSONLY = true;
  /** This Plugin may be used on any platform */
  boolean ANYSYSTEM = false;

  /** Get Type of Plugin.
    * @return the Plugin-Type based on enum EPlugin
    * @see #EPlugin
    */
  abstract public EPlugin getPluginType();

  /** Get Platform capabilities of Plugin.
    * @return WINDOWSONLY if plugin may only be run on Windows platform or ANYSYSTEM otherwise.
    * @see #WINDOWSONLY
    */
  abstract public boolean getWindowsOnly();

  /** The implementation of AbstractAction returned is used
    * to create a JMenuItem for this plugin.
    * The JMenuItem is added to Import, Export, Edit or Extras Menu dependent on
    * result of getPluginType().
    * @return a valid instance based on AbstractAction.
    * @see #getPluginType()
    */
  abstract public javax.swing.AbstractAction getAction(de.nm_soft.jineta.VMainWindow owner);

  /** The implementation of KeyStroke returns is used as a keyboard accelerator
    * for this plugin.
    * @return a valid instance of KeyStroke.
    */
  abstract public javax.swing.KeyStroke getAccelerator();
}
