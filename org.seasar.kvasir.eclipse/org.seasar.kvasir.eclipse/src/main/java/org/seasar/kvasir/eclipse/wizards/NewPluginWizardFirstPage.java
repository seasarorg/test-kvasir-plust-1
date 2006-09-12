package org.seasar.kvasir.eclipse.wizards;

import java.io.File;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.WizardNewProjectCreationPage;
import org.seasar.kvasir.eclipse.KvasirPlugin;


public class NewPluginWizardFirstPage extends WizardNewProjectCreationPage
{
    private Text   projectNameField_;

    private Text   locationPathField_;

    private String initialLocationPath_;


    public NewPluginWizardFirstPage()
    {
        super("NewPluginWizardFirstPage"); //$NON-NLS-1$
        setTitle(KvasirPlugin.getString("NewPluginWizardFirstPage.TITLE_PLUGIN_PROJECT")); //$NON-NLS-1$
        setDescription(KvasirPlugin.getString("NewPluginWizardFirstPage.DESCRIPTION_PLUGIN_PROJECT")); //$NON-NLS-1$
    }


    public void createControl(Composite parent)
    {
        super.createControl(parent);
        projectNameField_ = null;
        locationPathField_ = null;
        findProjectNameFieldAndLocationPathField(parent);
        projectNameField_.addListener(SWT.Modify, new Listener() {
            public void handleEvent(Event e)
            {
                if (!locationPathField_.isEnabled()) {
                    String projectName = projectNameField_.getText().trim();
                    String locationPath;
                    if (projectName.length() == 0) {
                        locationPath = initialLocationPath_;
                    } else {
                        locationPath = initialLocationPath_ + File.separator
                            + projectName;
                    }
                    locationPathField_.setText(locationPath);
                }
            }
        });
    }


    private boolean findProjectNameFieldAndLocationPathField(Composite composite)
    {
        Control[] children = composite.getChildren();
        for (int i = 0; i < children.length; i++) {
            if (children[i] instanceof Composite) {
                if (findProjectNameFieldAndLocationPathField((Composite)children[i])) {
                    return true;
                }
            } else if (children[i] instanceof Text) {
                if (projectNameField_ == null) {
                    projectNameField_ = (Text)children[i];
                } else if (locationPathField_ == null) {
                    locationPathField_ = (Text)children[i];
                    initialLocationPath_ = locationPathField_.getText();
                    return true;
                }
            }
        }
        return false;
    }
}
