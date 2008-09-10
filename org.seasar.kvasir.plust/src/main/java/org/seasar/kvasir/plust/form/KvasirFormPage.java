/**
 * 
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.seasar.kvasir.plust.form.command.IEditorCommandStack;
import org.seasar.kvasir.plust.model.PluginModel;


/**
 * @author shida
 *
 */
public abstract class KvasirFormPage extends FormPage
{
    private IEditorCommandStack commandStack;

    private PluginModel pluginRoot;


    public KvasirFormPage(FormEditor editor, PluginModel descriptor,
        String id, String name)
    {
        super(editor, id, name);
        this.commandStack = (IEditorCommandStack)editor
            .getAdapter(IEditorCommandStack.class);
        this.pluginRoot = descriptor;
    }


    protected IEditorCommandStack getCommandStack()
    {
        return this.commandStack;
    }


    protected PluginModel getDescriptor()
    {
        return this.pluginRoot;
    }
}
