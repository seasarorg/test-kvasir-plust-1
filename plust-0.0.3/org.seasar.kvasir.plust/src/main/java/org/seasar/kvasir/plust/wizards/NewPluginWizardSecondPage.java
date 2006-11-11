package org.seasar.kvasir.plust.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.seasar.kvasir.plust.KvasirPlugin;


public class NewPluginWizardSecondPage extends WizardPage
{
    private Text              pluginIdField_;

    private Text              pluginNameField_;

    private Text              pluginVersionField_;

    private Text              pluginProviderNameField_;

    private Text              pluginClassNameField_;

    private Button            addLicenseFileButton_;

    private Label             licenseLabel_;

    private Button[]          licenseButtons_;

    private Text              testEnvironmentVersionField_;

    private Listener          mustFieldListener_ = new Listener() {
                                                     public void handleEvent(
                                                         Event e)
                                                     {
                                                         setPageComplete(validatePage());
                                                     }
                                                 };

    private boolean           alreadyShown_;

    private boolean           addingLicenseFile_;

    private LicenseMetaData[] licenses_          = new LicenseMetaData[] { new LicenseMetaData(
                                                     "asl-2.0.txt", //$NON-NLS-1$
                                                     KvasirPlugin.getString("NewPluginWizardSecondPage.LICENSE_ASL2.0_NAME")), }; //$NON-NLS-1$


    public NewPluginWizardSecondPage()
    {
        super("NewPluginWizardSecondPage"); //$NON-NLS-1$
        setTitle(KvasirPlugin.getString("NewPluginWizardSecondPage.TITLE_PLUGIN_INFORMATION")); //$NON-NLS-1$
        setDescription(KvasirPlugin.getString("NewPluginWizardSecondPage.DESCRIPTION_PLUGIN_INFORMATION")); //$NON-NLS-1$
        setPageComplete(false);
    }


    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        setControl(composite);

        createPluginPropertyGroup(composite);
        createLicenseGroup(composite);
        createTestEnvironmentPropertyGroup(composite);

        setErrorMessage(null);
        setMessage(null);
    }


    void createPluginPropertyGroup(Composite parent)
    {
        Font font = parent.getFont();

        Group group = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setFont(font);
        group.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.GROUP_PLUGIN_PROPERTY")); //$NON-NLS-1$

        Label pluginNameLabel = new Label(group, SWT.NONE);
        pluginNameLabel.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.LABEL_PLUGIN_NAME")); //$NON-NLS-1$
        pluginNameLabel.setFont(font);

        pluginNameField_ = new Text(group, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = 250;
        pluginNameField_.setLayoutData(data);
        pluginNameField_.setFont(font);

        Label pluginIdLabel = new Label(group, SWT.NONE);
        pluginIdLabel.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.LABEL_PLUGIN_ID")); //$NON-NLS-1$
        pluginIdLabel.setFont(font);

        pluginIdField_ = new Text(group, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = 250;
        pluginIdField_.setLayoutData(data);
        pluginIdField_.setFont(font);
        pluginIdField_.addListener(SWT.Modify, mustFieldListener_);

        Label pluginVersionLabel = new Label(group, SWT.NONE);
        pluginVersionLabel.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.LABEL_VERSION")); //$NON-NLS-1$
        pluginVersionLabel.setFont(font);

        pluginVersionField_ = new Text(group, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = 250;
        pluginVersionField_.setLayoutData(data);
        pluginVersionField_.setFont(font);
        pluginVersionField_.addListener(SWT.Modify, mustFieldListener_);

        Label pluginProviderLabel = new Label(group, SWT.NONE);
        pluginProviderLabel.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.LABEL_PROVIDER")); //$NON-NLS-1$
        pluginProviderLabel.setFont(font);

        pluginProviderNameField_ = new Text(group, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = 250;
        pluginProviderNameField_.setLayoutData(data);
        pluginProviderNameField_.setFont(font);

        Label pluginClassNameLabel = new Label(group, SWT.NONE);
        pluginClassNameLabel.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.LABEL_INTERFACE")); //$NON-NLS-1$
        pluginClassNameLabel.setFont(font);

        pluginClassNameField_ = new Text(group, SWT.BORDER);
        data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = 250;
        pluginClassNameField_.setLayoutData(data);
        pluginClassNameField_.setFont(font);
        pluginClassNameField_.addListener(SWT.Modify, mustFieldListener_);
    }


    void createLicenseGroup(Composite parent)
    {
        Font font = parent.getFont();

        Group group = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setFont(font);
        group.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.GROUP_LICENSE")); //$NON-NLS-1$

        addLicenseFileButton_ = new Button(group, SWT.CHECK | SWT.RIGHT);
        addLicenseFileButton_.setFont(font);
        addLicenseFileButton_.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.BUTTON_CREATE_LICENSE_FILE")); //$NON-NLS-1$
        addLicenseFileButton_.setSelection(addingLicenseFile_);
        addLicenseFileButton_.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                addingLicenseFile_ = addLicenseFileButton_.getSelection();
                licenseLabel_.setEnabled(addingLicenseFile_);
                for (int i = 0; i < licenseButtons_.length; i++) {
                    licenseButtons_[i].setEnabled(addingLicenseFile_);
                }
            }
        });

        licenseLabel_ = new Label(group, SWT.NONE);
        licenseLabel_.setFont(font);
        licenseLabel_.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.LABEL_APPLIED_LICENSE")); //$NON-NLS-1$
        licenseLabel_.setEnabled(addingLicenseFile_);

        licenseButtons_ = new Button[licenses_.length];
        for (int i = 0; i < licenseButtons_.length; i++) {
            licenseButtons_[i] = new Button(group, SWT.RADIO);
            licenseButtons_[i].setFont(font);
            licenseButtons_[i].setText(licenses_[i].getName());
            licenseButtons_[i].setEnabled(addingLicenseFile_);
        }
        licenseButtons_[0].setSelection(true);
    }


    void createTestEnvironmentPropertyGroup(Composite parent)
    {
        Font font = parent.getFont();

        Group group = new Group(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        group.setLayout(layout);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setFont(font);
        group.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.GROUP_TEST_ENVIRONMENT")); //$NON-NLS-1$

        Label versionLabel = new Label(group, SWT.NONE);
        versionLabel.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.LABEL_TEST_ENVIRONMENT_VERSION")); //$NON-NLS-1$
        versionLabel.setFont(font);

        testEnvironmentVersionField_ = new Text(group, SWT.BORDER);
        GridData data = new GridData(GridData.FILL_HORIZONTAL);
        data.widthHint = 250;
        testEnvironmentVersionField_.setLayoutData(data);
        testEnvironmentVersionField_.setFont(font);
    }


    protected boolean validatePage()
    {
        if (getPluginId().length() == 0) {
            return false;
        }
        if (getPluginVersion().length() == 0) {
            return false;
        }
        if (getPluginClassName().length() == 0) {
            return false;
        }
        if (getTestEnvironmentVersion().length() == 0) {
            return false;
        }
        return true;
    }


    public void setVisible(boolean visible)
    {
        if (visible) {
            if (!alreadyShown_) {
                setDefaultValues();
                alreadyShown_ = true;
            }
        }
        super.setVisible(visible);
    }


    void setDefaultValues()
    {
        String projectName = ((NewPluginWizardFirstPage)getPreviousPage())
            .getProjectName();

        pluginNameField_.setText(toPluginName(projectName));
        pluginIdField_.setText(toPluginId(projectName));
        pluginVersionField_.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.DEFAULT_VERSION")); //$NON-NLS-1$
        pluginClassNameField_.setText(toPluginClassName(projectName));

        testEnvironmentVersionField_.setText(KvasirPlugin.getString("NewPluginWizardSecondPage.DEFAULT_TEST_ENVIRONMENT_VERSION")); //$NON-NLS-1$

        setPageComplete(validatePage());
    }


    String toPluginName(String projectName)
    {
        int dot = projectName.lastIndexOf('.');
        if (dot >= 0) {
            String name = projectName.substring(dot + 1);
            if (name.length() == 0) {
                return ""; //$NON-NLS-1$
            } else {
                return Character.toUpperCase(name.charAt(0))
                    + name.substring(1) + " Plugin"; //$NON-NLS-1$
            }
        } else {
            return Character.toUpperCase(projectName.charAt(0))
                + projectName.substring(1) + " Plugin"; //$NON-NLS-1$
        }
    }


    String toPluginId(String projectName)
    {
        int dot = projectName.lastIndexOf('.');
        if (dot >= 0) {
            return projectName;
        } else {
            return ""; //$NON-NLS-1$
        }
    }


    String toPluginClassName(String projectName)
    {
        int dot = projectName.lastIndexOf('.');
        if (dot >= 0) {
            String name = projectName.substring(dot + 1);
            if (name.length() == 0) {
                return ""; //$NON-NLS-1$
            } else {
                return Character.toUpperCase(name.charAt(0))
                    + name.substring(1) + "Plugin"; //$NON-NLS-1$
            }
        } else {
            return Character.toUpperCase(projectName.charAt(0))
                + projectName.substring(1) + "Plugin"; //$NON-NLS-1$
        }
    }


    public String getPluginId()
    {
        return pluginIdField_.getText().trim();
    }


    public String getPluginName()
    {
        return pluginNameField_.getText().trim();
    }


    public String getPluginVersion()
    {
        return pluginVersionField_.getText().trim();
    }


    public String getPluginProviderName()
    {
        return pluginProviderNameField_.getText().trim();
    }


    public String getPluginClassName()
    {
        return pluginClassNameField_.getText().trim();
    }


    public LicenseMetaData getSelectedLicense()
    {
        if (addingLicenseFile_) {
            for (int i = 0; i < licenseButtons_.length; i++) {
                if (licenseButtons_[i].getSelection()) {
                    return licenses_[i];
                }
            }
        }
        return null;
    }


    public String getTestEnvironmentVersion()
    {
        return testEnvironmentVersionField_.getText().trim();
    }
}
