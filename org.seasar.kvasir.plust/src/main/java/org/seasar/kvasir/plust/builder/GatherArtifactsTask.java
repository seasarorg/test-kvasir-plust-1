package org.seasar.kvasir.plust.builder;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.seasar.kvasir.plust.TransferListenerAdapter;
import org.seasar.kvasir.plust.maven.MavenEmbedderCallback;


public class GatherArtifactsTask
    implements MavenEmbedderCallback
{
    private IFile pomFile_;


    public GatherArtifactsTask(IFile pomFile)
    {
        pomFile_ = pomFile;
    }


    @SuppressWarnings("unchecked")
    public Object run(MavenEmbedder mavenEmbedder, IProgressMonitor monitor)
    {
        monitor.beginTask("Gathering required plugin artifacts",
            IProgressMonitor.UNKNOWN);
        try {
            if (!pomFile_.exists()) {
                return null;
            }
            MavenExecutionRequest request = new DefaultMavenExecutionRequest();
            request.setPom(pomFile_.getLocation().toFile());
            request.setTransferListener(new TransferListenerAdapter(monitor));
            MavenExecutionResult result = mavenEmbedder
                .readProjectWithDependencies(request);
            MavenProject pom = result.getProject();

            return (Artifact[])pom.getArtifacts().toArray(new Artifact[0]);
        } finally {
            monitor.done();
        }
    }
}
