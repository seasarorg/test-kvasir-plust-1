/**
 * 
 */
package org.seasar.kvasir.plust.model;

import java.util.ArrayList;
import java.util.List;


/**
 * @author shidat
 *
 */
public class PluginModel extends PlustModel
{

    //retrieved from build.properties

    public static final String PLUGIN_NAME = "pluginName";

    public static final String PLUGIN_CLASS_NAME_XML = "pluginClassNameXML";

    public static final String PLUGIN_ID = "pluginId";

    public static final String ARCHETYPE_ID = "archetypeId";

    public static final String PLUGIN_PACKAGE_PATH = "pluginPackagePath";

    public static final String PLUGIN_CLASS_NAME = "pluginClassName";

    public static final String PLUGIN_PROVIDER_NAME = "pluginProviderName";

    public static final String PLUGIN_SHORT_ID = "pluginShortId";

    public static final String PLUGIN_VERSION = "pluginVersion";

    public static final String TEST_ENVIROMENT_VERSION = "testEnviromentVersion";

    public static final String TEST_ENVIRONMENT_ARTIFACT_ID = "testEnvironmentArtifactId";

    public static final String TEST_ENVIROMENT_GROUP_ID = "testEnviromentGroupId";

    //plugin.xml/plugin@id, pom.xml/project/artifactId
    private String pluginId;

    //pom.xml/project/parent/groupId
    private String testEnvironmentGroupId;

    //pom.xml/project/parent/artifactId?
    private String testEnvironmentArtifactId;

    //pom.xml/project/parent/version
    private String testEnviromentVersion;

    private String archetypeId;

    private String pluginPackagePath;

    private String pluginClassName;

    //same in plugin.xml, pom.xml
    private String pluginProviderName;

    //pom.xml/project/groupId
    private String pluginShortId;

    //same in plugin.xml, pom.xml
    private String pluginName;

    //same in plugin.xml, pom.xml
    private String pluginVersion;

    private String pluginClassNameXML;

    private List<LibraryModel> runtime = new ArrayList<LibraryModel>();

    private List<ImportModel> requires = new ArrayList<ImportModel>();

    private List<ExtensionModel> extensions = new ArrayList<ExtensionModel>();

    private List<ExtensionPointModel> extensionPoints = new ArrayList<ExtensionPointModel>();


    public String getArchetypeId()
    {
        return archetypeId != null ? archetypeId : "";
    }


    public void setArchetypeId(String archetypeId)
    {
        this.archetypeId = archetypeId;
        firePropertyChange(ARCHETYPE_ID, archetypeId);
    }


    public ExtensionPointModel[] getExtensionPoints()
    {
        return extensionPoints.toArray(new ExtensionPointModel[extensionPoints
            .size()]);
    }


    public void setExtensionPoints(List<ExtensionPointModel> extensionPoints)
    {
        this.extensionPoints = extensionPoints;
    }


    public void addExtensionPoint(ExtensionPointModel extensionPoint)
    {
        this.extensionPoints.add(extensionPoint);
        firePropertyChange("extensionPoint", extensionPoint);
    }


    public void removeExtensionPoint(ExtensionPointModel extensionPoint)
    {
        this.extensionPoints.add(extensionPoint);
        firePropertyChange("extensionPoint", extensionPoint);
    }


    public ExtensionModel[] getExtensions()
    {
        return extensions.toArray(new ExtensionModel[extensions.size()]);
    }


    public void setExtensions(List<ExtensionModel> extensions)
    {
        this.extensions = extensions;
    }


    public void addExtension(ExtensionModel extension)
    {
        this.extensions.add(extension);
        firePropertyChange("extension", extension);
    }


    public void removeExtension(ExtensionModel extension)
    {
        this.extensions.remove(extension);
        firePropertyChange("extension", extension);
    }


    public String getPluginClassName()
    {
        return pluginClassName != null ? pluginClassName : "";
    }


    public void setPluginClassName(String pluginClassName)
    {
        this.pluginClassName = pluginClassName;
        firePropertyChange(PLUGIN_CLASS_NAME, pluginClassName);
    }


    public String getPluginClassNameXML()
    {
        return pluginClassNameXML != null ? pluginClassNameXML : "";
    }


    public void setPluginClassNameXML(String pluginClassNameXML)
    {
        this.pluginClassNameXML = pluginClassNameXML;
        firePropertyChange(PLUGIN_CLASS_NAME_XML, pluginClassNameXML);
    }


    public String getPluginId()
    {
        return pluginId != null ? pluginId : "";
    }


    public void setPluginId(String pluginId)
    {
        this.pluginId = pluginId;
        firePropertyChange(PLUGIN_ID, pluginId);
    }


    public String getPluginName()
    {
        return pluginName != null ? pluginName : "";
    }


    public void setPluginName(String pluginName)
    {
        this.pluginName = pluginName;
        firePropertyChange(PLUGIN_NAME, pluginName);
    }


    public String getPluginPackagePath()
    {
        return pluginPackagePath != null ? pluginPackagePath : "";
    }


    public void setPluginPackagePath(String pluginPackagePath)
    {
        this.pluginPackagePath = pluginPackagePath;
        firePropertyChange(PLUGIN_PACKAGE_PATH, pluginPackagePath);
    }


    public String getPluginProviderName()
    {
        return pluginProviderName != null ? pluginProviderName : "";
    }


    public void setPluginProviderName(String pluginProviderName)
    {
        this.pluginProviderName = pluginProviderName;
        firePropertyChange(PLUGIN_PROVIDER_NAME, pluginProviderName);
    }


    public String getPluginShortId()
    {
        return pluginShortId != null ? pluginShortId : "";
    }


    public void setPluginShortId(String pluginShortId)
    {
        this.pluginShortId = pluginShortId;
        firePropertyChange("pluginShotId", pluginShortId);
    }


    public String getPluginVersion()
    {
        return pluginVersion != null ? pluginVersion : "";
    }


    public void setPluginVersion(String pluginVersion)
    {
        this.pluginVersion = pluginVersion;
        firePropertyChange(PLUGIN_VERSION, pluginVersion);
    }


    public ImportModel[] getRequires()
    {
        return requires.toArray(new ImportModel[requires.size()]);
    }


    public void setRequires(List<ImportModel> requires)
    {
        this.requires = requires;
    }


    public void addRequire(ImportModel importModel)
    {
        this.requires.add(importModel);
        firePropertyChange("requires", importModel);
    }


    public void removeRequire(ImportModel importModel)
    {
        this.requires.remove(importModel);
    }


    public LibraryModel[] getRuntime()
    {
        return runtime.toArray(new LibraryModel[runtime.size()]);
    }


    public void setRuntime(List<LibraryModel> runtime)
    {
        this.runtime = runtime;
    }


    public void addRuntime(LibraryModel library)
    {
        this.runtime.add(library);
        firePropertyChange("runtime", library);
    }


    public void removeRuntime(LibraryModel library)
    {
        this.runtime.remove(library);
        firePropertyChange("runtime", library);
    }


    public String getTestEnviromentVersion()
    {
        return testEnviromentVersion != null ? testEnviromentVersion : "";
    }


    public void setTestEnviromentVersion(String testEnviromentVersion)
    {
        this.testEnviromentVersion = testEnviromentVersion;
        firePropertyChange("textEnviromentVersion", testEnviromentVersion);
    }


    public String getTestEnvironmentArtifactId()
    {
        return testEnvironmentArtifactId != null ? testEnvironmentArtifactId
            : "";
    }


    public void setTestEnvironmentArtifactId(String testEnvironmentArtifactId)
    {
        this.testEnvironmentArtifactId = testEnvironmentArtifactId;
        firePropertyChange("textEnviromentArtifactId",
            testEnvironmentArtifactId);
    }


    public String getTestEnvironmentGroupId()
    {
        return testEnvironmentGroupId != null ? testEnvironmentGroupId : "";
    }


    public void setTestEnvironmentGroupId(String testEnvironmentGroupId)
    {
        this.testEnvironmentGroupId = testEnvironmentGroupId;
        firePropertyChange("textEnvironmentGroupId", testEnvironmentGroupId);
    }


    public void updateValue(String name, String value)
    {
        if (TEST_ENVIROMENT_GROUP_ID.equals(name)) {
            setTestEnvironmentGroupId(value);
        } else if (TEST_ENVIRONMENT_ARTIFACT_ID.equals(name)) {
            setTestEnvironmentArtifactId(value);
        } else if (TEST_ENVIROMENT_VERSION.equals(name)) {
            setTestEnviromentVersion(value);
        } else if (PLUGIN_VERSION.equals(name)) {
            setPluginVersion(value);
        } else if (PLUGIN_SHORT_ID.equals(name)) {
            setPluginShortId(value);
        } else if (PLUGIN_PROVIDER_NAME.equals(name)) {
            setPluginProviderName(value);
        } else if (PLUGIN_CLASS_NAME.equals(name)) {
            setPluginClassName(value);
        } else if (PLUGIN_PACKAGE_PATH.equals(name)) {
            setPluginPackagePath(value);
        } else if (ARCHETYPE_ID.equals(name)) {
            setArchetypeId(value);
        } else if (PLUGIN_ID.equals(name)) {
            setPluginId(value);
        } else if (PLUGIN_CLASS_NAME_XML.equals(name)) {
            setPluginClassNameXML(value);
        } else if (PLUGIN_NAME.equals(name)) {
            setPluginName(value);
        }
    }


    public String getValue(String name)
    {
        if (TEST_ENVIROMENT_GROUP_ID.equals(name)) {
            return getTestEnviromentVersion();
        } else if (TEST_ENVIRONMENT_ARTIFACT_ID.equals(name)) {
            return getTestEnvironmentArtifactId();
        } else if (TEST_ENVIROMENT_VERSION.equals(name)) {
            return getTestEnviromentVersion();
        } else if (PLUGIN_VERSION.equals(name)) {
            return getPluginVersion();
        } else if (PLUGIN_SHORT_ID.equals(name)) {
            return getPluginShortId();
        } else if (PLUGIN_PROVIDER_NAME.equals(name)) {
            return getPluginProviderName();
        } else if (PLUGIN_CLASS_NAME.equals(name)) {
            return getPluginClassName();
        } else if (PLUGIN_PACKAGE_PATH.equals(name)) {
            return getPluginPackagePath();
        } else if (ARCHETYPE_ID.equals(name)) {
            return getArchetypeId();
        } else if (PLUGIN_ID.equals(name)) {
            return getPluginId();
        } else if (PLUGIN_CLASS_NAME_XML.equals(name)) {
            return getPluginClassNameXML();
        } else if (PLUGIN_NAME.equals(name)) {
            return getPluginName();
        }
        return "";
    }
}
