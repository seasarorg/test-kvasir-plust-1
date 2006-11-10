package org.seasar.kvasir.plust.launch.console;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleFactory;
import org.eclipse.ui.console.IConsoleManager;
import org.seasar.kvasir.plust.KvasirPlugin;


/**
 * Console factory is used to show the console from the Console view "Open Console"
 * drop-down action. This factory is registered via the org.eclipse.ui.console.consoleFactory 
 * extension point.
 * 
 *  @author Dmitri Maximovich
 *  @author YOKOTA Takehiko
 */
public class KvasirConsoleFactory
    implements IConsoleFactory
{
    public void openConsole()
    {
        showConsole();
    }


    public static void showConsole()
    {
        KvasirConsole console = KvasirPlugin.getDefault().getConsole();
        if (console != null) {
            IConsoleManager manager = ConsolePlugin.getDefault()
                .getConsoleManager();
            IConsole[] existing = manager.getConsoles();
            boolean exists = false;
            for (int i = 0; i < existing.length; i++) {
                if (console == existing[i])
                    exists = true;
            }
            if (!exists) {
                manager.addConsoles(new IConsole[] { console });
            }
            manager.showConsoleView(console);
        }
    }


    public static void closeConsole()
    {
        IConsoleManager manager = ConsolePlugin.getDefault()
            .getConsoleManager();
        KvasirConsole console = KvasirPlugin.getDefault().getConsole();
        if (console != null) {
            manager.removeConsoles(new IConsole[] { console });
            ConsolePlugin.getDefault().getConsoleManager().addConsoleListener(
                console.new MyLifecycle());
        }
    }
}
