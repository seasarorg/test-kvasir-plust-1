/**
 * 
 */
package org.seasar.kvasir.plust.form.command;

import org.seasar.kvasir.plust.model.PluginModel;

/**
 * @author shidat
 *
 */
public class UpdatePluginPropertyCommand
    implements IEditorCommand
{

    private String name;
    
    private PluginModel model;
    
    private String value;
    
    private String old;
    
    
    /**
     * @param name
     * @param model
     * @param value
     */
    public UpdatePluginPropertyCommand(String name, PluginModel model, String value)
    {
        super();
        this.name = name;
        this.model = model;
        this.value = value;
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#execute()
     */
    public void execute()
    {
        old = model.getValue(name);
        model.updateValue(name, value);
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
        model.updateValue(name, old);
    }

}
