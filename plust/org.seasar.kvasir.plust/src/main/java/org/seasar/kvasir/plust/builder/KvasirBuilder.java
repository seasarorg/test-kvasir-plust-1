package org.seasar.kvasir.plust.builder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.maven.artifact.Artifact;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.maven.ide.eclipse.container.Maven2ClasspathContainer;
import org.seasar.kvasir.plust.IKvasirProject;
import org.seasar.kvasir.plust.KvasirPlugin;

import net.skirnir.freyja.TemplateEvaluator;


public class KvasirBuilder extends IncrementalProjectBuilder
{
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

                KvasirPlugin plugin = KvasirPlugin.getDefault();

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
                        project.getFile(IKvasirProject.POM_FILE_NAME),
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
                    plugin.deployRequiredPluginsToTestEnvironment(project,
                        artifacts, new SubProgressMonitor(monitor, 1));
                }
                if (updateLib) {
                    plugin.deployLibToTestEnvironment(project, artifacts,
                        new SubProgressMonitor(monitor, 1));
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
                KvasirPlugin plugin = KvasirPlugin.getDefault();

                updatePomXml(project, new SubProgressMonitor(monitor, 1));
                plugin.resetSourceCheckedSet();
                updateClasspath(javaProject, new SubProgressMonitor(monitor, 1));

                plugin.buildTestEnvironment(getProject(), monitor);
            }

            return null;
        } finally {
            monitor.done();
        }
    }


    IPath toDestinationPath(IResource resource)
    {
        return new Path(IKvasirProject.TEST_PLUGIN_TARGET_PATH
            + resource.getProjectRelativePath().toPortableString().substring(
                IKvasirProject.PLUGIN_RESOURCES_PATH.length()));
    }


    void updatePomXml(IProject project, IProgressMonitor monitor)
    {
        monitor.beginTask("Updating pom.xml", 1);
        try {
            IFile pluginFile = project.getFile(IKvasirProject.PLUGIN_FILE_PATH);
            if (!pluginFile.exists()) {
                return;
            }
            IFile pomFile = project.getFile(IKvasirProject.POM_FILE_NAME);
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
                    new UpdatePluginDependenciesTask(project, imports,
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
            IKvasirProject.POM_FILE_NAME);
        KvasirPlugin.getDefault().resolveClasspathEntries(entries,
            moduleArtifacts, pomFile, true, true, monitor);

        Maven2ClasspathContainer container = new Maven2ClasspathContainer(
            entries);
        JavaCore.setClasspathContainer(container.getPath(),
            new IJavaProject[] { javaProject },
            new IClasspathContainer[] { container }, monitor);
    }


    @Override
    protected void clean(IProgressMonitor monitor)
        throws CoreException
    {
        KvasirPlugin.getDefault().cleanTestEnvironment(getProject(), false,
            monitor);
    }


    private static final class Verifier
        implements IResourceDeltaVisitor
    {
        boolean pomXmlUpdated;

        boolean pluginXmlUpdated;

        private List<IResource> removedPluginResources = new ArrayList<IResource>();

        private List<IResource> modifiedPluginResources = new ArrayList<IResource>();


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

            if (IKvasirProject.POM_FILE_NAME.equals(path)) {
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
