/**
 * 
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ColumnLayout;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.plust.KvasirPlugin;


/**
 * @author shida
 *
 */
public class GeneralPage extends KvasirFormPage
{

    public GeneralPage(FormEditor editor, PluginDescriptor descriptor)
    {
        super(editor, descriptor, "general", Messages.getString("GeneralPage.name")); //$NON-NLS-1$ //$NON-NLS-2$
    }


    @Override
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
        composite.setLayout(new FillLayout());
        composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createDescriptionColumn(composite, toolkit);
        
        Composite columns = toolkit.createComposite(form.getBody());
        ColumnLayout layout = new ColumnLayout();
        layout.maxNumColumns = 2;
        columns.setLayout(layout);
        columns.setLayoutData(new GridData(GridData.FILL_BOTH));
        createGeneralColumn(columns, toolkit);
        createDependencyColumn(columns, toolkit);
    }

    private void createDescriptionColumn(Composite composite, FormToolkit toolkit)
    {
        Section section = toolkit.createSection(composite,
            Section.DESCRIPTION | Section.TITLE_BAR);
        section.setText(Messages.getString("GeneralPage.section1")); //$NON-NLS-1$
        section
            .setDescription(Messages.getString("GeneralPage.description1")); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
        toolkit.createHyperlink(sectionClient, Messages.getString("GeneralPage.link"), SWT.NONE); //$NON-NLS-1$
        section.setClient(sectionClient);
    }    

    private void createGeneralColumn(Composite composite, FormToolkit toolkit)
    {
        Section section = toolkit.createSection(composite,
            Section.DESCRIPTION | Section.TITLE_BAR );
        section.setText(Messages.getString("GeneralPage.section2")); //$NON-NLS-1$
        section
            .setDescription(Messages.getString("GeneralPage.description2")); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout(3, false));
        toolkit.createLabel(sectionClient, Messages.getString("GeneralPage.id")); //$NON-NLS-1$
        toolkit.createLabel(sectionClient, ":"); //$NON-NLS-1$
        Text idText = toolkit.createText(sectionClient, getDescriptor().getId());
        idText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.createLabel(sectionClient, Messages.getString("GeneralPage.version")); //$NON-NLS-1$
        toolkit.createLabel(sectionClient, ":"); //$NON-NLS-1$
        Text versionText = toolkit.createText(sectionClient, getDescriptor().getVersionString());
        versionText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.createLabel(sectionClient, Messages.getString("GeneralPage.plugin.name")); //$NON-NLS-1$
        toolkit.createLabel(sectionClient, ":"); //$NON-NLS-1$
        Text nameText = toolkit.createText(sectionClient, getDescriptor().getName());
        nameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.createLabel(sectionClient, Messages.getString("GeneralPage.provider")); //$NON-NLS-1$
        toolkit.createLabel(sectionClient, ":"); //$NON-NLS-1$
        Text providerText = toolkit.createText(sectionClient, getDescriptor().getProviderName());
        providerText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        section.setClient(sectionClient);
    }
    
    private void createDependencyColumn(Composite composite, FormToolkit toolkit)
    {
        Section section = toolkit.createSection(composite,
            Section.DESCRIPTION | Section.TITLE_BAR);
        section.setText(Messages.getString("GeneralPage.section3")); //$NON-NLS-1$
        section
            .setDescription(Messages.getString("GeneralPage.description3")); //$NON-NLS-1$
        Composite sectionClient = toolkit.createComposite(section);
        sectionClient.setLayout(new GridLayout());
        toolkit.createHyperlink(sectionClient, Messages.getString("GeneralPage.dependency"), SWT.NONE); //$NON-NLS-1$
        toolkit.createHyperlink(sectionClient, Messages.getString("GeneralPage.library"), SWT.NONE); //$NON-NLS-1$
        toolkit.createHyperlink(sectionClient, Messages.getString("GeneralPage.extension"), SWT.NONE); //$NON-NLS-1$
        toolkit.createHyperlink(sectionClient, Messages.getString("GeneralPage.extensionpoint"), SWT.NONE);         //$NON-NLS-1$
        section.setClient(sectionClient);
    }
    
//    private void createRuntimeColumn(Composite composite, FormToolkit toolkit)
//    {
//        Section section = toolkit.createSection(composite,
//            Section.DESCRIPTION | Section.TITLE_BAR);
//        section.setText("プラグインの配備と実行");
//        section
//            .setDescription("作成したプラグインは簡単に配備、実行することが出来ます。プラグインの配備はMavenによるビルドで実施します。プラグインの実行はアプリケーションサーバの実行と共にKvasir上に展開され、実行されます。");
//        Composite sectionClient = toolkit.createComposite(section);
//        sectionClient.setLayout(new GridLayout());
//        toolkit.createHyperlink(sectionClient, "プラグインの配備", SWT.NONE);
//        toolkit.createHyperlink(sectionClient, "プラグインの実行", SWT.NONE);
//        section.setClient(sectionClient);
//    }
}
