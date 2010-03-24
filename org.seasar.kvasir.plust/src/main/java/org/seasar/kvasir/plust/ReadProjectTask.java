package org.seasar.kvasir.plust;

import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.seasar.kvasir.plust.maven.MavenEmbedderCallback;


public class ReadProjectTask
    implements MavenEmbedderCallback
{
    private final IFile pomFile_;


    public ReadProjectTask(IFile pomFile)
    {
        pomFile_ = pomFile;
    }


    public Object run(MavenEmbedder mavenEmbedder, IProgressMonitor monitor)
    {
        monitor.beginTask("Reading " + pomFile_.getLocation(), //$NON-NLS-1$
            IProgressMonitor.UNKNOWN);
        try {
            if (!pomFile_.exists()) {
                return null;
            }
            monitor.subTask("Reading " + pomFile_.getLocation()); //$NON-NLS-1$
            MavenExecutionRequest request = new DefaultMavenExecutionRequest();
            request.setPom(pomFile_.getLocation().toFile());
            request.setTransferListener(new TransferListenerAdapter(monitor));
            MavenExecutionResult result = mavenEmbedder
                .readProjectWithDependencies(request);
            return result.getProject();
        } finally {
            monitor.done();
        }
    }
}
