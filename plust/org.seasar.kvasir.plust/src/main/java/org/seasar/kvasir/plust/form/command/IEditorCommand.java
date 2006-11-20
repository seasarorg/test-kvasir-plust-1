/**
 * 
 */
package org.seasar.kvasir.plust.form.command;

/**
 * @author shida
 *
 */
public interface IEditorCommand
{

    void execute();


    void undo();


    void redo();
}
