package org.seasar.kvasir.plust.form;

public interface IEditorCommandStack
{

    void execute(IEditorCommand command);
    
    void undo();
    
    void redo();
    
    void clear();
}
