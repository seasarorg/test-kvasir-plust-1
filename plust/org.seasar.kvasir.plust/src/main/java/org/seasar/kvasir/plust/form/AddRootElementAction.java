/**
 * 
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.jface.action.Action;
import org.seasar.kvasir.plust.Messages;
import org.seasar.kvasir.plust.form.command.AddRootElementCommand;
import org.seasar.kvasir.plust.form.command.IEditorCommandStack;
import org.seasar.kvasir.plust.model.ExtensionModel;

/**
 * @author shidat
 *
 */
public class AddRootElementAction extends Action
{
    private ExtensionModel model;
    private IEditorCommandStack stack;
    
    public AddRootElementAction(String name, ExtensionModel model, IEditorCommandStack stack)
    {
        super(name + Messages.getString("AddRootElementAction.0")); //$NON-NLS-1$
        this.model = model;
        this.stack = stack;
    }
    
    public void run()
    {
        super.run();
        AddRootElementCommand command = new AddRootElementCommand(model);
        stack.execute(command);
    }
}
