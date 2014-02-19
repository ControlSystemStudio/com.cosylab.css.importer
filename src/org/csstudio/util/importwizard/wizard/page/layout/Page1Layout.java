package org.csstudio.util.importwizard.wizard.page.layout;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.csstudio.util.importwizard.project.ProjectDefinition;
import org.csstudio.util.importwizard.wizard.CSSImportPresenter;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.osgi.service.prefs.Preferences;


/**
 * <code>Page1Layout</code>
 * is the layout used in {@link org.csstudio.util.importwizard.wizard.page.Page1 Page1}.
 *
 * @author <a href="mailto:andrej.babic@cosylab.com">Andrej Babiè</a>
 *
 */
public class Page1Layout extends Composite {
	private final String preferencesNode = this.getClass().getCanonicalName();
	private static final String node_lastPath = "lastUsedPath";
	
	private final Text textProjectsRootPath;
	private Table table;
	private final TableViewer tableViewer;
	private final Text textProjectFilter;
	private String currentProjectsPath;

	private final CSSImportPresenter presenter;
	private final WizardPage wizardPage;

	/**
	 * Constructor
	 * @param parent Parent Composite
	 * @param page Wizard page to attach the view to.
	 * @param importPresenter Presenter instance from the wizard.
	 */
	public Page1Layout(Composite parent, WizardPage page, CSSImportPresenter importPresenter) {
		super(parent, parent.getStyle());
		setLayout(new GridLayout(2, false));
		
		this.presenter = importPresenter;
		this.wizardPage = page;
		
		//  Select folder label.
		CLabel lblSelectRootFolder = new CLabel(this, SWT.NONE);
		lblSelectRootFolder.setText("Select root folder of your projects:");
		new Label(this, SWT.NONE);
		
		//  Path input box.
		textProjectsRootPath = new Text(this, SWT.BORDER);
		GridData gd_textProjectsRootPath = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		gd_textProjectsRootPath.widthHint = 371;
		textProjectsRootPath.setLayoutData(gd_textProjectsRootPath);
		
		// When enter is pressed the focus is lost.
		textProjectsRootPath.addKeyListener(new KeyListener() {
			
			@Override
			public void keyReleased(KeyEvent e) {
				if(e.keyCode ==SWT.CR || e.keyCode == 16777296){
					textProjectFilter.setFocus();
					
					// Load projects if path was modified.
					String path = textProjectsRootPath.getText();
					if(!path.equals(currentProjectsPath) && validPath(path)){
						loadTableData(path);
						textProjectFilter.setText("");
					}
				}
			}
			
			@Override
			public void keyPressed(KeyEvent e) {}
		});
		
		// Select path button.
		final Button buttonSelectFolder = new Button(this, SWT.NONE);
		buttonSelectFolder.setText("...");
		buttonSelectFolder.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				// Read the last used path.
				Preferences preferences = InstanceScope.INSTANCE.getNode(preferencesNode);
				String lastPath = preferences.get(node_lastPath, "C:\\Work\\cs-studio");
				
			    DirectoryDialog directoryDialog = new DirectoryDialog(buttonSelectFolder.getShell());
			    directoryDialog.setFilterPath(lastPath);
			    String path = directoryDialog.open();
			    
			    if(path != null){
			    	preferences.put(node_lastPath, path);
			    	textProjectsRootPath.setText(path);
			    	textProjectFilter.setFocus();
			    	
					if(!path.equals(currentProjectsPath) && validPath(path)){
						loadTableData(path);
					}
			    }else{
			    	textProjectsRootPath.setFocus();
			    }
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
		
		// Projects table label.
		CLabel lblSelectTheProjects = new CLabel(this, SWT.NONE);
		lblSelectTheProjects.setText("Select the projects you want to import:");
		new Label(this, SWT.NONE);
		
		// Projects filter text box.
		textProjectFilter = new Text(this, SWT.BORDER);
		textProjectFilter.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		textProjectFilter.setMessage("Filter projects...");
		textProjectFilter.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				String filterText = textProjectFilter.getText();
				if(filterText.length()>0){
					List<ProjectDefinition> filteredList = new ArrayList<ProjectDefinition>(presenter.getAllProjectsDefinition().size());
					for (ProjectDefinition projectDefinition:presenter.getAllProjectsDefinition()){
						if(projectDefinition.getProjectId().contains(filterText)){
							filteredList.add(projectDefinition);
						}
					}
					fillTableWithData(filteredList);
				}else{
					fillTableWithData(presenter.getAllProjectsDefinition());
				}
			}
		});
		new Label(this, SWT.NONE);
		
		// Projects table.
		tableViewer = new TableViewer(this, SWT.BORDER | SWT.CHECK | SWT.FULL_SELECTION);
		TableViewerColumn nameColumnViewer = new TableViewerColumn(tableViewer, parent.getStyle());
		TableColumn nameColumn = nameColumnViewer.getColumn();
		nameColumn.setWidth(410);
		nameColumn.setText("Project name");
		nameColumnViewer.setLabelProvider(new ColumnLabelProvider(){
			@Override
		      public String getText(Object element) {
		        ProjectDefinition projectDefinition = (ProjectDefinition) element;
		        return projectDefinition.getProjectId();
		      }
		});
		
		table = tableViewer.getTable();
		table.setLinesVisible(true);
		table.setHeaderVisible(true);
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());
		fillTableWithData(presenter.getAllProjectsDefinition());
		table.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				if(event.detail == SWT.CHECK){
					TableItem currentItem = (TableItem) event.item;
					
					if(currentItem.getChecked()){
						presenter.getSelectedProjects().add((ProjectDefinition)currentItem.getData());
					}else{
						presenter.getSelectedProjects().remove(currentItem.getData());
					}
					
					// At least one project needs to be selected.
					for(TableItem item :table.getItems()){
						if(item.getChecked()){
							wizardPage.setPageComplete(true);
							return;
						}
					}
					
					wizardPage.setPageComplete(false);
				}
			}
		});

		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
		new Label(this, SWT.NONE);
	}

	/**
	 * Load data from path into table.
	 * @param path Path on disk where the plug-ins/features are.
	 */
	protected void loadTableData(final String path) {
		currentProjectsPath = path;
		BusyIndicator.showWhile(getDisplay(), new Runnable() {
			
			@Override
			public void run() {
				try {
					presenter.loadProjectsFromPath(path);
					fillTableWithData(presenter.getAllProjectsDefinition());
				} catch (IOException e) {
					//TODO: Handle error and display it to the user.
				}
			}
		});
	}
	
	/**
	 * Check if path is valid on disk.
	 * @param path Path to validate.
	 * @return True, if path valid.
	 */
	private boolean validPath(String path) {
		File directory = new File(path);
		return directory.exists();
	}
	
	/**
	 * Fill the UI table with project definitions.
	 * @param projectDefinitions List of ProjectDefinition-s to display in the table.
	 */
	private void fillTableWithData(List<ProjectDefinition> projectDefinitions){
		// Set the new data.
		tableViewer.setInput(projectDefinitions);
		
		// If there are any selected projects.
		if(!presenter.getSelectedProjects().isEmpty()){
			Map<String, ProjectDefinition> projectsMap = presenter.getProjectsMap();
			
			// Check if the project in the current line is already selected, and check it.
			for(TableItem item: tableViewer.getTable().getItems()){
				if(presenter.getSelectedProjects().contains(projectsMap.get(item.getText()))){
					item.setChecked(true);
				}
			}
		}
	}
}
