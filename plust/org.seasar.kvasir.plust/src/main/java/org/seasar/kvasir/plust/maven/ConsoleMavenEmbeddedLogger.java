package org.seasar.kvasir.plust.maven;

import org.apache.maven.embedder.MavenEmbedderLogger;
import org.seasar.kvasir.plust.launch.console.KvasirConsole;


public class ConsoleMavenEmbeddedLogger
    implements MavenEmbedderLogger
{
    private int                 treshold_ = LEVEL_DEBUG;

    private final KvasirConsole console_;


    public ConsoleMavenEmbeddedLogger()
    {
        this(null);
    }


    public ConsoleMavenEmbeddedLogger(KvasirConsole console)
    {
        console_ = console;
    }


    private void out(String s)
    {
        if (console_ != null) {
            console_.logMessage(s);
        } else {
            System.out.println(s);
        }
    }


    private void outError(String s)
    {
        if (console_ != null) {
            console_.logError(s);
        } else {
            System.out.println(s);
        }
    }


    public void debug(String msg)
    {
        if (isDebugEnabled()) {
            out("[DEBUG] " + msg);
        }
    }


    public void debug(String msg, Throwable t)
    {
        if (isDebugEnabled()) {
            out("[DEBUG] " + msg + " " + t.getMessage());
        }
    }


    public void info(String msg)
    {
        if (isInfoEnabled()) {
            out("[INFO] " + msg);
        }
    }


    public void info(String msg, Throwable t)
    {
        if (isInfoEnabled()) {
            out("[INFO] " + msg + " " + t.getMessage());
        }
    }


    public void warn(String msg)
    {
        if (isWarnEnabled()) {
            out("[WARN] " + msg);
        }
    }


    public void warn(String msg, Throwable t)
    {
        if (isWarnEnabled()) {
            out("[WARN] " + msg + " " + t.getMessage());
        }
    }


    public void fatalError(String msg)
    {
        if (isFatalErrorEnabled()) {
            outError("[FATAL ERROR] " + msg);
        }
    }


    public void fatalError(String msg, Throwable t)
    {
        if (isFatalErrorEnabled()) {
            outError("[FATAL ERROR] " + msg + " " + t.getMessage());
        }
    }


    public void error(String msg)
    {
        if (isErrorEnabled()) {
            outError("[ERROR] " + msg);
        }
    }


    public void error(String msg, Throwable t)
    {
        if (isErrorEnabled()) {
            outError("[ERROR] " + msg + " " + t.getMessage());
        }
    }


    public boolean isDebugEnabled()
    {
        return treshold_ <= LEVEL_DEBUG;
    }


    public boolean isInfoEnabled()
    {
        return treshold_ <= LEVEL_INFO;
    }


    public boolean isWarnEnabled()
    {
        return treshold_ <= LEVEL_WARN;
    }


    public boolean isErrorEnabled()
    {
        return treshold_ <= LEVEL_ERROR;
    }


    public boolean isFatalErrorEnabled()
    {
        return treshold_ <= LEVEL_FATAL;
    }


    public void setThreshold(int treshold)
    {
        treshold_ = treshold;
    }


    public int getThreshold()
    {
        return treshold_;
    }
}
