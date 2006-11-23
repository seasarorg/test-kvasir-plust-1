/**
 * 
 */
package org.seasar.kvasir.plust.form;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
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
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.forms.DetailsPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.MasterDetailsBlock;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.seasar.kvasir.plust.IExtensionPoint;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.KvasirProject;
import org.seasar.kvasir.plust.form.command.AddExtensionCommand;
import org.seasar.kvasir.plust.form.command.IEditorCommandStackListener;
import org.seasar.kvasir.plust.form.command.RemoveExtensionCommand;
import org.seasar.kvasir.plust.model.ExtensionModel;
import org.seasar.kvasir.plust.model.PlustLabelProvider;
import org.seasar.kvasir.plust.model.PlustTreeContentProvider;

import net.skirnir.xom.Attribute;
import net.skirnir.xom.BeanAccessor;
import net.skirnir.xom.Element;
import net.skirnir.xom.Node;
import net.skirnir.xom.PropertyDescriptor;
import net.skirnir.xom.TargetNotFoundException;


/**
 * @author shida
 *
 */
public class ExtensionBlock extends MasterDetailsBlock
    implements IEditorCommandStackListener
{

    private KvasirFormPage formPage;

    private TreeViewer viewer;


    public ExtensionBlock(KvasirFormPage formPage)
    {
        super();
        this.formPage = formPage;
        this.formPage.getCommandStack().addCommandStackListener(this);
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
        Button add = toolkit.createButton(buttons, "(&a)追加", SWT.PUSH);
        add.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e)
            {
                IWorkbenchWindow window = PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow();
                IEditorInput editorInput = formPage.getEditorInput();

                try {
                    KvasirProject kvasirProject = KvasirPlugin.getDefault()
                        .getKvasirProject(editorInput);
                    IExtensionPoint[] extensionPoints = kvasirProject
                        .getImportedExtensionPoints();
                    ElementListSelectionDialog dialog = new ElementListSelectionDialog(
                        window.getShell(), new LabelProvider() {

                            public String getText(Object element)
                            {
                                IExtensionPoint point = (IExtensionPoint)element;
                                return point.getId();
                            }


                            public Image getImage(Object element)
                            {
                                return KvasirPlugin.getDefault().getImage(
                                    KvasirPlugin.IMG_EXTENSION_POINT);
                            }
                        });
                    dialog.setElements(extensionPoints);
                    dialog.setTitle("拡張ポイントの選択");
                    dialog.setMessage("コントリビュートする拡張ポイントを選んでください");

                    if (dialog.open() == Dialog.OK) {
                        IExtensionPoint point = (IExtensionPoint)dialog
                            .getFirstResult();
                        if (point != null) {
                            ExtensionModel model = new ExtensionModel();
                            model.setPoint(point.getId());
                            model.setDescription(point.getDescription(Locale
                                .getDefault()));
                            //デフォルトのエレメントも作る.
                            BeanAccessor accessor = point
                                .getElementClassAccessor();
                            Object object = accessor.newInstance();
                            String[] names = accessor.getAttributeNames();
                            List attrs = new ArrayList();
                            for (int i = 0; i < names.length; i++) {
                                String name = names[i];
                                PropertyDescriptor descriptor = accessor.getAttributeDescriptor(name);
                                Attribute attribute = new Attribute(name, "\"",descriptor.getDefault());
                                attrs.add(attribute);
                            }
                            //TODO 子供のエレメントも作るか...
                            model.setProperty(new Element[] { new Element(
                                accessor.getBeanName(), (Attribute[])attrs
                                    .toArray(new Attribute[0]), new Node[0]) });

                            formPage.getCommandStack().execute(
                                new AddExtensionCommand(formPage
                                    .getDescriptor(), model));
                        }
                    }
                } catch (CoreException e1) {
                    // TODO 自動生成された catch ブロック
                    e1.printStackTrace();
                }
            }
        });
        Button remove = toolkit.createButton(buttons, "(&d)削除", SWT.PUSH);
        remove.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e)
            {
                if (viewer.getSelection() != null) {
                    IStructuredSelection selection = (IStructuredSelection)viewer
                        .getSelection();
                    for (Iterator iter = selection.iterator(); iter.hasNext();) {
                        Object element = (Object)iter.next();
                        if (element instanceof ExtensionModel) {
                            formPage.getCommandStack().execute(
                                new RemoveExtensionCommand(formPage
                                    .getDescriptor(), (ExtensionModel)element));
                        }
                        if (element instanceof Element) {
                            Element elem = (Element)element;
                            //TODO 親が取れないと消せない...
                        }
                    }

                }
            }
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


    public void fireCommandStachChanged()
    {
        if (viewer != null) {
            viewer.setInput(formPage.getDescriptor().getExtensions());
        }
    }

}
