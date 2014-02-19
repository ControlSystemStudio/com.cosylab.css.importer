package org.csstudio.util.importwizard.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.csstudio.util.importwizard.project.ProjectDefinition;
import org.csstudio.util.importwizard.project.importer.IImportMonitor;
import org.csstudio.util.importwizard.wizard.page.Page1;
import org.csstudio.util.importwizard.wizard.page.Page2;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IImportWizard;
import org.eclipse.ui.IWorkbench;


/**
 * <code>CSSImportWizard</code>
 * is a {@link org.eclipse.ui.IImportWizard IImportWizard} implementation used to import CSS plug-in/feature projects into a workspace.
 *
 * @author <a href="mailto:andrej.babic@cosylab.com">Andrej Babiè</a>
 *
 */
public class CSSImportWizard extends Wizard implements IImportWizard {
	private final static String WINDOW_TITLE = "Welcome to the CSS project import wizard.";
	private final CSSImportPresenter presenter = new CSSImportPresenter();

	private Page1 page1;
	private Page2 page2;

	/**
	 * Default constructor for the import wizard.
	 */
	public CSSImportWizard() {
		super();
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		page1 = new Page1(presenter);
		addPage(page1);
		
		page2 = new Page2(presenter);
		addPage(page2);
	}

	@Override
	public boolean performFinish() {

		try {
			new ProgressMonitorDialog(getShell()).run(true, false, new IRunnableWithProgress() {
				
				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException,
						InterruptedException {
					
					List<ProjectDefinition> projectsToImport = new ArrayList<ProjectDefinition>(presenter.getSelectedProjects());

					// Setup the progress dialog and start the import.
					monitor.beginTask("Importing selected project into workspace.", presenter.countTotalProjectsToImport(projectsToImport));
					
					presenter.importProjects(projectsToImport,new IImportMonitor() {
						@Override
						public void notifyImportCompleted(String projectId) {
							monitor.worked(1);
							monitor.subTask(projectId);
						}
					});
				}
			});
		} catch (InvocationTargetException e) {
			// TODO Notify user for error
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Notify user for error
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		setWindowTitle(WINDOW_TITLE);
		setNeedsProgressMonitor(true);
	}

}
