/**
 * 
 */
package org.seasar.kvasir.plust.form;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.KvasirProject;
import org.seasar.kvasir.plust.model.ExtensionModel;


/**
 * @author shida
 *
 */
public class ExtensionDetailsPage
    implements IDetailsPage
{

    private ExtensionModel bean;

    private String point;

    private IManagedForm form;

    private KvasirProject kvasirProject;

    private List editors = new ArrayList();


    public ExtensionDetailsPage(ExtensionModel bean, String point)
    {
        super();
        this.bean = bean;
        this.point = point;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IDetailsPage#createContents(org.eclipse.swt.widgets.Composite)
     */
    public void createContents(Composite parent)
    {
        TableWrapLayout layout = new TableWrapLayout();
        layout.topMargin = 5;
        layout.leftMargin = 5;
        layout.rightMargin = 2;
        layout.bottomMargin = 2;
        parent.setLayout(layout);

        FormToolkit toolkit = form.getToolkit();
        Section s1 = toolkit.createSection(parent, Section.DESCRIPTION
            | Section.TITLE_BAR);
        s1.marginWidth = 10;
        s1.setText(Messages.getString("ExtensionDetailsPage.section")); //$NON-NLS-1$

        s1.setDescription(point
            + Messages.getString("ExtensionDetailsPage.description")); //$NON-NLS-1$
        TableWrapData td = new TableWrapData(TableWrapData.FILL,
            TableWrapData.TOP);
        td.grabHorizontal = true;
        s1.setLayoutData(td);
        Composite client = toolkit.createComposite(s1);
        client.setLayout(new GridLayout());

        toolkit.createLabel(client, "拡張の説明:");
        Label label = toolkit.createLabel(client, bean.getDescription(), SWT.WRAP);
        label.setLayoutData(new GridData(GridData.FILL_BOTH));
//        try {
//            IExtensionPoint extensionPoint = kvasirProject
//                .getExtensionPoint(point);
//            BeanAccessor accessor = extensionPoint.getElementClassAccessor();
//            if (accessor != null) {
//                String[] names = accessor.getAttributeNames();
//                for (int i = 0; i < names.length; i++) {
//                    String name = names[i];
//                    PropertyDescriptor descriptor = accessor
//                        .getAttributeDescriptor(name);
//                    PropertyEditor editor = new PropertyEditor(descriptor,
//                        getValue(descriptor));
//                    editor.createContents(client, toolkit);
//                    editors.add(editor);
//                }
//            }
//            if (accessor != null) {
//                String[] names = accessor.getAttributeNames();
//
//                System.out.println(names.length);
//                for (int i = 0; i < names.length; i++) {
//                    String name = names[i];
//                    PropertyDescriptor descriptor = accessor
//                        .getAttributeDescriptor(name);
//                    PropertyEditor editor = new PropertyEditor(descriptor,
//                        getValue(descriptor));
//                    editor.createContents(client, toolkit);
//                    editors.add(editor);
//                }
//            }
//        } catch (CoreException e) {
//            e.printStackTrace();
//        }
        toolkit.paintBordersFor(s1);
        s1.setClient(client);
    }


//    private String getValue(PropertyDescriptor descriptor)
//    {
//        Method method = descriptor.getReadMethod();
//        try {
//            return String.valueOf(method.invoke(bean, new Object[] {}));
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (IllegalAccessException e) {
//            e.printStackTrace();
//        } catch (InvocationTargetException e) {
//            e.printStackTrace();
//        }
//        return "";
//    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#commit(boolean)
     */
    public void commit(boolean onSave)
    {

    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#dispose()
     */
    public void dispose()
    {

    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#initialize(org.eclipse.ui.forms.IManagedForm)
     */
    public void initialize(IManagedForm form)
    {
        this.form = form;
        IEditorInput input = (IEditorInput)form.getInput();
        this.kvasirProject = KvasirPlugin.getDefault().getKvasirProject(input);
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#isDirty()
     */
    public boolean isDirty()
    {
        return false;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#isStale()
     */
    public boolean isStale()
    {
        return false;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#refresh()
     */
    public void refresh()
    {

    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#setFocus()
     */
    public void setFocus()
    {

    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IFormPart#setFormInput(java.lang.Object)
     */
    public boolean setFormInput(Object input)
    {
        return false;
    }


    /* (non-Javadoc)
     * @see org.eclipse.ui.forms.IPartSelectionListener#selectionChanged(org.eclipse.ui.forms.IFormPart, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IFormPart part, ISelection selection)
    {

    }

}
