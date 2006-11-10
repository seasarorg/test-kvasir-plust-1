/**
 * 
 */
package org.seasar.kvasir.plust.form;

import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;


/**
 * @author shida
 *
 */
public abstract class KvasirFormPage extends FormPage
{
    private IEditorCommandStack commandStack;

    private PluginDescriptor descriptor;
    
    public KvasirFormPage(FormEditor editor, PluginDescriptor descriptor,
        String id, String name)
    {
        super(editor, id, name);
        this.commandStack = (IEditorCommandStack)editor
            .getAdapter(IEditorCommandStack.class);
        this.descriptor = descriptor;
    }

    protected IEditorCommandStack getCommandStack() {
        return this.commandStack;
    }
    
    protected PluginDescriptor getDescriptor() {
        return this.descriptor;
    }
}
