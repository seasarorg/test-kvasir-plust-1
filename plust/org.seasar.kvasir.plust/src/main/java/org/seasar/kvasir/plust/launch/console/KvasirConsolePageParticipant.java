package org.seasar.kvasir.plust.launch.console;

import org.eclipse.ui.IActionBars;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleConstants;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.part.IPageBookViewPage;


public class KvasirConsolePageParticipant
    implements IConsolePageParticipant
{
    private KvasirConsole console_;

    private IPageBookViewPage page_;

    private KvasirConsoleRemoveAction consoleRemoveAction_;


    public void init(IPageBookViewPage page, IConsole console)
    {
        console_ = (KvasirConsole)console;
        page_ = page;
        consoleRemoveAction_ = new KvasirConsoleRemoveAction();
        IActionBars bars = page.getSite().getActionBars();
        bars.getToolBarManager().appendToGroup(IConsoleConstants.LAUNCH_GROUP,
            consoleRemoveAction_);
    }


    public void dispose()
    {
        consoleRemoveAction_ = null;
        page_ = null;
        console_ = null;
    }


    public void activated()
    {
    }


    public void deactivated()
    {
    }


    @SuppressWarnings("unchecked")
    public Object getAdapter(Class adapter)
    {
        return null;
    }
}
