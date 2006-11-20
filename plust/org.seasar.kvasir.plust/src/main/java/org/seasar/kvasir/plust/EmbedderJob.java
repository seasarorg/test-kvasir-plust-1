package org.seasar.kvasir.plust;

import org.apache.maven.embedder.MavenEmbedder;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.maven.ide.eclipse.MavenEmbedderCallback;


public class EmbedderJob extends Job
{
    private final MavenEmbedderCallback template_;

    private final MavenEmbedder embedder_;

    private Object callbackResult_;


    public EmbedderJob(String name, MavenEmbedderCallback template,
        MavenEmbedder embedder)
    {
        super(name);
        template_ = template;
        embedder_ = embedder;
    }


    protected IStatus run(IProgressMonitor monitor)
    {
        callbackResult_ = template_.run(embedder_, monitor);
        return Status.OK_STATUS;
    }


    public Object getCallbackResult()
    {
        return callbackResult_;
    }
}
