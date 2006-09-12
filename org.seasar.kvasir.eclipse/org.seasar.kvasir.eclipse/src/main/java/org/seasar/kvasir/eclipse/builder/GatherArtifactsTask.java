package org.seasar.kvasir.eclipse.builder;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.maven.ide.eclipse.MavenEmbedderCallback;
import org.maven.ide.eclipse.TransferListenerAdapter;
import org.seasar.kvasir.eclipse.KvasirPlugin;


public class GatherArtifactsTask
    implements MavenEmbedderCallback
{
    private IFile pomFile_;


    public GatherArtifactsTask(IFile pomFile)
    {
        pomFile_ = pomFile;
    }


    public Object run(MavenEmbedder mavenEmbedder, IProgressMonitor monitor)
    {
        monitor.beginTask("Gathering required plugin artifacts",
            IProgressMonitor.UNKNOWN);
        try {
            if (!pomFile_.exists()) {
                return null;
            }
            TransferListenerAdapter listener = new TransferListenerAdapter(
                monitor);
            MavenProject pom = mavenEmbedder.readProjectWithDependencies(
                pomFile_.getLocation().toFile(), listener);

            return (Artifact[])pom.getArtifacts().toArray(new Artifact[0]);
        } catch (AbstractArtifactResolutionException ex) {
            KvasirPlugin.getDefault().log(ex);
            return null;
        } catch (ProjectBuildingException ex) {
            KvasirPlugin.getDefault().log(ex);
            return null;
        } finally {
            monitor.done();
        }
    }
}
