/**
 *
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.jface.action.Action;
import org.seasar.kvasir.plust.Messages;
import org.seasar.kvasir.plust.form.command.IEditorCommandStack;
import org.seasar.kvasir.plust.form.command.RemoveElementCommand;
import org.seasar.kvasir.plust.model.ExtensionElementModel;


/**
 * @author shidat
 *
 */
public class RemoveElementAction extends Action
{

    private IEditorCommandStack stack;

    private ExtensionElementModel model;


    public RemoveElementAction(IEditorCommandStack stack,
        ExtensionElementModel model)
    {
        super();
        this.stack = stack;
        this.model = model;
        setText(Messages.getString("RemoveElementAction.0")); //$NON-NLS-1$
        // 元々は「親がnullでなかったらenabled」のようになっていたが、ExtensionModel直下の
        // ExtensionElementModelは親がnullだが削除できるようにしたいのでこうした。（skirnir）
        setEnabled(model.isRoot() || model.getParent() != null);
    }


    public void run()
    {
        RemoveElementCommand command = new RemoveElementCommand(model);
        stack.execute(command);
    }
}
