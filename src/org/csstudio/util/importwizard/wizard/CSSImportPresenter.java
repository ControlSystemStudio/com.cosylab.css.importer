package org.csstudio.util.importwizard.wizard;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.csstudio.util.importwizard.project.ProjectDefinition;
import org.csstudio.util.importwizard.project.importer.IImportMonitor;
import org.csstudio.util.importwizard.project.importer.ProjectImporter;
import org.csstudio.util.importwizard.project.provider.FSProjectDefinitionProvider;


/**
 * <code>CSSImportPresenter</code>
 * is the presenter class for the {@link org.csstudio.util.importwizard.wizard.CSSImportWizard CSSImportWizard}.</br>
 * It manages all the actions of the views.
 *
 * @author <a href="mailto:andrej.babic@cosylab.com">Andrej Babiè</a>
 *
 */
public class CSSImportPresenter {

	private List<ProjectDefinition> selectedProjects = new ArrayList<ProjectDefinition>();
	private List<ProjectDefinition> allProjectsDefinition = new ArrayList<ProjectDefinition>();
	private boolean importTestProjects = false;

	/**
	 * Import the test projects along with the regular projects as well.
	 * @return True = import the test projects.
	 */
	public boolean isImportTestProjects() {
		return importTestProjects;
	}

	/**
	 * Set if you want to import the test projects as well.
	 * @param importTestProjects True to import the test projects.
	 */
	public void setImportTestProjects(boolean importTestProjects) {
		this.importTestProjects = importTestProjects;
	}

	/**
	 * Returns the currently user selected projects.
	 * @return List of ProjectDefinitions the user has selected.
	 */
	public List<ProjectDefinition> getSelectedProjects() {
		return selectedProjects;
	}

	/**
	 * Return the list of all the currently loaded project definitions.
	 * @return List of all project definitions.
	 */
	public List<ProjectDefinition> getAllProjectsDefinition() {
		return allProjectsDefinition;
	}

	/**
	 * Load the projects from the specified path.
	 * @param path Path to load the projects.
	 * @throws IOException When error occurred while reading the files.
	 */
	public void loadProjectsFromPath(String path) throws IOException {
		selectedProjects.clear();
		allProjectsDefinition.clear();

		FSProjectDefinitionProvider projectDefinitionReader = new FSProjectDefinitionProvider(path);
		Map<String, ProjectDefinition> newProjectDefinitions = projectDefinitionReader.getProjectDefinitions();

		for (Map.Entry<String, ProjectDefinition> currentEntry : newProjectDefinitions.entrySet()) {
			allProjectsDefinition.add(currentEntry.getValue());
		}
	}

	/**
	 * Count the total number of project that will be imported.
	 * @param projectsToImport Project the user selected for import.
	 * @return Number of projects the user selected + their dependencies + test projects if so specified.
	 */
	public int countTotalProjectsToImport(List<ProjectDefinition> projectsToImport) {
		Map<String, ProjectDefinition> projectsMap = getProjectsMap();

		ProjectImporter projectImporter = new ProjectImporter(projectsMap);
		projectImporter.setImportTestProjects(importTestProjects);
		return projectImporter.getNumberOfProjectsToImport(projectsToImport);
	}

	/**
	 * Import the projects with their dependencies. It also imports their tests, if specified.
	 * @param projectsToImport Projects the user specified to import.
	 * @param importMonitor Import monitor. To be used for getting info of the import status.
	 */
	public void importProjects(List<ProjectDefinition> projectsToImport, IImportMonitor importMonitor) {
		Map<String, ProjectDefinition> projectsMap = getProjectsMap();

		ProjectImporter projectImporter = new ProjectImporter(projectsMap);
		projectImporter.setProgressMonitor(importMonitor);
		projectImporter.setImportTestProjects(importTestProjects);
		projectImporter.importProjectsWithDependencies(projectsToImport);
	}

	/**
	 * Return the Map of all the currently loaded project definitions.
	 * @return Map where the key=projectId, value=ProjectDefinition
	 */
	public Map<String, ProjectDefinition> getProjectsMap() {
		// We are using a HashMap because of its access time complexity.
		Map<String, ProjectDefinition> projectsMap = new HashMap<String, ProjectDefinition>();
		for (ProjectDefinition projectDefinition : allProjectsDefinition) {
			projectsMap.put(projectDefinition.getProjectId(), projectDefinition);
		}
		return projectsMap;
	}
}
