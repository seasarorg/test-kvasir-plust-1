/**
 * 
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.model.PluginModel;


/**
 * @author shida
 *
 */
public class GeneralPage extends KvasirFormPage
{

    public GeneralPage(FormEditor editor, PluginModel pluginRoot)
    {
        super(editor, pluginRoot,
            "general", Messages.getString("GeneralPage.name")); //$NON-NLS-1$ //$NON-NLS-2$
    }


    protected void createFormContent(IManagedForm managedForm)
    {
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        form.setText(Messages.getString("GeneralPage.title")); //$NON-NLS-1$
        // TODO Eclipse3.1では存在しないAPI呼び出し。
        //        form.setImage(KvasirPlugin.getImageDescriptor(KvasirPlugin.IMG_LOGO).createImage());
        GridLayout gridLayout = new GridLayout();
        form.getBody().setLayout(gridLayout);
        Composite composite = toolkit.createComposite(form.getBody());
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createDescriptionColumn(composite, toolkit);
        createDependencyColumn(composite, toolkit);

        Composite columns = toolkit.createComposite(form.getBody());
        ColumnLayout layout = new ColumnLayout();
        layout.maxNumColumns = 2;
        layout.minNumColumns = 2;
        columns.setLayout(layout);
        columns.setLayoutData(new GridData(GridData.FILL_BOTH));
        createGeneralColumn(columns, toolkit);
        createRuntimeColumn(columns, toolkit);

    }


    private void createDescriptionColumn(Composite composite,
        FormToolkit toolkit)
    {
        Section section = toolkit.createSection(composite, Section.DESCRIPTION
            | Section.TITLE_BAR);
        section.setText(Messages.getString("GeneralPage.section1")); //$NON-NLS-1$
        section.setDescription(Messages.getString("GeneralPage.description1")); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
//        toolkit.createHyperlink(sectionClient, Messages
//            .getString("GeneralPage.link"), SWT.NONE); //$NON-NLS-1$
        section.setClient(sectionClient);
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    }


    private void createGeneralColumn(Composite composite, FormToolkit toolkit)
    {
        Section section = toolkit.createSection(composite, Section.DESCRIPTION
            | Section.TITLE_BAR);
        section.setText(Messages.getString("GeneralPage.section2")); //$NON-NLS-1$
        section.setDescription(Messages.getString("GeneralPage.description2")); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout(3, false));
        toolkit
            .createLabel(sectionClient, Messages.getString("GeneralPage.id")); //$NON-NLS-1$
        toolkit.createLabel(sectionClient, ":"); //$NON-NLS-1$
        Text idText = toolkit.createText(sectionClient, getDescriptor()
            .getPluginId());
        idText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.createLabel(sectionClient, Messages
            .getString("GeneralPage.version")); //$NON-NLS-1$
        toolkit.createLabel(sectionClient, ":"); //$NON-NLS-1$
        Text versionText = toolkit.createText(sectionClient, getDescriptor()
            .getPluginVersion());
        versionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.createLabel(sectionClient, Messages
            .getString("GeneralPage.plugin.name")); //$NON-NLS-1$
        toolkit.createLabel(sectionClient, ":"); //$NON-NLS-1$
        Text nameText = toolkit.createText(sectionClient, getDescriptor()
            .getPluginName());
        nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.createLabel(sectionClient, Messages
            .getString("GeneralPage.provider")); //$NON-NLS-1$
        toolkit.createLabel(sectionClient, ":"); //$NON-NLS-1$
        Text providerText = toolkit.createText(sectionClient, getDescriptor()
            .getPluginProviderName());
        providerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        section.setClient(sectionClient);
    }


    private void createDependencyColumn(Composite composite, FormToolkit toolkit)
    {
        Section section = toolkit.createSection(composite, Section.DESCRIPTION
            | Section.TITLE_BAR);
        section.setText(Messages.getString("GeneralPage.section3")); //$NON-NLS-1$
        section.setDescription(Messages.getString("GeneralPage.description3")); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
        Hyperlink dependency = toolkit.createHyperlink(sectionClient, Messages
            .getString("GeneralPage.dependency"), SWT.NONE); //$NON-NLS-1$
        dependency.addHyperlinkListener(new HyperlinkAdapter() {

            public void linkActivated(HyperlinkEvent e)
            {
                getEditor().setActivePage("dependency");
            }
        });

        Hyperlink extension = toolkit.createHyperlink(sectionClient, Messages
            .getString("GeneralPage.extension"), SWT.NONE); //$NON-NLS-1$
        extension.addHyperlinkListener(new HyperlinkAdapter() {
            public void linkActivated(HyperlinkEvent e)
            {
                getEditor().setActivePage("extension");
            }
        });
        Hyperlink point = toolkit.createHyperlink(sectionClient, Messages
            .getString("GeneralPage.extensionpoint"), SWT.NONE); //$NON-NLS-1$
        point.addHyperlinkListener(new HyperlinkAdapter() {
            public void linkActivated(HyperlinkEvent e)
            {
                getEditor().setActivePage("extensionPoint");
            }
        });
        
        Hyperlink update = toolkit.createHyperlink(sectionClient, "クラスパスを最新の状態に更新", SWT.NONE);
        update.addHyperlinkListener(new HyperlinkAdapter() {
            public void linkActivated(HyperlinkEvent e)
            {
                KvasirPlugin.getDefault().flushKvasirProject(getEditorInput());
            }
        });
        section.setClient(sectionClient);
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

    }


    private void createRuntimeColumn(Composite composite, FormToolkit toolkit)
    {
        Section section = toolkit.createSection(composite, Section.DESCRIPTION
            | Section.TITLE_BAR);
        section.setText("テスト環境");
        section
            .setDescription("作成したプラグインはテスト環境に配備、実行することが出来ます。プラグインの配備はMavenによるビルドで実施します。テスト環境は+PLUSTによって自動的に構築されます。テスト環境を再構築する場合はテスト環境のリビルドをクリックしてください。");
        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());

        Composite settings = toolkit.createComposite(sectionClient);
        settings.setLayoutData(new GridData(GridData.FILL_BOTH));
        settings.setLayout(new GridLayout(3,false));
        toolkit.createLabel(settings, "グループID");
        toolkit.createLabel(settings, ":");
        Text groupIdText = toolkit.createText(settings, getDescriptor()
            .getTestEnvironmentGroupId());
        groupIdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        toolkit.createLabel(settings, "アーティファクトID");
        toolkit.createLabel(settings, ":");
        Text artifactText = toolkit.createText(settings, getDescriptor()
            .getTestEnvironmentArtifactId());
        artifactText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        toolkit.createLabel(settings, "バージョン");
        toolkit.createLabel(settings, ":");
        Text versionText = toolkit.createText(settings, getDescriptor()
            .getTestEnviromentVersion());
        versionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        Composite execute = toolkit.createComposite(sectionClient);
        execute.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        execute.setLayout(new GridLayout());
        toolkit.createHyperlink(execute, "テスト環境のリビルド", SWT.NONE);
        section.setClient(sectionClient);
    }
}
