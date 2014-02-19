package org.csstudio.util.importwizard.wizard.page;

import org.csstudio.util.importwizard.wizard.CSSImportPresenter;
import org.csstudio.util.importwizard.wizard.page.layout.Page1Layout;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;


/**
 * <code>Page1</code>
 * represents the first page of the {@link org.csstudio.util.importwizard.wizard.CSSImportWizard CSSImportWizard}.</br>
 *
 * @author <a href="mailto:andrej.babic@cosylab.com">Andrej Babiè</a>
 *
 */
public class Page1 extends WizardPage{
	private final static String PAGE_TITLE = "Select the projects you want to import.";
	private final CSSImportPresenter presenter;

	/**
	 * Constructor
	 * @param presenter Pass the presenter instance from the WizardPage.
	 */
	public Page1(CSSImportPresenter presenter) {
		super(PAGE_TITLE);
		this.presenter = presenter;
	}

	@Override
	public void createControl(Composite parent) {
		Page1Layout pageLayout = new Page1Layout(parent, this, presenter);
		
		setControl(pageLayout);
	    setPageComplete(false);
	}
}
