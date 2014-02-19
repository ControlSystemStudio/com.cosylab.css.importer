package org.csstudio.util.importwizard.wizard.page.layout;

import org.csstudio.util.importwizard.wizard.CSSImportPresenter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


/**
 * <code>Page2Layout</code>
 * is the layout used in {@link org.csstudio.util.importwizard.wizard.page.Page2 Page2}.
 *
 * @author <a href="mailto:andrej.babic@cosylab.com">Andrej Babiè</a>
 *
 */
public class Page2Layout extends Composite {

	private final CSSImportPresenter presenter;

	/**
	 * Constructor
	 * @param parent Parent Composite
	 * @param importPresenter Presenter instance from the wizard.
	 */
	public Page2Layout(Composite parent, CSSImportPresenter importPresenter) {
		super(parent, parent.getStyle());
		this.presenter = importPresenter;

		// Layout.
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
	    setLayout(layout);
	    
	    // Import test projects label.
	    Label labelCheck = new Label(this, SWT.NONE);
	    labelCheck.setText("Import also test projects");
	    
	    // Import test projects check box.
	    Button check = new Button(this, SWT.CHECK);
	    check.setSelection(false);
	    check.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				presenter.setImportTestProjects(((Button) event.widget).getSelection());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {}
		});
	}

}
