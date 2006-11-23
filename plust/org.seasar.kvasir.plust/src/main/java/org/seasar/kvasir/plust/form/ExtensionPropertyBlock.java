/**
 * 
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.model.PlustLabelProvider;
import org.seasar.kvasir.plust.model.PlustTreeContentProvider;


/**
 * @author shida
 *
 */
public class ExtensionPropertyBlock extends MasterDetailsBlock
{

    private KvasirFormPage formPage;


    public ExtensionPropertyBlock(KvasirFormPage formPage)
    {
        super();
        this.formPage = formPage;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    protected void createMasterPart(final IManagedForm managedForm,
        Composite parent)
    {
        FormToolkit toolkit = managedForm.getToolkit();
        Section section = toolkit.createSection(parent, Section.DESCRIPTION
            | Section.TITLE_BAR);
        section.setText("Kvasirプラグイン拡張ポイント"); //$NON-NLS-1$
        section
            .setDescription("Kvasirプラグインが拡張する機能を選択します。選択できる拡張は、依存ページで指定したプラグインによって異なります。また、拡張する機能によって、設定できる項目も変わります。"); //$NON-NLS-1$
        section.marginWidth = 10;
        section.marginHeight = 5;
        Composite client = toolkit.createComposite(section, SWT.WRAP);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginWidth = 2;
        layout.marginHeight = 2;
        client.setLayout(layout);
        Tree t = toolkit.createTree(client, SWT.NULL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 20;
        gd.widthHint = 100;
        t.setLayoutData(gd);
        toolkit.paintBordersFor(client);
        Composite buttons = new Composite(client, SWT.NONE);
        buttons.setLayout(new GridLayout());
        gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        buttons.setLayoutData(gd);
        Button add = toolkit.createButton(buttons, "(&a)追加", SWT.PUSH); //$NON-NLS-1$
        Button remove = toolkit.createButton(buttons, "(&d)削除", SWT.PUSH); //$NON-NLS-1$
        section.setClient(client);
        final SectionPart spart = new SectionPart(section);
        managedForm.addPart(spart);
        TreeViewer viewer = new TreeViewer(t);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event)
            {
                managedForm.fireSelectionChanged(spart, event.getSelection());
            }
        });
        viewer.setContentProvider(new PlustTreeContentProvider());
        viewer.setLabelProvider(new PlustLabelProvider());
        viewer.setInput(formPage.getDescriptor().getExtensions());
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createToolBarActions(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createToolBarActions(IManagedForm managedForm)
    {
        final ScrolledForm form = managedForm.getForm();
        Action haction = new Action("hor", Action.AS_RADIO_BUTTON) { //$NON-NLS-1$
            public void run()
            {
                sashForm.setOrientation(SWT.HORIZONTAL);
                form.reflow(true);
            }
        };
        haction.setChecked(true);
        haction.setImageDescriptor(KvasirPlugin
            .getImageDescriptor(KvasirPlugin.IMG_HORIZONTAL));
        Action vaction = new Action("ver", Action.AS_RADIO_BUTTON) { //$NON-NLS-1$
            public void run()
            {
                sashForm.setOrientation(SWT.VERTICAL);
                form.reflow(true);
            }
        };
        vaction.setChecked(false);
        vaction.setImageDescriptor(KvasirPlugin
            .getImageDescriptor(KvasirPlugin.IMG_VERTICAL));
        form.getToolBarManager().add(haction);
        form.getToolBarManager().add(vaction);
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#registerPages(org.eclipse.ui.forms.DetailsPart)
     */
    protected void registerPages(DetailsPart detailsPart)
    {
        detailsPart.setPageProvider(new ExtensionDetailsPageProvider());

    }

}
