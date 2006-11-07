package org.seasar.kvasir.eclipse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IJavaProject;
import org.seasar.kvasir.base.plugin.PluginAlfr;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.xom.KvasirBeanAccessorFactory;
import org.seasar.kvasir.util.collection.I18NProperties;
import org.seasar.kvasir.util.io.impl.FileResource;

import net.skirnir.xom.BeanAccessor;
import net.skirnir.xom.IllegalSyntaxException;
import net.skirnir.xom.ValidationException;
import net.skirnir.xom.XMLParser;
import net.skirnir.xom.XMLParserFactory;
import net.skirnir.xom.XOMapper;
import net.skirnir.xom.XOMapperFactory;


/**
 * Kvasirプラグインプロジェクトを扱うためのクラスです。
 *
 * @author YOKOTA Takehiko
 */
public class KvasirProject
    implements IKvasirProject
{
    public static final String METAINF_KVASIR_EXTENSIONPOINTS = METAINF_KVASIR
        + "extension-points/";

    private XMLParser parser_ = XMLParserFactory.newInstance();

    private IJavaProject javaProject_;

    private SortedMap extensionPointMap_ = new TreeMap();

    private IExtensionPoint[] importedExtensionPoints_ = new IExtensionPoint[0];

    private boolean initialized_;


    public KvasirProject(IJavaProject javaProject)
    {
        javaProject_ = javaProject;
    }


    public IExtensionPoint[] getExtensionPoints()
        throws CoreException
    {
        prepareForExtensionPoints();

        return (IExtensionPoint[])extensionPointMap_.values().toArray(
            new IExtensionPoint[0]);
    }


    void prepareForExtensionPoints()
        throws CoreException
    {
        if (initialized_) {
            return;
        }

        IProject project = javaProject_.getProject();
        IFolder pluginsFolder = project.getFolder(TEST_PLUGINS_PATH);
        if (pluginsFolder.exists()) {
            XOMapper mapper = newMapper();
            IResource[] children = pluginsFolder.members();
            Map pluginMap = new HashMap();
            for (int i = 0; i < children.length; i++) {
                if (children[i].getType() != IResource.FOLDER) {
                    continue;
                }
                IFolder folder = (IFolder)children[i];
                IFile pluginFile = folder.getFile(PluginAlfr.PLUGIN_XML);
                if (!pluginFile.exists()) {
                    continue;
                }
                PluginDescriptor plugin = getPluginDescriptor(pluginFile,
                    mapper);
                I18NProperties properties = new I18NProperties(
                    new FileResource(
                        new File(folder.getLocation().toOSString())),
                    PluginDescriptor.PROPERTIES_BASENAME,
                    PluginDescriptor.PROPERTIES_SUFFIX);
                pluginMap.put(plugin.getId(),
                    new PluginInfo(plugin, properties));
            }

            pluginMap = resolvePlugins(pluginMap, mapper);

            ClassLoader classLoader = new ProjectClassLoader(javaProject_);
            List importedExtensionPointList = new ArrayList();
            for (Iterator itr = pluginMap.values().iterator(); itr.hasNext();) {
                PluginInfo info = (PluginInfo)itr.next();
                org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint[] points = info
                    .getDescriptor().getExtensionPoints();
                for (int i = 0; i < points.length; i++) {
                    IExtensionPoint extensionPoint = newExtensionPoint(
                        points[i], info, classLoader, mapper);
                    extensionPointMap_.put(points[i].getFullId(),
                        extensionPoint);
                    if (extensionPoint.getElementClassAccessor() != null) {
                        importedExtensionPointList.add(extensionPoint);
                    }
                }
            }
            importedExtensionPoints_ = (IExtensionPoint[])importedExtensionPointList
                .toArray(new IExtensionPoint[0]);
        }

        initialized_ = true;
    }


    Map resolvePlugins(Map pluginMap, XOMapper mapper)
        throws CoreException
    {
        Map resolved = new HashMap();
        for (Iterator itr = pluginMap.values().iterator(); itr.hasNext();) {
            PluginInfo info = (PluginInfo)itr.next();
            PluginDescriptor plugin = info.getDescriptor();
            resolved
                .put(plugin.getId(), resolvePlugin(info, pluginMap, mapper));
        }
        return resolved;
    }


    PluginInfo resolvePlugin(PluginInfo info, Map pluginMap, XOMapper mapper)
        throws CoreException
    {
        return resolvePlugin(info, pluginMap, info.getDescriptor(), mapper);
    }


    PluginInfo resolvePlugin(PluginInfo info, Map pluginMap,
        PluginDescriptor startPoint, XOMapper mapper)
        throws CoreException
    {
        PluginDescriptor plugin = info.getDescriptor();

        if (plugin.getBase() == null) {
            // 他のプラグインを継承していない場合は何もしない。
            return info;
        }

        PluginDescriptor parentPlugin = (PluginDescriptor)pluginMap.get(plugin
            .getBase().getPlugin());
        if (parentPlugin == null) {
            // 親プラグインが見つからない場合は無視する。
            KvasirPlugin.getDefault().log(
                constructStatus("Parent plugin does not exist: parent="
                    + plugin.getBase().getPlugin() + ", target plugin="
                    + plugin.getId()));
            return info;
        } else if (parentPlugin == startPoint) {
            // ループを検出した。
            throw new CoreException(constructStatus("Loop detected: plugin="
                + startPoint));
        }

        // 親プラグインを解決してからマージする。
        // TODO plugin.xpropertiesをマージするように。
        PluginInfo resolved = resolvePlugin(new PluginInfo(parentPlugin, null),
            pluginMap, startPoint, mapper);
        return new PluginInfo((PluginDescriptor)mapper.merge(resolved
            .getDescriptor(), plugin), null);
    }


    PluginDescriptor getPluginDescriptor(IFile pluginFile, XOMapper mapper)
        throws CoreException
    {
        try {
            return getPluginDescriptor(pluginFile.getContents(), mapper);
        } catch (IllegalSyntaxException ex) {
            throw new CoreException(constructStatus("Illegal syntax: "
                + pluginFile, ex));
        } catch (IOException ex) {
            throw new CoreException(constructStatus(
                "Can't read: " + pluginFile, ex));
        } catch (ValidationException ex) {
            throw new CoreException(constructStatus("Validation error: "
                + pluginFile, ex));
        }
    }


    PluginDescriptor getPluginDescriptor(InputStream in, XOMapper mapper)
        throws CoreException, ValidationException, IllegalSyntaxException,
        IOException
    {
        try {
            return (PluginDescriptor)mapper.toBean(parser_.parse(
                new InputStreamReader(in, "UTF-8")).getRootElement(),
                PluginDescriptor.class);
        } finally {
            try {
                in.close();
            } catch (Throwable ignore) {
            }
        }
    }


    IStatus constructStatus(Throwable t)
    {
        return KvasirPlugin.constructStatus(t);
    }


    IStatus constructStatus(String message)
    {
        return KvasirPlugin.constructStatus(message);
    }


    IStatus constructStatus(String message, Throwable t)
    {
        return KvasirPlugin.constructStatus(message, t);
    }


    IExtensionPoint newExtensionPoint(
        org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint point,
        PluginInfo pluginInfo, ClassLoader classLoader, XOMapper mapper)
        throws CoreException
    {
        String id = point.getFullId();
        BeanAccessor accessor = null;
        String elementClassName = point.getElementClassName();
        try {
            Class elementClass = classLoader.loadClass(elementClassName);
            accessor = mapper.getBeanAccessor(elementClass);
        } catch (ClassNotFoundException ignore) {
        }

        return new ExtensionPoint(id, pluginInfo.getDescriptor().getId(),
            new PluginPropertyI18NString(pluginInfo.getProperties(), point
                .getDescription()), accessor);
    }


    XOMapper newMapper()
    {
        return XOMapperFactory.newInstance().setBeanAccessorFactory(
            new KvasirBeanAccessorFactory());
    }


    public IExtensionPoint getExtensionPoint(String point)
        throws CoreException
    {
        prepareForExtensionPoints();

        return (IExtensionPoint)extensionPointMap_.get(point);
    }


    public IExtensionPoint[] getImportedExtensionPoints()
        throws CoreException
    {
        prepareForExtensionPoints();

        return importedExtensionPoints_;
    }
}
