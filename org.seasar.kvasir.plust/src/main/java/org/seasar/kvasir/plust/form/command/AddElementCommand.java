/**
 * 
 */
package org.seasar.kvasir.plust.form.command;

import org.seasar.kvasir.plust.model.ExtensionElementModel;


/**
 * @author shidat
 *
 */
public class AddElementCommand
    implements IEditorCommand
{

    private ExtensionElementModel model;

    private String name;

    private ExtensionElementModel addedModel;


    public AddElementCommand(ExtensionElementModel model, String name)
    {
        super();
        this.model = model;
        this.name = name;
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#execute()
     */
    public void execute()
    {
        addedModel = model.addChild(name);
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#redo()
     */
    public void redo()
    {
        // TODO 自動生成されたメソッド・スタブ

    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#undo()
     */
    public void undo()
    {
        model.removeChild(name, addedModel);
    }

}
