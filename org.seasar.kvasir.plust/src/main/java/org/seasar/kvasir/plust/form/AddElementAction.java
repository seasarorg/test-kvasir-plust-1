/**
 * 
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.jface.action.Action;
import org.seasar.kvasir.plust.Messages;
import org.seasar.kvasir.plust.form.command.AddElementCommand;
import org.seasar.kvasir.plust.form.command.IEditorCommandStack;
import org.seasar.kvasir.plust.model.ExtensionElementModel;

/**
 * @author shidat
 *
 */
public class AddElementAction extends Action
{

    private String name;
    
    private IEditorCommandStack stack;
    
    private ExtensionElementModel model;

    public AddElementAction(String name, IEditorCommandStack stack, ExtensionElementModel model)
    {
        super();
        this.name = name;
        this.stack = stack;
        this.model = model;
        setText(name + Messages.getString("AddElementAction.0")); //$NON-NLS-1$
    }
    
    public void run()
    {
        AddElementCommand command = new AddElementCommand(model, name);
        stack.execute(command);
    }
}
