package org.seasar.kvasir.plust.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.seasar.kvasir.plust.KvasirPlugin;


public class NewPluginWizardThirdPage extends WizardPage
{
    private TemplateMetaData[] templates_ = new TemplateMetaData[] {
        new TemplateMetaData(
            "empty", //$NON-NLS-1$
            KvasirPlugin.getString("NewPluginWizardThirdPage.TEMPLATE_EMPTY"), //$NON-NLS-1$
            KvasirPlugin
                .getString("NewPluginWizardThirdPage.TEMPLATE_EMPTY_DESCRIPTION")), //$NON-NLS-1$
        new TemplateMetaData(
            "generic", //$NON-NLS-1$
            KvasirPlugin.getString("NewPluginWizardThirdPage.TEMPLATE_GENERIC"), //$NON-NLS-1$
            KvasirPlugin
                .getString("NewPluginWizardThirdPage.TEMPLATE_GENERIC_DESCRIPTION")), //$NON-NLS-1$
        new TemplateMetaData(
            "cms-generic", //$NON-NLS-1$
            KvasirPlugin
                .getString("NewPluginWizardThirdPage.TEMPLATE_CMS_GENERIC"), //$NON-NLS-1$
            KvasirPlugin
                .getString("NewPluginWizardThirdPage.TEMPLATE_CMS_GENERIC_DESCRIPTION")), //$NON-NLS-1$
        new TemplateMetaData(
            "pop", KvasirPlugin.getString("NewPluginWizardThirdPage.TEMPLATE_POP"), //$NON-NLS-1$ //$NON-NLS-2$
            KvasirPlugin
                .getString("NewPluginWizardThirdPage.TEMPLATE_POP_DESCRIPTION")), //$NON-NLS-1$
        new TemplateMetaData(
            "application", KvasirPlugin.getString("NewPluginWizardThirdPage.TEMPLATE_APPLICATION"), //$NON-NLS-1$ //$NON-NLS-2$
            KvasirPlugin
                .getString("NewPluginWizardThirdPage.TEMPLATE_APPLICATION_DESCRIPTION")), }; //$NON-NLS-1$

    private List templateList_;

    private Text descriptionText_;

    private Listener mustFieldListener_ = new Listener() {
        public void handleEvent(Event e)
        {
            setPageComplete(validatePage());
        }
    };


    public NewPluginWizardThirdPage()
    {
        super("NewPluginWizardThirdPage"); //$NON-NLS-1$
        setTitle(KvasirPlugin
            .getString("NewPluginWizardThirdPage.TITLE_TEMPLATE")); //$NON-NLS-1$
        setDescription(KvasirPlugin
            .getString("NewPluginWizardThirdPage.DESCRIPTION_TEMPLATE")); //$NON-NLS-1$
        setPageComplete(false);
    }


    public void createControl(Composite parent)
    {
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));
        setControl(composite);

        createSelectTemplateGroup(composite);

        setErrorMessage(null);
        setMessage(null);
    }


    void createSelectTemplateGroup(Composite parent)
    {
        Font font = parent.getFont();

        Label selectTemplateLabel = new Label(parent, SWT.NONE);
        selectTemplateLabel.setFont(font);
        selectTemplateLabel.setText(KvasirPlugin
            .getString("NewPluginWizardThirdPage.LABEL_AVAILABLE_TEMPLATES")); //$NON-NLS-1$

        Composite composite = new Composite(parent, SWT.NULL);
        composite.setFont(parent.getFont());
        composite.setLayout(new GridLayout(2, true));
        composite.setLayoutData(new GridData(GridData.FILL_BOTH));

        templateList_ = new List(composite, SWT.SINGLE | SWT.BORDER
            | SWT.H_SCROLL | SWT.V_SCROLL);
        templateList_.setFont(font);
        templateList_.setLayoutData(new GridData(GridData.FILL_BOTH));
        for (int i = 0; i < templates_.length; i++) {
            templateList_.add(templates_[i].getName());
        }
        templateList_.addListener(SWT.Selection, mustFieldListener_);
        templateList_.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event)
            {
                String description;
                int selectionIndex = templateList_.getSelectionIndex();
                if (selectionIndex != -1) {
                    description = templates_[selectionIndex].getDescription();
                } else {
                    description = ""; //$NON-NLS-1$
                }
                descriptionText_.setText(description);
            }
        });

        descriptionText_ = new Text(composite, SWT.MULTI | SWT.BORDER
            | SWT.V_SCROLL | SWT.WRAP);
        descriptionText_.setFont(font);
        descriptionText_.setLayoutData(new GridData(GridData.FILL_BOTH));
    }


    protected boolean validatePage()
    {
        if (templateList_.getSelectionCount() == 0) {
            return false;
        }
        return true;
    }


    public String getTemplateId()
    {
        int index = templateList_.getSelectionIndex();
        if (index != -1) {
            return templates_[index].getId();
        } else {
            return null;
        }
    }
}
