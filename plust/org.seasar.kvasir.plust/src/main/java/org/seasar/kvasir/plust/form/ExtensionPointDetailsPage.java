/**
 * 
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IDetailsPage;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint;


/**
 * @author shida
 *
 */
public class ExtensionPointDetailsPage
    implements IDetailsPage
{

    private IManagedForm form;

    private Section section;

    private Text className;

    private Text idText;

    private Text description;


    public ExtensionPointDetailsPage(KvasirFormPage formPage)
    {
        super();
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
        section = toolkit.createSection(parent, Section.DESCRIPTION
            | Section.TITLE_BAR);
        section.marginWidth = 10;
        section.setText(Messages
            .getString("ExtensionPointDetailsPage.section1")); //$NON-NLS-1$

        section.setDescription(Messages
            .getString("ExtensionPointDetailsPage.description1")); //$NON-NLS-1$
        TableWrapData td = new TableWrapData(TableWrapData.FILL,
            TableWrapData.TOP);
        td.grabHorizontal = true;
        section.setLayoutData(td);
        Composite client = toolkit.createComposite(section);
        client.setLayout(new GridLayout(4, false));
        toolkit.createLabel(client, Messages
            .getString("ExtensionPointDetailsPage.id")); //$NON-NLS-1$
        toolkit.createLabel(client, ":"); //$NON-NLS-1$
        idText = toolkit.createText(client, ""); //$NON-NLS-1$
        idText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.createLabel(client, ""); //$NON-NLS-1$
        toolkit.createLabel(client, Messages
            .getString("ExtensionPointDetailsPage.clazz")); //$NON-NLS-1$
        toolkit.createLabel(client, ":"); //$NON-NLS-1$
        className = toolkit.createText(client, ""); //$NON-NLS-1$
        className.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.createButton(client, "...", SWT.NONE); //$NON-NLS-1$
        toolkit.createLabel(client, "説明"); //$NON-NLS-1$
        toolkit.createLabel(client, ":"); //$NON-NLS-1$
        description = toolkit.createText(client, ""); //$NON-NLS-1$
        description.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        toolkit.paintBordersFor(section);
        section.setClient(client);

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
        // TODO Auto-generated method stub
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
        IStructuredSelection structuredSelection = (IStructuredSelection)selection;
        ExtensionPoint point = (ExtensionPoint)structuredSelection
            .getFirstElement();
        section.setDescription(point.getId()
            + Messages.getString("ExtensionPointDetailsPage.description2")); //$NON-NLS-1$
        idText.setText(point.getId());
        className.setText(point.getElementClassName());
        description.setText(point.getDescription());
    }

}
