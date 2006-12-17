package org.seasar.kvasir.plust.editors;

import org.eclipse.ui.editors.text.TextEditor;
import org.seasar.kvasir.plust.Messages;


public class XMLEditor extends TextEditor
{

    private ColorManager colorManager;


    public XMLEditor()
    {
        super();
        colorManager = new ColorManager();
        setSourceViewerConfiguration(new XMLConfiguration(colorManager));
        setDocumentProvider(new XMLDocumentProvider());
        setPartName(Messages.getString("XMLEditor.0")); //$NON-NLS-1$
    }


    public void dispose()
    {
        colorManager.dispose();
        super.dispose();
    }

}
