/**
 * 
 */
package org.seasar.kvasir.plust.form.provider;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.seasar.kvasir.base.plugin.descriptor.Import;
import org.seasar.kvasir.plust.KvasirPlugin;


/**
 * @author shida
 *
 */
public class DependencyLabelProvider extends LabelProvider
    implements IBaseLabelProvider
{

    public String getText(Object element)
    {
        if (element instanceof Import) {
            Import imp = (Import)element;
            return imp.getPlugin();
        }
        return super.getText(element);
    }


    public Image getImage(Object element)
    {
        if (element instanceof Import) {
            return KvasirPlugin.getImageDescriptor(KvasirPlugin.IMG_REQUIRED)
                .createImage();
        }
        return super.getImage(element);
    }
}
