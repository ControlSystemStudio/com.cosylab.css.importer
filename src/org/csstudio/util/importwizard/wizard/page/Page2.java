package org.csstudio.util.importwizard.wizard.page;

import org.csstudio.util.importwizard.wizard.CSSImportPresenter;
import org.csstudio.util.importwizard.wizard.page.layout.Page2Layout;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;


/**
 * <code>Page2</code>
 * represents the second page of the {@link org.csstudio.util.importwizard.wizard.CSSImportWizard CSSImportWizard}.</br>
 *
 * @author <a href="mailto:andrej.babic@cosylab.com">Andrej Babiè</a>
 *
 */
public class Page2 extends WizardPage {
	private final static String PAGE_TITLE = "Select the additional options.";
	private final CSSImportPresenter presenter;

	/**
	 * Constructor
	 * @param presenter Pass the presenter instance from the WizardPage.
	 */
	public Page2(CSSImportPresenter presenter) {
		super(PAGE_TITLE);
		this.presenter = presenter;
	}

	@Override
	public void createControl(Composite parent) {
		Page2Layout pageLayout = new Page2Layout(parent, presenter);
		setControl(pageLayout);
	    
	}
}
