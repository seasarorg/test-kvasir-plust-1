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
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.ISelection;
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
import org.eclipse.swt.widgets.Menu;
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
import org.eclipse.ui.texteditor.ValidateStateException;
import org.seasar.kvasir.plust.IExtensionPoint;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.KvasirProject;
import org.seasar.kvasir.plust.Messages;
import org.seasar.kvasir.plust.form.command.AddExtensionCommand;
import org.seasar.kvasir.plust.form.command.AddRootElementCommand;
import org.seasar.kvasir.plust.form.command.IEditorCommandStackListener;
import org.seasar.kvasir.plust.form.command.RemoveExtensionCommand;
import org.seasar.kvasir.plust.model.ExtensionElementModel;
import org.seasar.kvasir.plust.model.ExtensionModel;
import org.seasar.kvasir.plust.model.PlustLabelProvider;
import org.seasar.kvasir.plust.model.PlustTreeContentProvider;

import net.skirnir.xom.Attribute;
import net.skirnir.xom.BeanAccessor;
import net.skirnir.xom.Element;
import net.skirnir.xom.MalformedValueException;
import net.skirnir.xom.Node;
import net.skirnir.xom.PropertyDescriptor;
import net.skirnir.xom.TargetNotFoundException;
import net.skirnir.xom.ValidationException;
import net.skirnir.xom.XOMapper;


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
        section.setText(Messages.getString("ExtensionBlock.5")); //$NON-NLS-1$
        section.setDescription(Messages.getString("ExtensionBlock.6")); //$NON-NLS-1$
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
        Button add = toolkit.createButton(buttons, Messages
            .getString("ExtensionBlock.0"), SWT.PUSH); //$NON-NLS-1$
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
                    dialog.setTitle(Messages.getString("ExtensionBlock.1")); //$NON-NLS-1$
                    dialog.setMessage(Messages.getString("ExtensionBlock.2")); //$NON-NLS-1$

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
                            model.setKvasirProject(kvasirProject);
                            XOMapper mapper = accessor.getMapper();
                            try {
                                // requiredな値を埋めるのは難しいのでrequiredチェックをスキップさせる。（skirnir）
                                mapper.setStrict(false);
                                //requiredな値を埋める
                                //                            fillAttribute(accessor, object);
                                model.setProperty(new Element[] { mapper
                                    .toElement(object) });
                            } catch (ValidationException ex) {
                                // object中の、requiredな値が埋まっていない。
                                // FIXME OKボタンの処理を中断して、required
                                // な値を埋めるようにエラーメッセージを出す。
                                // FIXME まだ表示されていないので、そもそも
                                // requiredな値を入れることができない。
                                // object生成時に同時にrequiredな値で埋めてやる必要あり。
                                throw new RuntimeException("Validation Error",
                                    ex);
                            } finally {
                                mapper.setStrict(true);
                            }
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
        Button remove = toolkit.createButton(buttons, Messages
            .getString("ExtensionBlock.3"), SWT.PUSH); //$NON-NLS-1$
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
        hookContextMenu();
    }


    /**
     * 必須属性を設定する。
     * FIXME とりあえず空文字を突っ込んでいるが、これでよいかは微妙。
     * Popプラグインだとなぜか怒られる。
     * @param descriptor
     * @param object
     */
    //    private void fillAttribute(BeanAccessor beanAccessor, Object object)
    //    {
    //        String[] requiredAttributeNames = beanAccessor
    //            .getRequiredAttributeNames();
    //        for (int i = 0; i < requiredAttributeNames.length; i++) {
    //            String name = requiredAttributeNames[i];
    //            try {
    //                String value = beanAccessor.getAttributeDescriptor(name)
    //                    .getDefault();
    //                if (value == null) {
    //                    value = "";
    //                }
    //                beanAccessor.setAttribute(object, name, value);
    //            } catch (TargetNotFoundException e) {
    //                e.printStackTrace();
    //            } catch (MalformedValueException e) {
    //                e.printStackTrace();
    //            }
    //        }
    //    }
    private void hookContextMenu()
    {
        MenuManager menuMgr = new MenuManager(Messages
            .getString("ExtensionBlock.4")); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager manager)
            {
                ExtensionBlock.this.fillContextMenu(manager);
            }
        });
        Menu menu = menuMgr.createContextMenu(viewer.getControl());
        viewer.getControl().setMenu(menu);
    }


    protected void fillContextMenu(IMenuManager manager)
    {
        ISelection selection = viewer.getSelection();
        if (selection instanceof IStructuredSelection) {
            IStructuredSelection structuredSelection = (IStructuredSelection)selection;
            Object element = structuredSelection.getFirstElement();
            if (element instanceof ExtensionElementModel) {
                ExtensionElementModel model = (ExtensionElementModel)element;
                String[] names = model.getChildNames();
                for (int i = 0; i < names.length; i++) {
                    String name = names[i];
                    AddElementAction action = new AddElementAction(name,
                        formPage.getCommandStack(), model);
                    manager.add(action);
                }
                manager.add(new RemoveElementAction(formPage.getCommandStack(),
                    model));
            }
            if (element instanceof ExtensionModel) {
                ExtensionModel model = (ExtensionModel)element;
                String name = model.getChildRootName();
                manager.add(new AddRootElementAction(name, model, formPage
                    .getCommandStack()));
            }
        }
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
            ExtensionModel[] extensions = formPage.getDescriptor()
                .getExtensions();
            for (int i = 0; i < extensions.length; i++) {
                ExtensionModel model = extensions[i];
                try {
                    model.refresh();
                } catch (RuntimeException e) {
                    e.printStackTrace();
                }
            }
            //    viewer.setInput(formPage.getDescriptor().getExtensions());
            viewer.refresh();
        }
    }

}
