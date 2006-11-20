package org.seasar.kvasir.plust.form.command;

public interface IEditorCommandStack
{

    void execute(IEditorCommand command);


    void undo();


    void redo();


    void clear();


    void addCommandStackListener(IEditorCommandStackListener listener);


    void removeCommandStackListener(IEditorCommandStackListener listener);
}
