/**
 * 
 */
package org.seasar.kvasir.plust.form.provider;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;

import net.skirnir.xom.PropertyDescriptor;


/**
 * @author shida
 *
 */
public class PropertyEditor
{

    private PropertyDescriptor descriptor;

    private String value;


    public PropertyEditor(PropertyDescriptor descriptor, String value)
    {
        super();
        this.descriptor = descriptor;
        this.value = value;
    }


    public void createContents(Composite composite, FormToolkit toolkit)
    {

        toolkit.createLabel(composite, descriptor.getName());
        toolkit.createLabel(composite, ":");
        toolkit.createText(composite, value);
    }

}
