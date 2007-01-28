/**
 * 
 */
package org.seasar.kvasir.plust.form.command;

import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.KvasirProject;
import org.seasar.kvasir.plust.model.ExtensionPointModel;
import org.seasar.kvasir.plust.model.PluginModel;


/**
 * @author shidat
 *
 */
public class AddExtensionPointCommand
    implements IEditorCommand
{

    private PluginModel root;

    private IType extension;

    private ExtensionPointModel point;


    public AddExtensionPointCommand(PluginModel root, IType extension)
    {
        super();
        this.root = root;
        this.extension = extension;
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.IEditorCommand#execute()
     */
    public void execute()
    {
        String id = extension.getPackageFragment().getElementName();
     //   id  = toUpperCase(id);
        point = new ExtensionPointModel();
        point.setId(id);
        point.setClassName(extension.getFullyQualifiedName());
        root.addExtensionPoint(point);
    }

    private String toUpperCase(String source) {
        String newStr = source.substring(1);
        newStr = source.substring(0,1).toUpperCase() + newStr;
        if (newStr.endsWith("y")) {
            newStr = newStr.substring(0, newStr.length() - 1) + "ies";
        } else {
            newStr = newStr + "s";
        }
        return newStr;
    }

    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.IEditorCommand#undo()
     */
    public void undo()
    {
        root.removeExtensionPoint(point);
    }


    public void redo()
    {
        root.addExtensionPoint(point);
    }

}
