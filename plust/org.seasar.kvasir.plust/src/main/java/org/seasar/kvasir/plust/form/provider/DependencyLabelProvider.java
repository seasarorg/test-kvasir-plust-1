/**
 * 
 */
package org.seasar.kvasir.plust.form.provider;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.model.ImportModel;


/**
 * @author shida
 *
 */
public class DependencyLabelProvider extends LabelProvider
    implements IBaseLabelProvider
{

    public String getText(Object element)
    {
        if (element instanceof ImportModel) {
            ImportModel imp = (ImportModel)element;
            return imp.getPluginId();
        }
        return super.getText(element);
    }


    public Image getImage(Object element)
    {
        if (element instanceof ImportModel) {
            return KvasirPlugin.getImageDescriptor(KvasirPlugin.IMG_REQUIRED)
                .createImage();
        }
        return super.getImage(element);
    }
}
