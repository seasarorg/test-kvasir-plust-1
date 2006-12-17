/**
 * 
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.SelectionDialog;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.seasar.kvasir.base.descriptor.ExtensionElement;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.Messages;
import org.seasar.kvasir.plust.form.command.AddExtensionPointCommand;
import org.seasar.kvasir.plust.form.command.IEditorCommandStack;
import org.seasar.kvasir.plust.form.command.IEditorCommandStackListener;
import org.seasar.kvasir.plust.model.ExtensionPointModel;
import org.seasar.kvasir.plust.model.PlustLabelProvider;
import org.seasar.kvasir.plust.model.PlustTreeContentProvider;


/**
 * @author shida
 *
 */
public class ExtensionPointBlock extends MasterDetailsBlock
    implements IEditorCommandStackListener
{

    private KvasirFormPage formPage;

    private TreeViewer viewer;


    public ExtensionPointBlock(KvasirFormPage formPage)
    {
        super();
        this.formPage = formPage;
        IEditorCommandStack stack = formPage.getCommandStack();
        stack.addCommandStackListener(this);
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createMasterPart(org.eclipse.ui.forms.IManagedForm, org.eclipse.swt.widgets.Composite)
     */
    protected void createMasterPart(final IManagedForm managedForm,
        final Composite parent)
    {
        FormToolkit toolkit = managedForm.getToolkit();
        Section section = toolkit.createSection(parent, Section.DESCRIPTION
            | Section.TITLE_BAR);
        section.setText("Kvasirプラグインの拡張"); //$NON-NLS-1$
        section
            .setDescription("Kvasirプラグインが提供する拡張ポイントを選択します。選択できる拡張ポイントはクラスパス内のJavaBeansになります。"); //$NON-NLS-1$
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
        add.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e)
            {
                super.widgetSelected(e);
                //Open JavaClass Selector.
                IWorkbenchWindow window = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow();
                IEditorInput editorInput = formPage.getEditorInput();
                try {
                    //get ExtensionElement type
                    IProject project = KvasirPlugin.getDefault()
                        .getCurrentProject(editorInput);
                    IJavaProject javaProject = JavaCore.create(project);
                    IType type = javaProject.findType(ExtensionElement.class.getName());
                    IJavaSearchScope scope = SearchEngine.createHierarchyScope(type);
                    SelectionDialog dialog = JavaUI.createTypeDialog(window
                        .getShell(), window, scope,
                        IJavaElementSearchConstants.CONSIDER_CLASSES, false);
                    dialog.setTitle(Messages.getString("ExtensionPointBlock.0")); //$NON-NLS-1$
                    dialog.setMessage(Messages.getString("ExtensionPointBlock.1")); //$NON-NLS-1$
                    if (dialog.open() == Dialog.OK) {
                        Object[] result = dialog.getResult();
                        for (int i = 0; i < result.length; i++) {
                            IType object = (IType)result[i];
                            IEditorCommandStack stack = formPage
                            .getCommandStack();
                            stack.execute(new AddExtensionPointCommand(
                                formPage.getDescriptor(), object));
                        }

                    }
                } catch (JavaModelException e1) {
                    e1.printStackTrace();
                }
            }
        });

        Button remove = toolkit.createButton(buttons, Messages.getString("ExtensionPointBlock.2"), SWT.PUSH); //$NON-NLS-1$
        remove.addSelectionListener(new SelectionAdapter() {

        });
        section.setClient(client);
        final SectionPart spart = new SectionPart(section);
        managedForm.addPart(spart);
        viewer = new TreeViewer(t);
        viewer.addSelectionChangedListener(new ISelectionChangedListener() {
            public void selectionChanged(SelectionChangedEvent event)
            {
                managedForm.fireSelectionChanged(spart, event.getSelection());
            }
        });
        viewer.setContentProvider(new PlustTreeContentProvider());
        viewer.setLabelProvider(new PlustLabelProvider());
        viewer.setInput(formPage.getDescriptor().getExtensionPoints());
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createToolBarActions(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createToolBarActions(IManagedForm managedForm)
    {
        final ScrolledForm form = managedForm.getForm();
        Action haction = new Action(Messages.getString("ExtensionPointBlock.3"), Action.AS_RADIO_BUTTON) { //$NON-NLS-1$
            public void run()
            {
                sashForm.setOrientation(SWT.HORIZONTAL);
                form.reflow(true);
            }
        };
        haction.setChecked(true);
        haction.setImageDescriptor(KvasirPlugin
            .getImageDescriptor(KvasirPlugin.IMG_HORIZONTAL));
        Action vaction = new Action(Messages.getString("ExtensionPointBlock.4"), Action.AS_RADIO_BUTTON) { //$NON-NLS-1$
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
        detailsPart.registerPage(ExtensionPointModel.class,
            new ExtensionPointDetailsPage(formPage));

    }


    public void fireCommandStachChanged()
    {
        if (viewer != null) {
            viewer.setInput(formPage.getDescriptor().getExtensionPoints());
        }
    }

}
