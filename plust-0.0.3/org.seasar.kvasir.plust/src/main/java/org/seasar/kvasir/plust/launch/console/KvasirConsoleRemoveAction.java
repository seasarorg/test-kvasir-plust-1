package org.seasar.kvasir.plust.launch.console;

import org.eclipse.jface.action.Action;
import org.seasar.kvasir.plust.KvasirPlugin;


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
