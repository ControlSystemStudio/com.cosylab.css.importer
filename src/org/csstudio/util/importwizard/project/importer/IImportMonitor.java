package org.csstudio.util.importwizard.project.importer;

/**
 * <code>IImportMonitor</code> 
 * provides an interface for implementing a import monitor. 
 * The import monitor can be assigned to the ProjectImporter in order to 
 * be notified when a project is imported.
 * 
 * @author <a href="mailto:andrej.babic@cosylab.com">Andrej Babiè</a>
 * 
 */
public interface IImportMonitor {
	/**
	 * When a project is successfully imported the monitor gets notified with the projectId.
	 * projectId.
	 * 
	 * @param projectId
	 */
	void notifyImportCompleted(String projectId);
}
