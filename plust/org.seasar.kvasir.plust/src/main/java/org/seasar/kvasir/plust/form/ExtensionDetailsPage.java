/**
 * 
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.model.ExtensionModel;


/**
 * @author shida
 *
 */
public class ExtensionDetailsPage
    implements IDetailsPage
{

    private String point;

    private IManagedForm form;

    public ExtensionDetailsPage(ExtensionModel bean, String point)
    {
        super();
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
        toolkit.paintBordersFor(s1);
        s1.setClient(client);

    }

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
