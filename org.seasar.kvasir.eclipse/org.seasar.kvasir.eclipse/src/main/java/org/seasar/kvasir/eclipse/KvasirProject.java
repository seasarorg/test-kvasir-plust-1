package org.seasar.kvasir.eclipse;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
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
import org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint;
import org.seasar.kvasir.base.plugin.descriptor.Import;
import org.seasar.kvasir.base.plugin.descriptor.PluginDescriptor;
import org.seasar.kvasir.base.plugin.descriptor.Requires;
import org.seasar.kvasir.base.xom.KvasirBeanAccessorFactory;
import org.seasar.kvasir.eclipse.kvasir.IExtensionPointInfo;
import org.seasar.kvasir.eclipse.kvasir.impl.ExtensionPointInfo;

import net.skirnir.xom.IllegalSyntaxException;
import net.skirnir.xom.ValidationException;
import net.skirnir.xom.XMLDocument;
import net.skirnir.xom.XMLParser;
import net.skirnir.xom.XMLParserFactory;
import net.skirnir.xom.XOMapper;
import net.skirnir.xom.XOMapperFactory;
import net.skirnir.xom.annotation.bean.BeanAccessorBean;


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

    private SortedMap extensionPointMap_;

    private SortedMap extensionPointInfoMap_;


    public KvasirProject(IJavaProject javaProject)
    {
        javaProject_ = javaProject;
    }


    public ExtensionPoint[] getExtensionPoints()
        throws CoreException
    {
        return (ExtensionPoint[])getExtensionPointMap().values().toArray(
            new ExtensionPoint[0]);
    }


    SortedMap getExtensionPointMap()
        throws CoreException
    {
        if (extensionPointMap_ == null) {
            extensionPointMap_ = new TreeMap();
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
                    IFile pluginFile = folder.getFile(PLUGIN_FILE_NAME);
                    if (!pluginFile.exists()) {
                        continue;
                    }
                    PluginDescriptor plugin = getPluginDescriptor(pluginFile,
                        mapper);
                    pluginMap.put(plugin.getId(), plugin);
                }

                pluginMap = resolvePlugins(pluginMap, mapper);

                for (Iterator itr = pluginMap.values().iterator(); itr
                    .hasNext();) {
                    PluginDescriptor plugin = (PluginDescriptor)itr.next();
                    ExtensionPoint[] points = plugin.getExtensionPoints();
                    for (int i = 0; i < points.length; i++) {
                        extensionPointMap_
                            .put(points[i].getFullId(), points[i]);
                    }
                }
            }
        }
        return extensionPointMap_;
    }


    Map resolvePlugins(Map pluginMap, XOMapper mapper)
        throws CoreException
    {
        Map resolved = new HashMap();
        for (Iterator itr = pluginMap.values().iterator(); itr.hasNext();) {
            PluginDescriptor plugin = (PluginDescriptor)itr.next();
            resolved.put(plugin.getId(), resolvePlugin(plugin, pluginMap,
                mapper));
        }
        return resolved;
    }


    PluginDescriptor resolvePlugin(PluginDescriptor plugin, Map pluginMap,
        XOMapper mapper)
        throws CoreException
    {
        return resolvePlugin(plugin, pluginMap, plugin, mapper);
    }


    PluginDescriptor resolvePlugin(PluginDescriptor plugin, Map pluginMap,
        PluginDescriptor startPoint, XOMapper mapper)
        throws CoreException
    {
        if (plugin.getBase() == null) {
            // 他のプラグインを継承していない場合は何もしない。
            return plugin;
        }

        PluginDescriptor parentPlugin = (PluginDescriptor)pluginMap.get(plugin
            .getBase().getPlugin());
        if (parentPlugin == null) {
            // 親プラグインが見つからない場合は無視する。
            KvasirPlugin.getDefault().log(
                constructStatus("Parent plugin does not exist: parent="
                    + plugin.getBase().getPlugin() + ", target plugin="
                    + plugin.getId()));
            return plugin;
        } else if (parentPlugin == startPoint) {
            // ループを検出した。
            throw new CoreException(constructStatus("Loop detected: plugin="
                + startPoint));
        }

        // 親プラグインを解決してからマージする。
        return (PluginDescriptor)mapper.merge(resolvePlugin(parentPlugin,
            pluginMap, startPoint, mapper), plugin);
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


    public IExtensionPointInfo[] getExtensionPointInfos()
        throws CoreException
    {
        return (IExtensionPointInfo[])getExtensionPointInfoMap().values()
            .toArray(new IExtensionPointInfo[0]);
    }


    SortedMap getExtensionPointInfoMap()
        throws CoreException
    {
        if (extensionPointInfoMap_ == null) {
            extensionPointInfoMap_ = new TreeMap();
            IProject project = javaProject_.getProject();
            IFile pluginFile = project.getFile(PLUGIN_FILE_PATH);
            if (pluginFile.exists()) {
                XOMapper mapper = newMapper();
                ClassLoader classLoader = new ProjectClassLoader(javaProject_);
                PluginDescriptor plugin = getPluginDescriptor(pluginFile,
                    mapper);

                Map pluginMap = new HashMap();
                pluginMap.put(plugin.getId(), plugin);
                Requires requires = plugin.getRequires();
                if (requires != null) {
                    Import[] imports = requires.getImports();
                    for (int i = 0; i < imports.length; i++) {
                        String pluginResource = METAINF_KVASIR
                            + imports[i].getPlugin() + "/" + PLUGIN_FILE_NAME;
                        PluginDescriptor p;
                        try {
                            p = getPluginDescriptor(classLoader
                                .getResourceAsStream(pluginResource), mapper);
                        } catch (IllegalSyntaxException ex) {
                            throw new CoreException(constructStatus(
                                "Can't read " + pluginResource, ex));
                        } catch (ValidationException ex) {
                            throw new CoreException(constructStatus(
                                "Can't read " + pluginResource, ex));
                        } catch (IOException ex) {
                            throw new CoreException(constructStatus(
                                "Can't read " + pluginResource, ex));
                        }
                        pluginMap.put(p.getId(), p);
                    }
                }

                pluginMap = resolvePlugins(pluginMap, mapper);

                for (Iterator itr = pluginMap.values().iterator(); itr
                    .hasNext();) {
                    PluginDescriptor p = (PluginDescriptor)itr.next();
                    ExtensionPoint[] points = p.getExtensionPoints();
                    for (int i = 0; i < points.length; i++) {
                        extensionPointInfoMap_.put(points[i].getFullId(),
                            newExtensionPointInfo(points[i], classLoader));
                    }
                }
            }
        }
        return extensionPointInfoMap_;
    }


    public static IStatus constructStatus(Throwable t)
    {
        return KvasirPlugin.constructStatus(t);
    }


    public static IStatus constructStatus(String message)
    {
        return KvasirPlugin.constructStatus(message);
    }


    public static IStatus constructStatus(String message, Throwable t)
    {
        return KvasirPlugin.constructStatus(message, t);
    }


    IExtensionPointInfo newExtensionPointInfo(ExtensionPoint point,
        ClassLoader classLoader)
        throws CoreException
    {
        XOMapper mapper = newMapper();
        String id = point.getFullId();
        String resourcePath = METAINF_KVASIR_EXTENSIONPOINTS + id
            + "-schema.xml";
        InputStream is = classLoader.getResourceAsStream(resourcePath);
        BeanAccessorBean accessorBean;
        if (is != null) {
            // TODO 現在のXOMの実装では循環参照的に入れ子になっているエレメントについて
            // 正しくスキーマを出力できないため、 プラグインのJARにはスキーマ定義を入れていない。
            // そんなわけで実際はこのロジックは使われることはないが、
            // 将来的にやっぱりプラグインのJARにスキーマを入れておいてそれを使うとなったら
            // 以下のコードを適宜修正して利用することにしよう。
            try {
                XMLDocument document = parser_.parse(new InputStreamReader(is,
                    "UTF-8"));
                accessorBean = (BeanAccessorBean)mapper.toBean(document
                    .getRootElement(), BeanAccessorBean.class);
            } catch (ValidationException ex) {
                throw new CoreException(constructStatus(
                    "Can't get element schema: extension-point=" + id
                        + ": Can't read " + resourcePath, ex));
            } catch (IOException ex) {
                throw new CoreException(constructStatus(
                    "Can't get element schema: extension-point=" + id
                        + ": Can't read " + resourcePath, ex));
            } catch (IllegalSyntaxException ex) {
                throw new CoreException(constructStatus(
                    "Can't get element schema: extension-point=" + id
                        + ": Can't read " + resourcePath, ex));
            } finally {
                try {
                    is.close();
                } catch (IOException ignore) {
                }
            }
        } else {
            String elementClassName = point.getElementClassName();
            Class elementClass;
            try {
                elementClass = classLoader.loadClass(elementClassName);
            } catch (ClassNotFoundException ex) {
                throw new CoreException(constructStatus(
                    "Can't get element schema: extension-point=" + id, ex));
            }
            accessorBean = new BeanAccessorBean(mapper
                .getBeanAccessor(elementClass));
        }

        return new ExtensionPointInfo(id, accessorBean);
    }


    XOMapper newMapper()
    {
        return XOMapperFactory.newInstance().setBeanAccessorFactory(
            new KvasirBeanAccessorFactory());
    }


    public ExtensionPoint getExtensionPoint(String point)
        throws CoreException
    {
        return (ExtensionPoint)getExtensionPointMap().get(point);
    }


    public IExtensionPointInfo getExtensionPointInfo(String point)
        throws CoreException
    {
        return (IExtensionPointInfo)getExtensionPointInfoMap().get(point);
    }
}
