package org.seasar.kvasir.plust.maven;

import org.apache.maven.embedder.MavenEmbedder;
import org.eclipse.core.runtime.IProgressMonitor;


public interface MavenEmbedderCallback
{
    Object run(MavenEmbedder mavenEmbedder, IProgressMonitor monitor);
}
