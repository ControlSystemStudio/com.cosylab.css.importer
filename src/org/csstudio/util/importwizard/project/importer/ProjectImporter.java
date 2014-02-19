package org.csstudio.util.importwizard.project.importer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.csstudio.util.importwizard.project.ProjectDefinition;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;


/**
 * <code>ProjectImporter</code>
 * imports projects into the Eclipse workspace.
 *
 * @author <a href="mailto:andrej.babic@cosylab.com">Andrej Babiè</a>
 *
 */
public class ProjectImporter {
	
	private static final String TEST_PROJECT_SUFFIX = ".test";
	
	private Map<String, ProjectDefinition> projectDefinitions;
	private boolean importTestProjects = false;
	private IImportMonitor progressMonitor;
	
	/**
	 * Project importer constructor.
	 * @param projectDefinitions Projects in Map<String(projectId), ProjectDefinition> format.
	 */
	public ProjectImporter(Map<String, ProjectDefinition> projectDefinitions){
		this.projectDefinitions = projectDefinitions;
	}
	
	/**
	 * Import also test projects, if available.
	 * @param importTestProjects True if you want the test projects.
	 */
	public void setImportTestProjects(boolean importTestProjects) {
		this.importTestProjects = importTestProjects;
	}
	
	/**
	 * Build a set off all the projectIds that need to be imported.
	 * @param projectId of the project you want to import.
	 * @param alreadyCountedProjectIds Set of already added projects.
	 */
	private void recursiveBuildProjectsDependencySet(String projectId, Set<String> alreadyCountedProjectIds){
		ProjectDefinition project = projectDefinitions.get(projectId);
		
		// Project does not exist in the path.
		if(project == null){
			return;
		}
		
		// Project and its dependencies already counted.
		if(!alreadyCountedProjectIds.add(projectId)){
			return;
		}
		
		// Recursion for every project dependency.
		for(String dependencyName:project.getProjectDependencies()){
			recursiveBuildProjectsDependencySet(dependencyName, alreadyCountedProjectIds);
		}
		
		// Count also test projects.
		if(importTestProjects){
			recursiveBuildProjectsDependencySet(projectId+TEST_PROJECT_SUFFIX, alreadyCountedProjectIds);
		}
	};
	
	/**
	 * Return the Set of all the projects and their dependencies that need to be imported.
	 * @param projectsToImport List of projects you want to import.
	 * @return Set off all the projects Set<String(projectId)> you have to import.
	 */
	private Set<String> getProjectsDependencySet(List<ProjectDefinition> projectsToImport) {
		Set<String> allProjectsToImport = new HashSet<String>();
		
		// Build recursively the set of projects that need to be imported.
		for(ProjectDefinition project:projectsToImport){
			recursiveBuildProjectsDependencySet(project.getProjectId(),allProjectsToImport);
		}
		return allProjectsToImport;
	}
	
	/**
	 * Returns the total number of projects to import.
	 * @param projectsToImport List of projects you want to import.
	 * @return The total number of project that need to be imported (specified projects + their dependencies)
	 */
	public int getNumberOfProjectsToImport(List<ProjectDefinition> projectsToImport){
		return getProjectsDependencySet(projectsToImport).size();
	}
	
	/**
	 * Import the provided projects with their dependencies.
	 * @param projectsToImport List of projectIds to import.
	 */
	public void importProjectsWithDependencies(List<ProjectDefinition> projectsToImport) {
		for(String projectId:getProjectsDependencySet(projectsToImport)){
			importProject(projectDefinitions.get(projectId));
		}
	}

	/**
	 * Import project into Eclipse workspace.
	 * @param projectToImport ProjectDefinition of the project you want to import.
	 */
	private void importProject(ProjectDefinition projectToImport) {
		//TODO: Where to notify the user about what is going on??
		System.out.println(projectToImport.getProjectId());
		
		try {
			// Load and import the project into workspace.
			IProjectDescription description = ResourcesPlugin.getWorkspace().loadProjectDescription(
					new Path(projectToImport.getProjectPath()));
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(description.getName());
			project.create(description, null);
			project.open(null);
			
			notifyProjectImported(projectToImport.getProjectId());
		}catch (CoreException e) {
			// TODO:Handle error.
			e.printStackTrace();
		}
	}
	
	/**
	 * Set the progress monitor. You will get a notification when a project is imported.
	 * @param progressMonitor Progress monitor that will get notified.
	 */
	public void setProgressMonitor(IImportMonitor progressMonitor) {
		this.progressMonitor = progressMonitor;
	}
	
	/**
	 * Notify the monitor a project was imported.
	 * @param projectId ProjectId of the imported project.
	 */
	private void notifyProjectImported(String projectId) {
		if(progressMonitor!=null){
			progressMonitor.notifyImportCompleted(projectId);
		}
	}
}
