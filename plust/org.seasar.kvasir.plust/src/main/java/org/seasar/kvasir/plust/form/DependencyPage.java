package org.seasar.kvasir.plust.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
import org.seasar.kvasir.plust.IPlugin;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.KvasirProject;
import org.seasar.kvasir.plust.Messages;
import org.seasar.kvasir.plust.form.command.AddImportCommand;
import org.seasar.kvasir.plust.form.command.IEditorCommandStackListener;
import org.seasar.kvasir.plust.form.command.RemoveImportCommand;
import org.seasar.kvasir.plust.model.ImportModel;
import org.seasar.kvasir.plust.model.PluginModel;
import org.seasar.kvasir.plust.model.PlustLabelProvider;
import org.seasar.kvasir.plust.model.PlustTreeContentProvider;


public class DependencyPage extends KvasirFormPage
    implements IEditorCommandStackListener
{

    private TableViewer viewer;


    public DependencyPage(FormEditor editor, PluginModel root)
    {
        super(editor, root,
            "dependency", Messages.getString("DependencyPage.name")); //$NON-NLS-1$ //$NON-NLS-2$
        getCommandStack().addCommandStackListener(this);
    }


    protected void createFormContent(IManagedForm managedForm)
    {
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();
        form.setText(Messages.getString("DependencyPage.2")); //$NON-NLS-1$
        // TODO Eclipse3.1では存在しないAPI呼び出し。
        //        form.setImage(KvasirPlugin.getImageDescriptor(KvasirPlugin.IMG_LOGO).createImage());
        GridLayout layout = new GridLayout();
        layout.numColumns = 1;
        form.getBody().setLayout(layout);
        createImportSection(form, toolkit, Messages
            .getString("DependencyPage.3")); //$NON-NLS-1$
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
        viewer = new TableViewer(t);
        viewer.setContentProvider(new PlustTreeContentProvider());
        viewer.setLabelProvider(new PlustLabelProvider());
        viewer.setInput(getDescriptor().getRequires());

        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 200;
        gd.widthHint = 100;
        t.setLayoutData(gd);
        toolkit.paintBordersFor(client);
        Composite bp = toolkit.createComposite(client);
        bp.setLayout(new GridLayout());
        Button add = toolkit.createButton(bp, Messages
            .getString("DependencyPage.4"), SWT.PUSH); //$NON-NLS-1$
        gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        add.setLayoutData(gd);

        add.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e)
            {
                KvasirProject kvasirProject = KvasirPlugin.getDefault()
                    .getKvasirProject(getEditorInput());
                try {
                    IPlugin[] plugins = kvasirProject.getPlugins();
                    List inculdablePlugins = new ArrayList();

                    for (int i = 0; i < plugins.length; i++) {
                        IPlugin plugin = plugins[i];
                        ImportModel[] requires = getDescriptor().getRequires();
                        boolean duplicate = false;
                        for (int j = 0; j < requires.length; j++) {
                            ImportModel model = requires[j];
                            if (model.getPluginId().equals(plugin.getId())) {
                                duplicate = true;
                            }
                        }
                        if (!duplicate) {
                            ImportModel model = new ImportModel();
                            model.setPluginId(plugin.getId());
                            model.setVersion(plugin.getVersion());
                            inculdablePlugins.add(model);
                        }
                    }

                    ElementListSelectionDialog dialog = new ElementListSelectionDialog(
                        form.getShell(), new LabelProvider() {

                            public String getText(Object element)
                            {
                                ImportModel model = (ImportModel)element;
                                return model.getPluginId();
                            }


                            public Image getImage(Object element)
                            {
                                return KvasirPlugin.getDefault().getImage(
                                    KvasirPlugin.IMG_REQUIRED);
                            }
                        });
                    dialog.setElements(inculdablePlugins.toArray());
                    dialog.setTitle(Messages.getString("DependencyPage.0")); //$NON-NLS-1$
                    dialog.setMessage(Messages.getString("DependencyPage.1")); //$NON-NLS-1$
                    if (dialog.open() == Dialog.OK) {
                        Object result = dialog.getFirstResult();
                        if (result instanceof ImportModel) {
                            getCommandStack().execute(
                                new AddImportCommand((ImportModel)result,
                                    getDescriptor()));
                        }
                    }

                } catch (CoreException e1) {
                    e1.printStackTrace();
                }
            }

        });
        Button del = toolkit.createButton(bp, Messages
            .getString("DependencyPage.5"), SWT.PUSH); //$NON-NLS-1$
        del.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e)
            {
                if (viewer.getSelection() != null) {
                    IStructuredSelection selection = (IStructuredSelection)viewer
                        .getSelection();
                    for (Iterator iter = selection.iterator(); iter.hasNext();) {
                        ImportModel element = (ImportModel)iter.next();
                        getCommandStack().execute(
                            new RemoveImportCommand(element, getDescriptor()));
                    }
                }
            }
        });
        gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        del.setLayoutData(gd);
        gd = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        bp.setLayoutData(gd);
        section.setText(title);
        section.setDescription(Messages.getString("DependencyPage.6")); //$NON-NLS-1$
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


    public void fireCommandStachChanged()
    {
        if (viewer != null) {
            viewer.setInput(getDescriptor().getRequires());
        }
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
