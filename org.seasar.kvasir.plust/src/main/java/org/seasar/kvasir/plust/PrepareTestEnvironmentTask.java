package org.seasar.kvasir.plust;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.project.MavenProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.seasar.kvasir.plust.maven.MavenEmbedderCallback;


public class PrepareTestEnvironmentTask
    implements MavenEmbedderCallback
{
    private final MavenProject project_;

    private String groupId_;

    private String artifactId_;

    private String version_;


    public PrepareTestEnvironmentTask(MavenProject project, String groupId,
        String artifactId, String version)
    {
        project_ = project;
        groupId_ = groupId;
        artifactId_ = artifactId;
        version_ = version;
    }


    public Object run(MavenEmbedder mavenEmbedder, IProgressMonitor monitor)
    {
        monitor.beginTask("Resolving", IProgressMonitor.UNKNOWN); //$NON-NLS-1$
        try {
            Artifact artifact = mavenEmbedder.createArtifact(groupId_,
                artifactId_, version_, null, //$NON-NLS-1$ //$NON-NLS-2$
                "zip"); //$NON-NLS-1$
            mavenEmbedder.resolve(artifact, project_
                .getRemoteArtifactRepositories(), mavenEmbedder
                .getLocalRepository());
            return artifact;
        } catch (AbstractArtifactResolutionException ex) {
            handleAbstractArtifactResolutionException(ex);
            return null;
        } finally {
            monitor.done();
        }
    }


    protected void handleAbstractArtifactResolutionException(
        AbstractArtifactResolutionException ex)
    {
        String name = ex.getGroupId() + ":" + ex.getArtifactId() + "-" //$NON-NLS-1$ //$NON-NLS-2$
            + ex.getVersion() + "." + ex.getType(); //$NON-NLS-1$
        String msg = ex.getOriginalMessage() + " " + name; //$NON-NLS-1$
        KvasirPlugin.getDefault().getConsole().logError(msg);
    }
}
