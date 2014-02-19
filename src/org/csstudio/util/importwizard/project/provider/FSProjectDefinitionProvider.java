package org.csstudio.util.importwizard.project.provider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.csstudio.util.importwizard.project.ProjectDefinition;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * <code>FSProjectDefinitionProvider</code>
 * reads {@link org.csstudio.util.importwizard.project.ProjectDefinition ProjectDefinition}s from the file system.
 *
 * @author <a href="mailto:andrej.babic@cosylab.com">Andrej Babiè</a>
 *
 */
public class FSProjectDefinitionProvider extends SimpleFileVisitor<Path> implements IProjectsDefinitionProvider {
	private static final String NAME_MANIFEST_DEPENDENCY = "Require-Bundle";
	private static final String FILENAME_PROJECT = ".project";
	private static final String FILENAME_FEATURE = "feature.xml";
	private static final String FILENAME_MANIFEST = "META-INF/MANIFEST.MF";
	
	private Map<String,ProjectDefinition> projectDefinitions = new HashMap<String, ProjectDefinition>();
	
	/**
	 * Constructs the ProjectDefinition file system reader.
	 * @param path Must be non-empty. Specify root path from which the recursive scan will start.
	 * @throws IOException In case of file system read error.
	 */
	public FSProjectDefinitionProvider(String path) throws IOException{
		if(path == null || path.isEmpty()){
			throw new IllegalArgumentException("Path cannot be null or empty.");
		}
	
		Files.walkFileTree(Paths.get(path), this);
	}
	
	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attr) throws FileNotFoundException, IOException{
		// We found a project.
		if(file.getFileName().endsWith(FILENAME_PROJECT) && attr.isRegularFile()){
			try {
				String projectPath = file.toString();
				ArrayList<String> dependenciesInCurrentProject = new ArrayList<String>();
				
				// Read project description.
				IProjectDescription projectDescription = ResourcesPlugin.getWorkspace().loadProjectDescription(new FileInputStream(projectPath));
				String projectName = projectDescription.getName();
				
				// Read feature description if exists (if it's a feature).
				Path featurePath = file.getParent().resolve(FILENAME_FEATURE);
				if(Files.exists(featurePath)){
					processFeatureDescriptor(dependenciesInCurrentProject,featurePath);
				}
				
				// Read project manifest if exists.
				Path manifestPath = file.getParent().resolve(FILENAME_MANIFEST);
				if(Files.exists(manifestPath)){
					processPluginManifest(dependenciesInCurrentProject,manifestPath);
				}
				
				projectDefinitions.put(projectName, new ProjectDefinition(projectName, projectPath, dependenciesInCurrentProject));
			} catch (CoreException e) {
				throw new IOException("There was a problem while reading the project description: "+file.toAbsolutePath().toString(),e);
			}
		}
		return FileVisitResult.CONTINUE;
	}
	
	/**
	 * Process the feature description XML.
	 * @param dependenciesInCurrentProject A list of projectIds dependencies of the current project to be populated.
	 * @param featurePath Location on disk of the feature XML
	 * @throws IOException Error while reading the file.
	 */
	private void processFeatureDescriptor(ArrayList<String> dependenciesInCurrentProject, Path featurePath) throws IOException {
		File featureXml = featurePath.toFile();
		try {
			// Read the XML.
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(featureXml);
			doc.getDocumentElement().normalize();
			
			//For each Import
			NodeList imports = doc.getElementsByTagName("import");
			
			for(int i=0;i<imports.getLength();i++){
				Element currentImport = (Element) imports.item(i);
				
				// Normal plug in dependency.
				String pluginName = currentImport.getAttribute("plugin");
				if(!pluginName.isEmpty()){
					dependenciesInCurrentProject.add(pluginName);
					//TODO: Notify user is log?
					System.out.println("IMPORT PLUGIN: "+pluginName);
					continue;
				}
				
				// Feature dependency.
				String featureName = currentImport.getAttribute("feature");
				if(!featureName.isEmpty()){
					dependenciesInCurrentProject.add(featureName);
					//TODO: Notify user is log?
					System.out.println("IMPORT FEATURE: "+featureName);
					continue;
				}
			}
			
			// For each plug in.
			NodeList plugins = doc.getElementsByTagName("plugin");
			for(int i=0;i<plugins.getLength();i++){
				Element currentPlugin = (Element) plugins.item(i);
				
				// Plug ins of the feature.
				String pluginName = currentPlugin.getAttribute("id");
				if(!pluginName.isEmpty()){
					dependenciesInCurrentProject.add(pluginName);
					System.out.println("PLUGIN: "+pluginName);
				}
			}
			
			// For each include.
			NodeList includes = doc.getElementsByTagName("includes");
			for(int i=0;i<includes.getLength();i++){
				Element currentInclude = (Element) includes.item(i);
				
				//  Include dependency.
				String includeName = currentInclude.getAttribute("id");
				if(!includeName.isEmpty()){
					dependenciesInCurrentProject.add(includeName);
					System.out.println("INCLUDE: "+includeName);
				}
			}
			
			
		} catch (SAXException e) {
			//TODO: Notify user or skip?
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			//TODO: Notify user or skip?
			e.printStackTrace();
		}
	}

	/**
	 * Process the plug-in MANIFEST file.
	 * @param dependenciesInCurrentProject A list of projectIds dependencies of the current project to be populated.
	 * @param manifestPath Location on disk of the manifest.
	 * @throws IOException Possible exception in reading the file.
	 * @throws FileNotFoundException If wrong path is supplied.
	 */
	private void processPluginManifest(ArrayList<String> dependenciesInCurrentProject, Path manifestPath) throws IOException, FileNotFoundException {
		// Read the manifest and extract the dependencies in CSV format.
		Manifest currentManifest = new Manifest(new FileInputStream(manifestPath.toString()));
		Attributes attributes = currentManifest.getMainAttributes();
		String dependenciesCSV = attributes.getValue(NAME_MANIFEST_DEPENDENCY);
		
		if(dependenciesCSV !=null && !dependenciesCSV.isEmpty()){
			// Split and add the dependencies from the manifest.
			for(String dependency:dependenciesCSV.split(",")){			
				dependenciesInCurrentProject.add(dependency.split(";")[0]);
			}
		}
	}

	@Override
	public Map<String, ProjectDefinition> getProjectDefinitions() {
		return projectDefinitions;
	}
	
}
