/**
 * 
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.jface.viewers.ISelection;
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
import org.seasar.kvasir.plust.Messages;
import org.seasar.kvasir.plust.model.ExtensionElementModel;

import net.skirnir.xom.PropertyDescriptor;
import net.skirnir.xom.TargetNotFoundException;

/**
 * @author shidat
 *
 */
public class ExtensionElementDetailsPage implements IDetailsPage
{

    private ExtensionElementModel element;
    private IManagedForm form;

    public ExtensionElementDetailsPage(ExtensionElementModel element)
    {
        this.element = element;
    }

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
        s1.setText("拡張プロパティの設定"); //$NON-NLS-1$

        s1.setDescription("拡張に設定するプロパティを定義します。"); //$NON-NLS-1$
        TableWrapData td = new TableWrapData(TableWrapData.FILL,
            TableWrapData.TOP);
        td.grabHorizontal = true;
        s1.setLayoutData(td);
        Composite client = toolkit.createComposite(s1);
        client.setLayout(new GridLayout());
        s1.setClient(client);
        
        if (element.getAccessor() != null) {
            createAttributeFields(parent, toolkit);
        }
    }

    private void createAttributeFields(Composite parent, FormToolkit toolkit)
    {
        Section attrSection = toolkit.createSection(parent, Section.DESCRIPTION
            | Section.TITLE_BAR | Section.EXPANDED);
        attrSection.marginWidth = 10;
        attrSection.setText(Messages.getString("ExtensionElementDetailsPage.0")); //$NON-NLS-1$
        attrSection.setDescription(Messages.getString("ExtensionElementDetailsPage.1")); //$NON-NLS-1$

        Composite attributes = toolkit.createComposite(attrSection);
        attributes.setLayout(new GridLayout(3, false));
        String[] names = element.getAccessor().getAttributeNames();
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            PropertyDescriptor descriptor = element.getAccessor()
                .getAttributeDescriptor(name);
            String labelStr = ""; //$NON-NLS-1$
            if (descriptor.isRequired()) {
                labelStr = "*"; //$NON-NLS-1$
            }
            labelStr += descriptor.getName();

            Object attribute = ""; //$NON-NLS-1$
            try {
                attribute = element.getAccessor().getAttribute(element.getBean(), name);
            } catch (TargetNotFoundException e) {
                e.printStackTrace();
            }

            toolkit.createLabel(attributes, labelStr);
            toolkit.createLabel(attributes, ":"); //$NON-NLS-1$
            Text text = toolkit.createText(attributes, ""); //$NON-NLS-1$
            text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            if (attribute != null) {
                text.setText(attribute.toString());
            }
        }
        attrSection.setClient(attributes);
    }
    
    public void commit(boolean onSave)
    {
        // TODO 自動生成されたメソッド・スタブ
        
    }

    public void dispose()
    {
        // TODO 自動生成されたメソッド・スタブ
        
    }

    public void initialize(IManagedForm form)
    {
        this.form = form;
    }

    public boolean isDirty()
    {
        return false;
    }

    public boolean isStale()
    {
        return false;
    }

    public void refresh()
    {
        
    }

    public void setFocus()
    {
        
    }

    public boolean setFormInput(Object input)
    {
        
        return false;
    }

    public void selectionChanged(IFormPart part, ISelection selection)
    {
        
    }

}
