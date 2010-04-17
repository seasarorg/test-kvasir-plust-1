package org.seasar.kvasir.plust.builder;

import java.util.List;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionResult;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.seasar.kvasir.plust.KvasirPlugin;
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
            if (result.hasExceptions()) {
                for (Exception ex : (List<Exception>)result.getExceptions()) {
                    KvasirPlugin
                        .getDefault()
                        .log(
                            "[ONLINE MODE] Cannot gather required plugin artifacts",
                            ex);
                }

                // オフライン時にリモートリポジトリへのアクセスでエラーになっていることがあるため、
                // オフラインモードでリトライする。
                request.setOffline(true);
                result = mavenEmbedder.readProjectWithDependencies(request);
                if (result.hasExceptions()) {
                    for (Exception ex : (List<Exception>)result.getExceptions()) {
                        KvasirPlugin
                            .getDefault()
                            .log(
                                "[OFFLINE MODE] Cannot gather required plugin artifacts",
                                ex);
                    }
                    // オフラインでもだめなら処理を継続できない。
                    throw new RuntimeException();
                }
            }
            MavenProject pom = result.getProject();

            return (Artifact[])pom.getArtifacts().toArray(new Artifact[0]);
        } finally {
            monitor.done();
        }
    }
}
