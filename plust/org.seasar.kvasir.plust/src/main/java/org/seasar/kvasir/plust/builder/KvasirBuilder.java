package org.seasar.kvasir.plust.builder;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.ui.texteditor.MarkerUtilities;
import org.maven.ide.eclipse.container.Maven2ClasspathContainer;
import org.seasar.kvasir.plust.IKvasirProject;
import org.seasar.kvasir.plust.KvasirPlugin;
import org.seasar.kvasir.plust.PrepareTestEnvironmentTask;

import net.skirnir.freyja.TemplateEvaluator;
import net.skirnir.kvasir.m2.plugin.kvasir.ArtifactNotFoundException;
import net.skirnir.kvasir.m2.plugin.kvasir.ArtifactPattern;
import net.skirnir.kvasir.m2.plugin.kvasir.ArtifactUtils;


public class KvasirBuilder extends IncrementalProjectBuilder
{
    public static final String PLUGIN_OUTER_LIBRARIES_TAG = "<pluginOuterLibraries>";

    private TemplateEvaluator readPluginXmlEvaluator = new TemplateEvaluator(
        new ReadPluginXmlTagEvaluator(), null);

    private TemplateEvaluator writePluginXmlEvaluator = new TemplateEvaluator(
        new WritePluginXmlTagEvaluator(), null);


    /*
     * (non-Javadoc)
     * @see org.eclipse.core.internal.events.InternalBuilder#build(int,
     *      java.util.Map, org.eclipse.core.runtime.IProgressMonitor)
     */
    protected IProject[] build(int kind, Map args, IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Building Kvasir/Sora project",
            IProgressMonitor.UNKNOWN);
        try {
            IProject project = getProject();
            if (!project.hasNature(KvasirPlugin.NATURE_ID)) {
                return null;
            }

            IJavaProject javaProject = JavaCore.create(project);
            if (javaProject == null) {
                return null;
            }

            if (kind == INCREMENTAL_BUILD) {
                return null;
            }

            if (kind == AUTO_BUILD) {
                Verifier verifier = new Verifier();
                getDelta(project).accept(verifier, IContainer.EXCLUDE_DERIVED);

                boolean updatePomXml;
                boolean updateClassPath;
                boolean updateDependingPlugins;
                boolean updateLib;

                if (verifier.pluginXmlUpdated) {
                    updatePomXml = true;
                    updateClassPath = true;
                    updateDependingPlugins = true;
                    updateLib = false;
                } else if (verifier.pomXmlUpdated) {
                    updatePomXml = false;
                    updateClassPath = true;
                    updateDependingPlugins = false;
                    updateLib = true;
                } else {
                    updatePomXml = false;
                    updateClassPath = false;
                    updateDependingPlugins = false;
                    updateLib = false;
                }

                Artifact[] artifacts = null;
                if (updateDependingPlugins || updateLib) {
                    artifacts = KvasirPlugin.getDefault().gatherArtifacts(
                        project.getFile(KvasirPlugin.POM_FILE_NAME),
                        new SubProgressMonitor(monitor, 1));
                }

                if (updatePomXml) {
                    updatePomXml(project, new SubProgressMonitor(monitor, 1));
                }
                if (updateClassPath) {
                    updateClasspath(javaProject, new SubProgressMonitor(
                        monitor, 1));
                }
                if (updateDependingPlugins) {
                    deployRequiredPlugins(project, artifacts,
                        new SubProgressMonitor(monitor, 1));
                }
                if (updateLib) {
                    updateLib(project, artifacts, new SubProgressMonitor(
                        monitor, 1));
                }

                for (Iterator itr = verifier.removedPluginResources.iterator(); itr
                    .hasNext();) {
                    IResource destination = project
                        .findMember(toDestinationPath((IResource)itr.next()));
                    if (destination != null) {
                        destination.delete(false, new SubProgressMonitor(
                            monitor, 1));
                    }
                }

                for (Iterator itr = verifier.modifiedPluginResources.iterator(); itr
                    .hasNext();) {
                    IResource resource = (IResource)itr.next();
                    IPath destination = toDestinationPath(resource);
                    if (resource.getType() == IResource.FILE) {
                        IFile destinationFile = project.getFile(destination);
                        if (destinationFile.exists()) {
                            destinationFile.setContents(((IFile)resource)
                                .getContents(), false, false,
                                new SubProgressMonitor(monitor, 1));
                        } else {
                            destinationFile.create(((IFile)resource)
                                .getContents(), false, new SubProgressMonitor(
                                monitor, 1));
                        }
                    } else {
                        IFolder destinationFolder = project
                            .getFolder(destination);
                        if (!destinationFolder.exists()) {
                            destinationFolder.create(false, true,
                                new SubProgressMonitor(monitor, 1));
                        }
                    }
                }
            } else {
                updatePomXml(project, new SubProgressMonitor(monitor, 1));
                KvasirPlugin.getDefault().resetSourceCheckedSet();
                updateClasspath(javaProject, new SubProgressMonitor(monitor, 1));

                Properties prop = KvasirPlugin.getDefault()
                    .loadBuildProperties(project);
                String testEnvironmentGroupId = prop
                    .getProperty(KvasirPlugin.PROP_TESTENVIRONMENTGROUPID);
                if (testEnvironmentGroupId == null) {
                    testEnvironmentGroupId = KvasirPlugin
                        .getString("NewPluginWizardSecondPage.DEFAULT_GROUPID");
                }
                String testEnvironmentArtifactId = prop
                    .getProperty(KvasirPlugin.PROP_TESTENVIRONMENTARTIFACTID);
                if (testEnvironmentArtifactId == null) {
                    testEnvironmentArtifactId = KvasirPlugin
                        .getString("NewPluginWizardSecondPage.DEFAULT_ARTIFACTID");
                }
                String testEnvironmentVersion = prop
                    .getProperty(KvasirPlugin.PROP_TESTENVIRONMENTVERSION);
                if (testEnvironmentVersion != null) {
                    prepareTestEnvironment(testEnvironmentGroupId,
                        testEnvironmentArtifactId, testEnvironmentVersion,
                        new SubProgressMonitor(monitor, 1));

                    Artifact[] artifacts = KvasirPlugin.getDefault()
                        .gatherArtifacts(
                            project.getFile(KvasirPlugin.POM_FILE_NAME),
                            new SubProgressMonitor(monitor, 1));

                    deployRequiredPlugins(project, artifacts,
                        new SubProgressMonitor(monitor, 1));

                    deployStaticResources(project, prop
                        .getProperty("archetypeId"), new SubProgressMonitor(
                        monitor, 1));
                    deployPluginResources(project, new SubProgressMonitor(
                        monitor, 1));
                    updateLib(project, artifacts, new SubProgressMonitor(
                        monitor, 1));
                }
            }

            return null;
        } finally {
            monitor.done();
        }
    }


    void deployStaticResources(IProject project, String archetypeId,
        SubProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Deploying static resources",
            IProgressMonitor.UNKNOWN);
        try {
            if (archetypeId == null) {
                return;
            }

            KvasirPlugin plugin = KvasirPlugin.getDefault();
            plugin.createInstanceFromArchetype(project, true,
                new SubProgressMonitor(monitor, 1));
            if (monitor.isCanceled()) {
                throw new OperationCanceledException();
            }
        } finally {
            monitor.done();
        }
    }


    void prepareTestEnvironment(String groupId, String artifactId,
        String version, IProgressMonitor monitor)
        throws CoreException
    {
        KvasirPlugin plugin = KvasirPlugin.getDefault();

        monitor.beginTask("Preparing test environment",
            IProgressMonitor.UNKNOWN);
        try {
            MavenProject mavenProject = plugin.getMavenProject(getProject()
                .getFile(KvasirPlugin.POM_FILE_NAME), new SubProgressMonitor(
                monitor, 1));
            Artifact distArchive = (Artifact)plugin.executeInEmbedder(
                new PrepareTestEnvironmentTask(mavenProject, groupId,
                    artifactId, version), new SubProgressMonitor(monitor, 1));
            if (distArchive == null) {
                plugin.getConsole().logError(
                    "Can't resolve archive: " + distArchive);
                throw new CoreException(KvasirPlugin
                    .constructStatus("Can't resolve archive: " + distArchive));
            }

            ZipFile zipFile = null;
            File file = null;
            try {
                zipFile = new ZipFile(distArchive.getFile());
                ZipEntry entry = zipFile.getEntry(distArchive.getArtifactId()
                    + "-" + distArchive.getVersion() + "/kvasir.war");
                if (entry == null) {
                    throw new CoreException(KvasirPlugin
                        .constructStatus("Can't find kvasir.war in "
                            + distArchive.getFile().getAbsolutePath()));
                }

                file = File.createTempFile("kvasir", ".tmp");
                file.deleteOnExit();
                copyZipEntryAsFile(zipFile, entry, file);

                IFolder webapp = getProject().getFolder(
                    IKvasirProject.WEBAPP_PATH);
                plugin.unzip(file, webapp, true, new SubProgressMonitor(
                    monitor, 1));
                webapp.setDerived(true);
            } catch (ZipException ex) {
                throw new CoreException(KvasirPlugin.constructStatus(ex));
            } catch (IOException ex) {
                throw new CoreException(KvasirPlugin.constructStatus(ex));
            } finally {
                if (zipFile != null) {
                    try {
                        zipFile.close();
                    } catch (IOException ex) {
                        plugin.log(ex);
                    }
                    zipFile = null;
                }
                if (file != null) {
                    file.delete();
                }
            }

            // 開発対象のプラグインがdistributionに同梱されている場合は、
            // 同梱されている方のプラグインを削除しておく。
            String targetPluginDirectoryName = mavenProject.getArtifactId()
                + "-" + mavenProject.getArtifact().getVersion();
            IFolder targetPluginDirectory = getProject().getFolder(
                IKvasirProject.TEST_PLUGINS_PATH + "/"
                    + targetPluginDirectoryName);
            if (targetPluginDirectory.exists()) {
                targetPluginDirectory.delete(false, new SubProgressMonitor(
                    monitor, 1));
            }

        } finally {
            monitor.done();
        }
    }


    void copyZipEntryAsFile(ZipFile zipFile, ZipEntry entry, File file)
    {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = zipFile.getInputStream(entry);
            os = new FileOutputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            byte[] buf = new byte[4096];
            int len;
            while ((len = bis.read(buf)) >= 0) {
                bos.write(buf, 0, len);
            }
            bis.close();
            is = null;
            bos.close();
            os = null;
        } catch (IOException ex) {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ex2) {
                    KvasirPlugin.getDefault().log(ex2);
                }
            }
            if (os != null) {

                try {
                    os.close();
                } catch (IOException ex2) {
                    KvasirPlugin.getDefault().log(ex2);
                }
            }
        }
    }


    void deployRequiredPlugins(IProject project, Artifact[] artifacts,
        IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Deploying required plugins", 1);
        try {
            if (artifacts == null) {
                return;
            }

            IProgressMonitor subMonitor = new SubProgressMonitor(monitor, 1);
            subMonitor
                .beginTask("Gathering required plugins", artifacts.length);
            try {
                for (int i = 0; i < artifacts.length; i++) {
                    subMonitor.worked(1);

                    if (!"zip".equals(artifacts[i].getType())
                        || !"runtime".equals(artifacts[i].getScope())) {
                        continue;
                    }

                    IFolder destinationFolder = project
                        .getFolder(IKvasirProject.TEST_PLUGINS_PATH + "/"
                            + artifacts[i].getArtifactId() + "-"
                            + artifacts[i].getVersion());
                    KvasirPlugin.getDefault().unzip(artifacts[i].getFile(),
                        destinationFolder, false,
                        new SubProgressMonitor(monitor, 1));
                }
            } finally {
                subMonitor.done();
            }
        } finally {
            monitor.done();
        }
    }


    void deployPluginResources(IProject project, IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Deploying plugin resources",
            IProgressMonitor.UNKNOWN);
        try {
            IFolder pluginResources = project
                .getFolder(IKvasirProject.PLUGIN_RESOURCES_PATH);
            if (!pluginResources.exists()) {
                return;
            }

            KvasirPlugin.copy(pluginResources, project.getFullPath().append(
                IKvasirProject.TEST_PLUGIN_TARGET_PATH), false,
                new SubProgressMonitor(monitor, 1));
        } finally {
            monitor.done();
        }
    }


    void updateLib(IProject project, Artifact[] artifacts,
        IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Updating lib", IProgressMonitor.UNKNOWN);
        try {
            if (artifacts == null) {
                return;
            }
            IFile pomFile = project.getFile(KvasirPlugin.POM_FILE_NAME);
            if (!pomFile.exists()) {
                return;
            }
            KvasirPlugin.getDefault().deleteMarkers(pomFile);
            MavenProject pom = KvasirPlugin.getDefault().getMavenProject(
                pomFile, monitor);
            if (pom == null) {
                return;
            }
            Build build = pom.getBuild();
            if (build == null) {
                return;
            }
            Plugin plugin = (Plugin)build.getPluginsAsMap().get(
                "org.seasar.kvasir.maven.plugin:maven-kvasir-plugin");
            if (plugin == null) {
                return;
            }
            Xpp3Dom configuration = (Xpp3Dom)plugin.getConfiguration();
            Xpp3Dom[] children = configuration.getChildren();
            String outerLibraries = null;
            for (int i = 0; i < children.length; i++) {
                if ("pluginOuterLibraries".equals(children[i].getName())) {
                    outerLibraries = children[i].getValue();
                    break;
                }
            }
            if (outerLibraries == null) {
                return;
            }

            IFolder target = project
                .getFolder(IKvasirProject.TEST_PLUGIN_TARGET_PATH);
            if (!target.exists()) {
                return;
            }
            IFolder lib = project
                .getFolder(IKvasirProject.TEST_PLUGIN_LIB_PATH);
            if (lib.exists()) {
                IResource[] members = lib.members();
                for (int i = 0; i < members.length; i++) {
                    members[i]
                        .delete(false, new SubProgressMonitor(monitor, 1));
                }
            }

            try {
                ArtifactUtils.copyPluginOuterLibraries(null, target
                    .getLocation().toFile(), "lib", ArtifactUtils
                    .parseLibraries(outerLibraries), new HashSet(Arrays
                    .asList(artifacts)));
            } catch (IOException ex) {
                KvasirPlugin.getDefault().log(
                    "Can't copy plugin outer libraries to "
                        + target.getLocation().toPortableString() + "/lib", ex);
                return;
            } catch (ArtifactNotFoundException ex) {
                ArtifactPattern[] patterns = ex.getArtifactPatterns();
                if (patterns != null) {
                    for (int i = 0; i < patterns.length; i++) {
                        createMarker(pomFile, patterns[i].toString());
                    }
                } else {
                    createMarker(pomFile, null);
                }
            }

            target.refreshLocal(IResource.DEPTH_INFINITE,
                new SubProgressMonitor(monitor, 1));
        } finally {
            monitor.done();
        }
    }


    private void createMarker(IFile file, String pattern)
        throws CoreException
    {
        Map attributes = new HashMap();
        attributes.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
        String message;
        if (pattern == null) {
            message = "Outer library cannot be resolved";
        } else {
            message = "Outer library '" + pattern + "' cannot be resolved";
        }
        MarkerUtilities.setMessage(attributes, message);

        int lineNumber = getLineNumber(file, PLUGIN_OUTER_LIBRARIES_TAG);
        if (lineNumber != -1) {
            MarkerUtilities.setLineNumber(attributes, lineNumber);
        }

        if (pattern != null) {
            String pomContent = null;
            InputStream in = null;
            try {
                in = file.getContents();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                    in, file.getCharset()));
                StringWriter sw = new StringWriter();
                char[] buf = new char[4096];
                int len;
                while ((len = br.read(buf)) != -1) {
                    sw.write(buf, 0, len);
                }
                pomContent = sw.toString();
            } catch (IOException ex) {
                ;
            } catch (CoreException ex) {
                ;
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException ex) {
                        ;
                    }
                }
            }
            if (pomContent != null) {
                // TODO This implementation may be a bit incorrect.
                int idx = pomContent.indexOf(PLUGIN_OUTER_LIBRARIES_TAG);
                if (idx != -1) {
                    int pre = idx + PLUGIN_OUTER_LIBRARIES_TAG.length();
                    int i;
                    for (i = pre; i < pomContent.length(); i++) {
                        char ch = pomContent.charAt(i);
                        if (ch == ',' || ch == '<') {
                            if (setPositionIfMatched(pomContent, pre, i,
                                pattern, attributes)) {
                                break;
                            }
                            if (ch == '<') {
                                break;
                            }
                            pre = i + 1;
                        }
                    }
                    if (i == pomContent.length()) {
                        setPositionIfMatched(pomContent, pre, pomContent
                            .length(), pattern, attributes);
                    }
                }
            }
        }

        MarkerUtilities.createMarker(file, attributes,
            KvasirPlugin.POM_MARKER_ID);
    }


    private boolean setPositionIfMatched(String string, int start, int end,
        String pattern, Map attributes)
    {
        String tkn = string.substring(start, end);
        int left = getLeftSpaceLength(tkn);
        int right = getRightSpaceLength(tkn);
        if (pattern.equals(tkn.substring(left, tkn.length() - right))) {
            MarkerUtilities.setCharStart(attributes, start + left);
            MarkerUtilities.setCharEnd(attributes, end - right);
            return true;
        } else {
            return false;
        }
    }


    private int getLeftSpaceLength(String tkn)
    {
        for (int i = 0; i < tkn.length(); i++) {
            char ch = tkn.charAt(i);
            if (!(ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r')) {
                return i;
            }
        }
        return tkn.length();
    }


    private int getRightSpaceLength(String tkn)
    {
        for (int i = tkn.length() - 1; i >= 0; i--) {
            char ch = tkn.charAt(i);
            if (!(ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r')) {
                return (tkn.length() - 1 - i);
            }
        }
        return tkn.length();
    }


    private int getLineNumber(IFile file, String pattern)
        throws CoreException
    {
        InputStream in = null;
        try {
            in = file.getContents();
            BufferedReader br = new BufferedReader(new InputStreamReader(in,
                file.getCharset()));
            String line;
            int lineNumber = 1;
            while ((line = br.readLine()) != null) {
                int idx = line.indexOf(pattern);
                if (idx != -1) {
                    return lineNumber;
                }
                lineNumber++;
            }
            return -1;
        } catch (IOException ex) {
            return -1;
        } catch (CoreException ex) {
            return -1;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ex) {
                    ;
                }
            }
        }
    }


    IPath toDestinationPath(IResource resource)
    {
        return new Path(IKvasirProject.TEST_PLUGIN_TARGET_PATH
            + resource.getProjectRelativePath().toPortableString().substring(
                IKvasirProject.PLUGIN_RESOURCES_PATH.length()));
    }


    protected void clean(IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Cleaning project", 3);
        try {
            IFolder target = getProject().getFolder(IKvasirProject.BUILD_PATH);
            if (!target.exists()) {
                target.create(false, true, new SubProgressMonitor(monitor, 1));
            } else {
                target.refreshLocal(IResource.DEPTH_INFINITE,
                    new SubProgressMonitor(monitor, 1));
                cleanFolder(target, new SubProgressMonitor(monitor, 1));
            }
        } finally {
            monitor.done();
        }
    }


    void cleanFolder(IFolder folder, IProgressMonitor monitor)
        throws CoreException
    {
        monitor.beginTask("Cleaning folder", IProgressMonitor.UNKNOWN);
        try {
            HashSet exclusiveSet = new HashSet();
            IJavaProject javaProject = JavaCore.create(getProject());
            exclusiveSet.add(javaProject.getOutputLocation());
            IClasspathEntry[] entries = javaProject.getRawClasspath();
            for (int i = 0; i < entries.length; i++) {
                IPath outputLocation = entries[i].getOutputLocation();
                if (outputLocation != null) {
                    exclusiveSet.add(outputLocation);
                }
            }

            cleanFolder(folder, exclusiveSet, monitor);
        } finally {
            monitor.done();
        }
    }


    void cleanFolder(IFolder folder, Set exclusiveSet, IProgressMonitor monitor)
        throws CoreException
    {
        IResource[] members = folder.members();
        for (int i = 0; i < members.length; i++) {
            if (exclusiveSet.contains(members[i].getFullPath())) {
                continue;
            }
            if (members[i].getType() == IResource.FOLDER) {
                cleanFolder((IFolder)members[i], exclusiveSet, monitor);
            }
            members[i].delete(false, new SubProgressMonitor(monitor, 1));
        }
    }


    void updatePomXml(IProject project, IProgressMonitor monitor)
    {
        monitor.beginTask("Updating pom.xml", 1);
        try {
            IFile pluginFile = project.getFile(IKvasirProject.PLUGIN_FILE_PATH);
            if (!pluginFile.exists()) {
                return;
            }
            IFile pomFile = project.getFile(KvasirPlugin.POM_FILE_NAME);
            if (!pomFile.exists()) {
                return;
            }

            KvasirTemplateContext context = (KvasirTemplateContext)readPluginXmlEvaluator
                .newContext();
            readPluginXmlEvaluator.evaluate(context, new BufferedReader(
                new InputStreamReader(new FileInputStream(pluginFile
                    .getLocation().toFile()), "UTF-8")));
            Import[] imports = context.getImports();

            Properties prop = KvasirPlugin.getDefault().loadBuildProperties(
                project);
            String testEnvironmentVersion = prop
                .getProperty(KvasirPlugin.PROP_TESTENVIRONMENTVERSION);

            KvasirPlugin.getDefault()
                .executeInEmbedder(
                    new UpdatePluginDependenciesTask(pomFile, imports,
                        testEnvironmentVersion),
                    new SubProgressMonitor(monitor, 1));
        } catch (CoreException ex) {
            KvasirPlugin.getDefault().log(ex);
        } catch (IOException ex) {
            KvasirPlugin.getDefault().log(ex);
        } finally {
            monitor.done();
        }
    }


    private void updateClasspath(IJavaProject javaProject,
        IProgressMonitor monitor)
        throws JavaModelException
    {
        Set entries = new HashSet();
        Set moduleArtifacts = new HashSet();
        IFile pomFile = javaProject.getProject().getFile(
            KvasirPlugin.POM_FILE_NAME);
        KvasirPlugin.getDefault().resolveClasspathEntries(entries,
            moduleArtifacts, pomFile, true, true, monitor);

        Maven2ClasspathContainer container = new Maven2ClasspathContainer(
            entries);
        JavaCore.setClasspathContainer(container.getPath(),
            new IJavaProject[] { javaProject },
            new IClasspathContainer[] { container }, monitor);
    }


    private static final class Verifier
        implements IResourceDeltaVisitor
    {
        boolean pomXmlUpdated;

        boolean pluginXmlUpdated;

        private List removedPluginResources = new ArrayList();

        private List modifiedPluginResources = new ArrayList();


        public boolean visit(IResourceDelta delta)
        {
            IResource resource = delta.getResource();
            String path = resource.getProjectRelativePath().toPortableString();
            int kind = delta.getKind();
            System.out.print("[NOTICE] " + path + " is ");
            if (kind == IResourceDelta.ADDED) {
                System.out.println("added");
            } else if (kind == IResourceDelta.CHANGED) {
                System.out.println("changed");
            } else if (kind == IResourceDelta.REMOVED) {
                System.out.println("removed");
            } else {
                System.out.println("processed (kind=" + kind + ")");
            }

            if (KvasirPlugin.POM_FILE_NAME.equals(path)) {
                pomXmlUpdated = true;
                return false;
            } else if (IKvasirProject.PLUGIN_RESOURCES_PATH.startsWith(path)) {
                return true;
            } else if (path.startsWith(IKvasirProject.PLUGIN_RESOURCES_PATH
                + "/")
                && !KvasirPlugin.shouldResourceBeIgnored(path)) {
                if (kind == IResourceDelta.REMOVED) {
                    removedPluginResources.add(resource);
                } else {
                    modifiedPluginResources.add(resource);
                }

                if (IKvasirProject.PLUGIN_FILE_PATH.equals(path)) {
                    pluginXmlUpdated = true;
                }

                return true;
            } else {
                return false;
            }
        }
    }
}
