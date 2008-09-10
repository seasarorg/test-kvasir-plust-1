/**
 * 
 */
package org.seasar.kvasir.plust.form.command;

import org.seasar.kvasir.plust.model.ImportModel;
import org.seasar.kvasir.plust.model.PluginModel;

/**
 * @author shidat
 *
 */
public class RemoveImportCommand
    implements IEditorCommand
{
    private ImportModel importModel;
    
    private PluginModel pluginModel;
    
    
    public RemoveImportCommand(ImportModel importModel, PluginModel pluginModel)
    {
        super();
        this.importModel = importModel;
        this.pluginModel = pluginModel;
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#execute()
     */
    public void execute()
    {
        pluginModel.removeRequire(importModel);
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#redo()
     */
    public void redo()
    {
        pluginModel.removeRequire(importModel);
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#undo()
     */
    public void undo()
    {
        pluginModel.addRequire(importModel);
    }

}
