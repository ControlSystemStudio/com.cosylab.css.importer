This plugin provides an easy way to import CS-Studio plugins into your Eclipse workspace. You select the plugin (or plugins) you want to import, and all of the required dependencies will also be imported.

The plugin's code depends only on standard Eclipse plugins, and should build without error in Eclipse 4.4 for RCP and RAP Developers.

To build the plugin, first import it into your Eclipse workspace:

* File → Import...
* General → Existing Projects into Workspace
* Browse to and select your clone of the `com.cosylab.css.importer` repository
* Ensure `org.csstudio.util.importwizard` is ticked, and click Finish

Then install the plugin into your Eclipse IDE:

* Right-click the `org.csstudio.util.importwizard` project in the Package Explorer view
* Export...
* Plug-in Development → Deployable plug-ins and fragments
* Ensure `org.csstudio.util.importwizard` is ticked
* Choose "Install into host"
* Click Finish
* If prompted about installing unsigned content, allow the installation to continue
* Restart Eclipse when prompted

To import a CS-Studio plugin:
* File → Import...
* Other → Existing project with dependencies into Workspace
* Choose the root folder beneath which the projects are located
* Filter the projects by name, if desired
* Tick the projects you wish to import
* Click Finish
