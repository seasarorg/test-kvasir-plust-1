/**
 * 
 */
package org.seasar.kvasir.plust.form.command;

import org.seasar.kvasir.plust.model.ExtensionPointModel;

/**
 * @author shidat
 *
 */
public class UpdateExtensionPointCommand
    implements IEditorCommand
{

    private String oldId, id;
    
    private String oldName, className;
    
    private String oldDesc, description;
    
    private ExtensionPointModel model;
    
    
    public UpdateExtensionPointCommand(String id, String className, String description, ExtensionPointModel model)
    {
        super();
        this.id = id;
        this.className = className;
        this.description = description;
        this.model = model;
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#execute()
     */
    public void execute()
    {
        oldId = model.getId();
        oldName = model.getClassName();
        oldDesc = model.getDescription();
        
        model.setId(id);
        model.setClassName(className);
        model.setDescription(description);
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#redo()
     */
    public void redo()
    {
        model.setId(id);
        model.setClassName(className);
        model.setDescription(description);
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommand#undo()
     */
    public void undo()
    {
        model.setId(oldId);
        model.setClassName(oldName);
        model.setDescription(oldDesc);
    }

}
