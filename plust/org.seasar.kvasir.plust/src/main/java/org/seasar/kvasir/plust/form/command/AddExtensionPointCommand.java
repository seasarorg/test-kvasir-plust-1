/**
 *
 */
package org.seasar.kvasir.plust.form.command;

import org.eclipse.jdt.core.IType;
import org.seasar.kvasir.base.util.XOMUtils;
import org.seasar.kvasir.plust.IKvasirProject;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.model.ExtensionPointModel;
import org.seasar.kvasir.plust.model.PluginModel;

import net.skirnir.xom.BeanAccessor;


/**
 * @author shidat
 * @author skirnir
 *
 */
public class AddExtensionPointCommand
    implements IEditorCommand
{

    private PluginModel root;

    private IType extension;

    private ExtensionPointModel point;

    private IKvasirProject project;


    public AddExtensionPointCommand(PluginModel root, IType extension,
        IKvasirProject project)
    {
        super();
        this.root = root;
        this.extension = extension;
        this.project = project;
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.IEditorCommand#execute()
     */
    public void execute()
    {
        String id;
        try {
            Class<?> elementClass = project.getProjectClassLoader().loadClass(
                extension.getFullyQualifiedName());
            BeanAccessor accessor = XOMUtils.newMapper().getBeanAccessor(
                elementClass);
            id = root.getPluginId() + "." + toMultiple(accessor.getBeanName());
        } catch (Throwable t) {
            KvasirPlugin.getDefault().log(
                "Can't gather extensionPoint Element's information", t);
            id = root.getPluginId() + "."
                + toMultiple(extension.getElementName());
        }
        point = new ExtensionPointModel();
        point.setId(id);
        point.setClassName(extension.getFullyQualifiedName());
        root.addExtensionPoint(point);
    }


    String toMultiple(String name)
    {
        if (name == null) {
            return null;
        }

        if (name.endsWith("y")) {
            return name.substring(0, name.length() - 1) + "ies";
        } else if (name.endsWith("x") || name.endsWith("ch")) {
            return name + "es";
        } else {
            return name + "s";
        }
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
