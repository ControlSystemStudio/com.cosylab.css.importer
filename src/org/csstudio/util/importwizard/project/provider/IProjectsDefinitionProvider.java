package org.csstudio.util.importwizard.project.provider;

import java.util.Map;

import org.csstudio.util.importwizard.project.ProjectDefinition;


/**
 * <code>IProjectsDefinitionProvider</code> 
 * specifies the methods a {@link org.csstudio.util.importwizard.project.ProjectDefinition ProjectDefinition} provider should have.
 * Implementations: {@link org.csstudio.util.importwizard.project.provider.FSProjectDefinitionProvider FSProjectDefinitionProvider}
 * 
 * @author <a href="mailto:andrej.babic@cosylab.com">Andrej Babiè</a>
 * 
 */
public interface IProjectsDefinitionProvider {
	/**
	 * Return a map of the available {@link org.csstudio.util.importwizard.project.ProjectDefinition ProjectDefinition}s.
	 * 
	 * @return Map key=projectId, value=ProjectDefinition.
	 */
	public Map<String, ProjectDefinition> getProjectDefinitions();
}
