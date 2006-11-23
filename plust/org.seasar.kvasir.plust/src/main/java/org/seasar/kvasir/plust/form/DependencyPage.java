package org.seasar.kvasir.plust.form;

import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.forms.FormColors;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.seasar.kvasir.plust.model.PluginModel;
import org.seasar.kvasir.plust.model.PlustLabelProvider;
import org.seasar.kvasir.plust.model.PlustTreeContentProvider;


public class DependencyPage extends KvasirFormPage
{

    public DependencyPage(FormEditor editor, PluginModel root)
    {
        super(editor, root,
            "dependency", Messages.getString("DependencyPage.name")); //$NON-NLS-1$ //$NON-NLS-2$
    }


    protected void createFormContent(IManagedForm managedForm)
    {
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        form.setText("依存関係とランタイムライブラリ"); //$NON-NLS-1$
        // TODO Eclipse3.1では存在しないAPI呼び出し。
        //        form.setImage(KvasirPlugin.getImageDescriptor(KvasirPlugin.IMG_LOGO).createImage());
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        form.getBody().setLayout(layout);
        createImportSection(form, toolkit, "依存関係"); //$NON-NLS-1$
        //        createRuntimeSection(form, toolkit, "ランタイムライブラリ"); //$NON-NLS-1$
    }


    private void createImportSection(final ScrolledForm form,
        FormToolkit toolkit, String title)
    {
        Section section = toolkit.createSection(form.getBody(),
            Section.DESCRIPTION | Section.TITLE_BAR);
        section.setActiveToggleColor(toolkit.getHyperlinkGroup()
            .getActiveForeground());
        section.setToggleColor(toolkit.getColors().getColor(
            FormColors.SEPARATOR));
        Composite client = toolkit.createComposite(section, SWT.WRAP);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;

        client.setLayout(layout);
        Table t = toolkit.createTable(client, SWT.NULL);
        TableViewer importViewer = new TableViewer(t);
        importViewer.setContentProvider(new PlustTreeContentProvider());
        importViewer.setLabelProvider(new PlustLabelProvider());
        importViewer.setInput(getDescriptor().getRequires());

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 200;
        gd.widthHint = 100;
        t.setLayoutData(gd);
        toolkit.paintBordersFor(client);
        Composite bp = toolkit.createComposite(client);
        bp.setLayout(new GridLayout());
        Button add = toolkit.createButton(bp, "(&A)追加", SWT.PUSH); //$NON-NLS-1$
        gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        add.setLayoutData(gd);

        add.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e)
            {
                ElementListSelectionDialog dialog = new ElementListSelectionDialog(
                    form.getShell(), new LabelProvider());
                dialog.open();

                super.widgetSelected(e);
            }

        });
        Button del = toolkit.createButton(bp, "(&D)削除", SWT.PUSH); //$NON-NLS-1$
        gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        del.setLayoutData(gd);
        gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        bp.setLayoutData(gd);
        section.setText(title);
        section
            .setDescription("プラグインをビルドするために必要なプラグインを追加、または削除します。追加可能なプラグインはクラスパス、またはPOMから自動的に計算されます。"); //$NON-NLS-1$
        section.setClient(client);
        section.addExpansionListener(new ExpansionAdapter() {
            public void expansionStateChanged(ExpansionEvent e)
            {
                form.reflow(false);
            }
        });
        gd = new GridData(GridData.FILL_BOTH);
        section.setLayoutData(gd);
    }

    /*
     private void createRuntimeSection(final ScrolledForm form,
     FormToolkit toolkit, String title)
     {
     Section section = toolkit.createSection(form.getBody(),
     Section.DESCRIPTION | Section.TITLE_BAR);
     section.setActiveToggleColor(toolkit.getHyperlinkGroup()
     .getActiveForeground());
     section.setToggleColor(toolkit.getColors().getColor(
     FormColors.SEPARATOR));
     Composite client = toolkit.createComposite(section, SWT.WRAP);
     GridLayout layout = new GridLayout();
     layout.numColumns = 2;

     client.setLayout(layout);
     Table t = toolkit.createTable(client, SWT.NULL);
     GridData gd = new GridData(GridData.FILL_BOTH);
     gd.heightHint = 200;
     gd.widthHint = 100;
     t.setLayoutData(gd);
     toolkit.paintBordersFor(client);
     Composite bp = toolkit.createComposite(client);
     bp.setLayout(new GridLayout());
     Button add = toolkit.createButton(bp, "(&a)追加", SWT.PUSH); //$NON-NLS-1$
     gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
     add.setLayoutData(gd);
     Button del = toolkit.createButton(bp, "(&d)削除", SWT.PUSH); //$NON-NLS-1$
     gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
     del.setLayoutData(gd);
     gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
     bp.setLayoutData(gd);
     section.setText(title);
     section.setDescription("プラグイン実行時に必要なライブラリ、またはクラスパスを設定します。"); //$NON-NLS-1$
     section.setClient(client);
     section.addExpansionListener(new ExpansionAdapter() {
     public void expansionStateChanged(ExpansionEvent e)
     {
     form.reflow(false);
     }
     });
     gd = new GridData(GridData.FILL_BOTH);
     section.setLayoutData(gd);
     }
     */
}
