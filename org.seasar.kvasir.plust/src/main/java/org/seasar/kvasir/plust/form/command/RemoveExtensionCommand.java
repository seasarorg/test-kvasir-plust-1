/**
 * 
 */
package org.seasar.kvasir.plust.form.command;

import org.seasar.kvasir.plust.model.ExtensionModel;
import org.seasar.kvasir.plust.model.PluginModel;

/**
 * @author shidat
 *
 */
public class RemoveExtensionCommand
    implements IEditorCommand
{

    private PluginModel pluginModel;
    
    private ExtensionModel extensionModel;
    
    
    public RemoveExtensionCommand(PluginModel pluginModel, ExtensionModel extensionModel)
    {
        super();
        this.pluginModel = pluginModel;
        this.extensionModel = extensionModel;
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#execute()
     */
    public void execute()
    {
        pluginModel.removeExtension(extensionModel);
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#redo()
     */
    public void redo()
    {
        pluginModel.removeExtension(extensionModel);
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#undo()
     */
    public void undo()
    {
        pluginModel.addExtension(extensionModel);
    }

}
