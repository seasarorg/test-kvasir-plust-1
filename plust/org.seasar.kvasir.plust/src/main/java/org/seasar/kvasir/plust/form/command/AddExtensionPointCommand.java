/**
 * 
 */
package org.seasar.kvasir.plust.form.command;

import org.eclipse.jdt.core.IType;
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
        point = new ExtensionPointModel();
        point.setId(id);
        point.setClassName(extension.getFullyQualifiedName());
        root.addExtensionPoint(point);
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
