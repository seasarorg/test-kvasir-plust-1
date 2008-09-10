/**
 *
 */
package org.seasar.kvasir.plust.form.command;

import org.seasar.kvasir.plust.model.ExtensionElementModel;


/**
 * @author shidat
 *
 */
public class RemoveElementCommand
    implements IEditorCommand
{
    private ExtensionElementModel model;


    public RemoveElementCommand(ExtensionElementModel model)
    {
        super();
        this.model = model;
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#execute()
     */
    public void execute()
    {
        if (model.isRoot()) {
            model.getExtensionModel().removeRootElement(model);
        } else {
            ExtensionElementModel parent = model.getParent();
            parent.removeChild(model.getName(), model);
        }
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#redo()
     */
    public void redo()
    {
        execute();
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#undo()
     */
    public void undo()
    {
    }

}
