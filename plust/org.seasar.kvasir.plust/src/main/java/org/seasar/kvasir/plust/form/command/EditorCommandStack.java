/**
 * 
 */
package org.seasar.kvasir.plust.form.command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;


/**
 * @author shidat
 *
 */
public class EditorCommandStack
    implements IEditorCommandStack
{

    private Stack<IEditorCommand> stack = new Stack<IEditorCommand>();

    private Stack<IEditorCommand> undoStack = new Stack<IEditorCommand>();

    private List<IEditorCommandStackListener> listeners = new ArrayList<IEditorCommandStackListener>();


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommandStack#addCommandStackListener(org.seasar.kvasir.plust.form.command.IEditorCommandStackListener)
     */
    public void addCommandStackListener(IEditorCommandStackListener listener)
    {
        listeners.add(listener);
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommandStack#clear()
     */
    public void clear()
    {
        stack.clear();
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommandStack#execute(org.seasar.kvasir.plust.form.command.IEditorCommand)
     */
    public void execute(IEditorCommand command)
    {
        command.execute();
        stack.push(command);
        notifyStackChanged();
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommandStack#redo()
     */
    public void redo()
    {
        if (!undoStack.isEmpty()) {
            IEditorCommand command = undoStack.pop();
            command.redo();
            notifyStackChanged();
        }

    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommandStack#removeCommandStackListener(org.seasar.kvasir.plust.form.command.IEditorCommandStackListener)
     */
    public void removeCommandStackListener(IEditorCommandStackListener listener)
    {
        listeners.remove(listener);
    }


    /* (non-Javadoc)
     * @see org.seasar.kvasir.plust.form.command.IEditorCommandStack#undo()
     */
    public void undo()
    {
        if (!stack.isEmpty()) {
            IEditorCommand command = stack.pop();
            command.undo();
            undoStack.push(command);
            notifyStackChanged();
        }
    }


    private void notifyStackChanged()
    {
        for (Iterator<IEditorCommandStackListener> iter = listeners.iterator(); iter
            .hasNext();) {
            IEditorCommandStackListener listener = iter.next();
            listener.fireCommandStachChanged();
        }
    }


    public boolean isDirty()
    {
        return !stack.isEmpty();
    }
}
