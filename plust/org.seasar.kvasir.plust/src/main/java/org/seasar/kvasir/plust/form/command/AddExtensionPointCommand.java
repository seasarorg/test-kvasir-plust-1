/**
 * 
 */
package org.seasar.kvasir.plust.form.command;

import org.eclipse.jdt.core.IType;
import org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;


/**
 * @author shidat
 *
 */
public class AddExtensionPointCommand
    implements IEditorCommand
{

    private PluginDescriptor descriptor;

    private IType extension;

    private ExtensionPoint point;


    public AddExtensionPointCommand(PluginDescriptor descriptor, IType extension)
    {
        super();
        this.descriptor = descriptor;
        this.extension = extension;
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.IEditorCommand#execute()
     */
    public void execute()
    {
        String id = extension.getPackageFragment().getElementName();
        point = new ExtensionPoint();
        point.setId(id);
        point.setElementClassName(extension.getFullyQualifiedName());
        descriptor.addExtensionPoint(point);
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.IEditorCommand#undo()
     */
    public void undo()
    {
        //TODO remove extension.
    }


    public void redo()
    {
        descriptor.addExtensionPoint(point);
    }

}
