package org.csstudio.util.importwizard.project;

import java.util.List;

/**
 * <code>ProjectDefinition</code>
 * is the representation of a plug-in or feature with its dependencies.</br> 
 * It contains the projectId, projectPath and a list of dependencies.
 *
 * @author <a href="mailto:andrej.babic@cosylab.com">Andrej Babiè</a>
 *
 */
public class ProjectDefinition {
	private final String projectId;
	private final String projectPath;
	private final List<String> projectDependencies;

	/**
	 * Constructor
	 * @param projectId ID of the plug in/feature
	 * @param projectPath Full path of the project
	 * @param projectDependencies List of projectIds that the project depends on.
	 */
	public ProjectDefinition(String projectId, String projectPath,List<String> projectDependencies) {

		if (projectId == null || projectPath == null) {
			throw new IllegalArgumentException(
					"Project name and project path cannot be null.\nProject name: "
							+ projectId + "\nProjectPath: " + projectPath);
		}

		this.projectId = projectId;
		this.projectPath = projectPath;
		this.projectDependencies = projectDependencies;
	}

	/**
	 * Get the project dependencies.
	 * @return List of all the dependencies projectId-s.
	 */
	public List<String> getProjectDependencies() {
		return projectDependencies;
	}

	/**
	 * Get the project id.
	 * @return The string representation of the projectId.
	 */
	public String getProjectId() {
		return projectId;
	}

	/**
	 * Get the project path.
	 * @return String of the project path.
	 */
	public String getProjectPath() {
		return projectPath;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((projectId == null) ? 0 : projectId.hashCode());
		result = prime * result
				+ ((projectPath == null) ? 0 : projectPath.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ProjectDefinition other = (ProjectDefinition) obj;
		if (projectId == null) {
			if (other.projectId != null)
				return false;
		} else if (!projectId.equals(other.projectId))
			return false;
		if (projectPath == null) {
			if (other.projectPath != null)
				return false;
		} else if (!projectPath.equals(other.projectPath))
			return false;
		return true;
	}

}
