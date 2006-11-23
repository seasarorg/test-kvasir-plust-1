/**
 * 
 */
package org.seasar.kvasir.plust.model;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.model.ImportModel;


/**
 * @author shida
 *
 */
public class PlustLabelProvider extends LabelProvider
    implements IBaseLabelProvider
{

    public String getText(Object element)
    {
        if (element instanceof ImportModel) {
            ImportModel imp = (ImportModel)element;
            return imp.getPluginId();
        } else if (element instanceof LibraryModel) {
            //TODO ライブラリモデルはいつか修正
            LibraryModel libraryModel = (LibraryModel)element;
            return libraryModel.getDescription();
        } else if (element instanceof ExtensionModel) {
            ExtensionModel model = (ExtensionModel)element;
            return model.getPoint();
        } else if (element instanceof ExtensionPointModel) {
            ExtensionPointModel model = (ExtensionPointModel)element;
            return model.getId();
        }
        return super.getText(element);
    }


    public Image getImage(Object element)
    {
        if (element instanceof ImportModel) {
            return KvasirPlugin.getImageDescriptor(KvasirPlugin.IMG_REQUIRED)
                .createImage();
        } else if (element instanceof LibraryModel) {
            return KvasirPlugin.getImageDescriptor(KvasirPlugin.IMG_LIBRARY).createImage();
        } else if (element instanceof ExtensionModel) {
            return KvasirPlugin.getImageDescriptor(KvasirPlugin.IMG_EXTENSION).createImage();
        } else if (element instanceof ExtensionPointModel) {
            return KvasirPlugin.getImageDescriptor(KvasirPlugin.IMG_EXTENSION_POINT).createImage();
        }
        return super.getImage(element);
    }
}
