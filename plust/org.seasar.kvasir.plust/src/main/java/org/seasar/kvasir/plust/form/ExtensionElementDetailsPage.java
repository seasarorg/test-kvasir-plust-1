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

import net.skirnir.xom.Attribute;
import net.skirnir.xom.Element;

/**
 * @author shidat
 *
 */
public class ExtensionElementDetailsPage implements IDetailsPage
{

    private Element element;
    private IManagedForm form;

    public ExtensionElementDetailsPage(Element element)
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
        client.setLayout(new GridLayout(3, false));
        
        Attribute[] attributes = element.getAttributes();
        for (int i = 0; i < attributes.length; i++) {
            Attribute attribute = attributes[i];
            toolkit.createLabel(client, attribute.getName());
            toolkit.createLabel(client, ":");
            Text attrText = toolkit.createText(client, attribute.getValue());
            attrText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        }
        
        s1.setClient(client);
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
