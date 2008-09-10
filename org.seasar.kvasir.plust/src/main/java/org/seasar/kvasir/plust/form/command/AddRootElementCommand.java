/**
 *
 */
package org.seasar.kvasir.plust.form.command;

import org.seasar.kvasir.plust.model.ExtensionElementModel;
import org.seasar.kvasir.plust.model.ExtensionModel;


/**
 * @author shidat
 *
 */
public class AddRootElementCommand
    implements IEditorCommand
{

    private ExtensionModel model;

    private ExtensionElementModel element;


    public AddRootElementCommand(ExtensionModel model)
    {
        this.model = model;
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#execute()
     */
    public void execute()
    {
        element = model.addRootElement();
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#redo()
     */
    public void redo()
    {

    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#undo()
     */
    public void undo()
    {
        model.removeRootElement(element);
    }

}
