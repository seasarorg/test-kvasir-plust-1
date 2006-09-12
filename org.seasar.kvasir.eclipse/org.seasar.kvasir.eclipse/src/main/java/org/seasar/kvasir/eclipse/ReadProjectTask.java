package org.seasar.kvasir.eclipse;

import org.apache.maven.artifact.resolver.AbstractArtifactResolutionException;
import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.project.ProjectBuildingException;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.maven.ide.eclipse.MavenEmbedderCallback;
import org.maven.ide.eclipse.Messages;
import org.maven.ide.eclipse.TransferListenerAdapter;


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
            TransferListenerAdapter listener = new TransferListenerAdapter(
                monitor);
            return mavenEmbedder.readProjectWithDependencies(pomFile_
                .getLocation().toFile(), listener);
        } catch (ProjectBuildingException ex) {
            handleProjectBuildingException(ex);
            return null;
        } catch (AbstractArtifactResolutionException ex) {
            String name = ex.getGroupId() + ":" + ex.getArtifactId() + "-" //$NON-NLS-1$ //$NON-NLS-2$
                + ex.getVersion() + "." + ex.getType(); //$NON-NLS-1$
            String msg = ex.getOriginalMessage() + " " + name; //$NON-NLS-1$
            KvasirPlugin.getDefault().addMarker(this.pomFile_, msg, 1,
                IMarker.SEVERITY_ERROR);
            KvasirPlugin.getDefault().getConsole().logError(msg);

            try {
                return mavenEmbedder.readProject(pomFile_.getLocation().toFile());
            } catch (ProjectBuildingException ex2) {
                handleProjectBuildingException(ex2);
                return null;
            }
        } finally {
            monitor.done();
        }
    }


    protected void handleProjectBuildingException(ProjectBuildingException ex)
    {
        Throwable cause = ex.getCause();
        if (cause instanceof XmlPullParserException) {
            XmlPullParserException pex = (XmlPullParserException)cause;
            String msg = Messages.getString("plugin.markerParsingError") //$NON-NLS-1$
                + pex.getMessage();
            KvasirPlugin.getDefault().addMarker(this.pomFile_, msg,
                pex.getLineNumber(), IMarker.SEVERITY_ERROR); //$NON-NLS-1$
            KvasirPlugin.getDefault().getConsole().logError(
                msg + " at line " + pex.getLineNumber()); //$NON-NLS-1$
        } else {
            String msg = Messages.getString("plugin.markerBuildError") //$NON-NLS-1$
                + ex.getMessage();
            KvasirPlugin.getDefault().addMarker(this.pomFile_, msg, 1,
                IMarker.SEVERITY_ERROR); //$NON-NLS-1$
            KvasirPlugin.getDefault().getConsole().logError(msg);
        }
    }
}
