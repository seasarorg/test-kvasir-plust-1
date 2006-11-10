package org.seasar.kvasir.plust.builder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.maven.ide.eclipse.MavenEmbedderCallback;
import org.seasar.kvasir.plust.KvasirPlugin;


public class UpdatePluginDependenciesTask
    implements MavenEmbedderCallback
{
    private IFile pomFile_;

    private Import[] imports_;

    private String defaultVersion_ = "RELEASE";


    public UpdatePluginDependenciesTask(IFile pomFile, Import[] imports,
        String defaultVersion)
    {
        pomFile_ = pomFile;
        imports_ = imports;
        if (defaultVersion != null) {
            defaultVersion_ = defaultVersion;
        }
    }


    public Object run(MavenEmbedder mavenEmbedder, IProgressMonitor monitor)
    {
        monitor.beginTask("Updating plugin dependencies", 1);
        try {
            File pomFile = pomFile_.getLocation().toFile();
            Model pom;
            try {
                pom = mavenEmbedder.readModel(pomFile);
            } catch (IOException ex) {
                KvasirPlugin.getDefault().log(ex);
                return null;
            } catch (XmlPullParserException ex) {
                KvasirPlugin.getDefault().log(ex);
                return null;
            }

            List curDependencies = pom.getDependencies();
            List newDependencies = new ArrayList(curDependencies.size()
                + imports_.length);
            Map pluginVersionMap = new HashMap();
            for (Iterator itr = curDependencies.iterator(); itr.hasNext();) {
                Dependency dependency = (Dependency)itr.next();
                if ("zip".equals(dependency.getType())) {
                    pluginVersionMap.put(dependency.getArtifactId(), dependency
                        .getVersion());
                }
            }
            for (Iterator itr = curDependencies.iterator(); itr.hasNext();) {
                Dependency dependency = (Dependency)itr.next();
                String groupId = dependency.getGroupId();
                String artifactId = dependency.getArtifactId();
                if (!(groupId.equals(artifactId) && pluginVersionMap
                    .containsKey(artifactId))) {
                    // プラグイン以外のdependencyは残すようにする。
                    newDependencies.add(dependency);
                }
            }
            for (int i = 0; i < imports_.length; i++) {
                Dependency dependency = new Dependency();
                String plugin = imports_[i].getPlugin();
                String version = imports_[i].getVersion();
                if (version == null) {
                    version = (String)pluginVersionMap.get(plugin);
                    if (version == null) {
                        version = defaultVersion_;
                    }
                }
                dependency.setGroupId(plugin);
                dependency.setArtifactId(plugin);
                dependency.setVersion(version);
                newDependencies.add(dependency);

                dependency = new Dependency();
                dependency.setGroupId(plugin);
                dependency.setArtifactId(plugin);
                dependency.setVersion(version);
                dependency.setScope("runtime");
                dependency.setType("zip");
                newDependencies.add(dependency);
            }
            pom.setDependencies(newDependencies);

            OutputStream os = null;
            try {
                os = new FileOutputStream(pomFile);
                Writer writer = new BufferedWriter(new OutputStreamWriter(os,
                    "UTF-8"));
                mavenEmbedder.writeModel(writer, pom);
                writer.close();
                os = null;
            } catch (IOException ex) {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException ex1) {
                        KvasirPlugin.getDefault().log(ex);
                    }
                }
            }
            try {
                pomFile_.refreshLocal(IResource.DEPTH_ZERO,
                    new SubProgressMonitor(monitor, 1));
            } catch (CoreException ex) {
                KvasirPlugin.getDefault().log(ex);
            }
        } finally {
            monitor.done();
        }

        return null;
    }
}
