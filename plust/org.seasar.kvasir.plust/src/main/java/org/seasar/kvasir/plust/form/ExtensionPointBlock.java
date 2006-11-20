/**
 * 
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.IJavaElementSearchConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
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
import org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.form.command.AddExtensionPointCommand;
import org.seasar.kvasir.plust.form.command.IEditorCommandStack;
import org.seasar.kvasir.plust.form.command.IEditorCommandStackListener;


/**
 * @author shida
 *
 */
public class ExtensionPointBlock extends MasterDetailsBlock
    implements IEditorCommandStackListener
{

    private static final String EXTENSION_ELEMENT = "ExtensionElement";

    private KvasirFormPage formPage;

    private IManagedForm managedForm;

    private TreeViewer viewer;


    public ExtensionPointBlock(KvasirFormPage formPage)
    {
        super();
        this.formPage = formPage;
        IEditorCommandStack stack = formPage.getCommandStack();
        stack.addCommandStackListener(this);
    }


    class MasterContentProvider
        implements ITreeContentProvider
    {
        public Object[] getElements(Object inputElement)
        {
            return getChildren(inputElement);
        }


        public void dispose()
        {
        }


        public void inputChanged(Viewer viewer, Object oldInput, Object newInput)
        {
        }


        public Object[] getChildren(Object parentElement)
        {
            if (parentElement.getClass().isArray()) {
                return (Object[])parentElement;
            }
            return null;
        }


        public Object getParent(Object element)
        {
            // TODO Auto-generated method stub
            return null;
        }


        public boolean hasChildren(Object element)
        {
            // TODO Auto-generated method stub
            return false;
        }
    }

    class MasterLabelProvider extends LabelProvider
    {
        public String getText(Object obj)
        {
            if (obj instanceof ExtensionPoint) {
                ExtensionPoint extension = (ExtensionPoint)obj;
                return extension.getId();
            }
            return obj.toString();
        }


        public Image getImage(Object obj)
        {
            if (obj instanceof ExtensionPoint) {
                return KvasirPlugin.getImageDescriptor(
                    KvasirPlugin.IMG_EXTENSION).createImage();
            }
            return null;
        }
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
                    SelectionDialog dialog = JavaUI.createTypeDialog(window
                        .getShell(), window, KvasirPlugin.getDefault()
                        .getCurrentProject(editorInput),
                        IJavaElementSearchConstants.CONSIDER_ALL_TYPES, false);
                    dialog.setTitle("拡張ポイントクラスを指定");
                    dialog.setMessage("拡張ポイントクラスを指定:");
                    if (dialog.open() == Dialog.OK) {
                        Object[] result = dialog.getResult();
                        //Selected class is extension point def.
                        for (int i = 0; i < result.length; i++) {
                            IType object = (IType)result[i];
                            String[] interfaceNames = object
                                .getSuperInterfaceNames();
                            boolean impl = false;
                            for (int j = 0; j < interfaceNames.length; j++) {
                                String interfaceName = interfaceNames[j];
                                if (EXTENSION_ELEMENT.equals(interfaceName)) {
                                    impl = true;
                                }
                            }
                            if (!impl) {
                                MessageDialog
                                    .openError(window.getShell(),
                                        "拡張ポイントの指定が不正です",
                                        "拡張ポイントの指定が不正です。拡張ポイントクラスにはExtensionElementインタフェースの実装クラスのみが指定できます。");
                            } else {
                                IEditorCommandStack stack = formPage
                                    .getCommandStack();
                                stack.execute(new AddExtensionPointCommand(
                                    formPage.getDescriptor(), object));
                            }
                        }
                    }
                } catch (JavaModelException e1) {
                    e1.printStackTrace();
                }
            }
        });

        Button remove = toolkit.createButton(buttons, "(&d)削除", SWT.PUSH); //$NON-NLS-1$
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
        viewer.setContentProvider(new MasterContentProvider());
        viewer.setLabelProvider(new MasterLabelProvider());
        viewer.setInput(formPage.getDescriptor().getExtensionPoints());
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.MasterDetailsBlock#createToolBarActions(org.eclipse.ui.forms.IManagedForm)
     */
    protected void createToolBarActions(IManagedForm managedForm)
    {
        this.managedForm = managedForm;
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
        detailsPart.registerPage(ExtensionPoint.class,
            new ExtensionPointDetailsPage(formPage));

    }


    public void fireCommandStachChanged()
    {
        viewer.setInput(formPage.getDescriptor().getExtensionPoints());
    }

}
