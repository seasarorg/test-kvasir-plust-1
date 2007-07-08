package org.seasar.kvasir.plust.builder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.maven.embedder.MavenEmbedder;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.maven.ide.eclipse.MavenEmbedderCallback;
import org.seasar.kvasir.base.Version;
import org.seasar.kvasir.base.plugin.descriptor.Import;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.descriptor.Requires;
import org.seasar.kvasir.plust.IKvasirProject;
import org.seasar.kvasir.plust.KvasirPlugin;


public class UpdatePomXMLTask
    implements MavenEmbedderCallback
{
    private IProject project_;

    private IFile pomFile_;

    private PluginDescriptor descriptor_;

    private String defaultVersion_ = "RELEASE";


    public UpdatePomXMLTask(IProject project, PluginDescriptor descriptor,
        String defaultVersion)
    {
        project_ = project;
        pomFile_ = project.getFile(IKvasirProject.POM_FILE_PATH);
        descriptor_ = descriptor;
        if (defaultVersion != null) {
            defaultVersion_ = defaultVersion;
        }
    }


    @SuppressWarnings("unchecked")
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

            // プラグインのバージョン情報を更新する。
            String pluginVersion = descriptor_.getVersionString();
            if (pluginVersion != null) {
                Parent parent = pom.getParent();
                if (parent == null
                    || !pluginVersion.equals(parent.getVersion())) {
                    pom.setVersion(pluginVersion);
                }
            }

            // 依存pluginの情報を更新する。
            Import[] imports;
            Requires requires = descriptor_.getRequires();
            if (requires != null) {
                imports = requires.getImports();
            } else {
                imports = new Import[0];
            }

            List<Dependency> curDependencies = pom.getDependencies();
            List<Dependency> newDependencies = new ArrayList<Dependency>(
                curDependencies.size() + imports.length);
            Map<String, String> pluginVersionMap = new HashMap<String, String>();
            for (Iterator<Dependency> itr = curDependencies.iterator(); itr
                .hasNext();) {
                Dependency dependency = itr.next();
                if ("zip".equals(dependency.getType())) {
                    pluginVersionMap.put(dependency.getArtifactId(), dependency
                        .getVersion());
                }
            }
            for (Iterator<Dependency> itr = curDependencies.iterator(); itr
                .hasNext();) {
                Dependency dependency = itr.next();
                String groupId = dependency.getGroupId();
                String artifactId = dependency.getArtifactId();
                if (!(groupId.equals(artifactId) && pluginVersionMap
                    .containsKey(artifactId))) {
                    // プラグイン以外のdependencyは残すようにする。
                    newDependencies.add(dependency);
                }
            }
            for (int i = 0; i < imports.length; i++) {
                Dependency dependency = new Dependency();
                String plugin = imports[i].getPlugin();
                String version = imports[i].getVersionString();
                if (version == null) {
                    version = (String)pluginVersionMap.get(plugin);
                    if (version == null) {
                        version = getVersionFromTestEnvironment(plugin);
                        if (version == null) {
                            version = defaultVersion_;
                        }
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


    String getVersionFromTestEnvironment(String artifactId)
    {
        String prefix = artifactId + "-";
        IFolder plugins = project_.getFolder(IKvasirProject.TEST_PLUGINS_PATH);
        if (!plugins.exists()) {
            return null;
        }
        IResource[] children;
        try {
            children = plugins.members();
        } catch (CoreException ex) {
            KvasirPlugin.getDefault().log(ex);
            return null;
        }
        List<Version> versionList = new ArrayList<Version>();
        for (int i = 0; i < children.length; i++) {
            IResource child = children[i];
            String name = child.getName();
            if (name.startsWith(prefix)) {
                versionList.add(new Version(name.substring(prefix.length())));
            }
        }
        Version[] versions = versionList.toArray(new Version[0]);
        if (versions.length == 0) {
            return null;
        } else {
            Arrays.sort(versions);
            return versions[versions.length - 1].getString();
        }
    }
}
