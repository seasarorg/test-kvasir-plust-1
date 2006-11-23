/**
 * 
 */
package org.seasar.kvasir.plust.model;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.model.ImportModel;

import net.skirnir.xom.Element;


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
        } else if (element instanceof Element) {
            Element e = (Element)element;
            return e.getName();
        }
        return super.getText(element);
    }


    public Image getImage(Object element)
    {
        if (element instanceof ImportModel) {
            return KvasirPlugin.getDefault()
                .getImage(KvasirPlugin.IMG_REQUIRED);
        } else if (element instanceof LibraryModel) {
            return KvasirPlugin.getDefault()
                .getImage(KvasirPlugin.IMG_REQUIRED);
        } else if (element instanceof ExtensionModel) {
            return KvasirPlugin.getDefault()
            .getImage(KvasirPlugin.IMG_EXTENSION);
        } else if (element instanceof ExtensionPointModel) {
            return KvasirPlugin.getDefault()
            .getImage(KvasirPlugin.IMG_EXTENSION_POINT);
        } else if (element instanceof Element) {
            return KvasirPlugin.getDefault().getImage(KvasirPlugin.IMG_ELEMENT);
        }
        return super.getImage(element);
    }
}
