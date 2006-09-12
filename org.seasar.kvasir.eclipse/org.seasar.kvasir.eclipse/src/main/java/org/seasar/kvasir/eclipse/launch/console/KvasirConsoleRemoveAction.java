package org.seasar.kvasir.eclipse.launch.console;

import org.eclipse.jface.action.Action;
import org.seasar.kvasir.eclipse.KvasirPlugin;


public class KvasirConsoleRemoveAction extends Action
{
    public KvasirConsoleRemoveAction()
    {
        setToolTipText(KvasirPlugin.getString("KvasirConsoleRemoveAction.CLOSE_CONSOLE")); //$NON-NLS-1$
        setImageDescriptor(KvasirPlugin.getImageDescriptor("icons/kvasir.gif")); //$NON-NLS-1$
    }


    public void run()
    {
        KvasirConsoleFactory.closeConsole();
    }
}
