package org.seasar.kvasir.plust;

import org.eclipse.core.runtime.CoreException;
import org.seasar.kvasir.base.plugin.PluginAlfr;


public interface IKvasirProject
{
    String PLUGIN_RESOURCES_PATH = "src/main/plugin";

    String PLUGIN_FILE_PATH = PLUGIN_RESOURCES_PATH + "/"
        + PluginAlfr.PLUGIN_XML;

    String POM_FILE_NAME = "pom.xml";

    String POM_FILE_PATH = POM_FILE_NAME;

    String BUILD_PATH = "build";

    String WEBAPP_PATH = BUILD_PATH + "/webapp";

    String WEBAPP_WEBINF_CLASSES_PATH = WEBAPP_PATH + "/WEB-INF/classes";

    String TEST_PLUGINS_PATH = WEBAPP_PATH + "/kvasir/plugins";

    String TEST_PLUGIN_TARGET_PATH = TEST_PLUGINS_PATH + "/TARGET";

    String TEST_PLUGIN_LIB_PATH = TEST_PLUGIN_TARGET_PATH + "/lib";

    String METAINF_KVASIR = "META-INF/kvasir/";


    /**
     * このKvasirプラグインプロジェクトで利用できる拡張ポイントの配列を返します。
     * <p>このメソッドの返り値には、{@link #getImportedExtensionPoints()}の返り値
     * （インポート済み拡張ポイント）の他に、
     * テスト環境に含まれていてまだrequiresエントリに含まれていないプラグインが提供している
     * 拡張ポイントを含みます。
     * </p>
     * <p>このメソッドが返すIExtensionPointオブジェクトは、
     * それがインポート済み拡張ポイントの場合のみ
     * getElementClassAccessor()メソッドの返り値が非nullになります。
     * インポート済みでない拡張ポイントについては、
     * getElementClassAccessor()はnullを返します。
     * </p>
     *
     * @return IExtensionPointの配列。nullを返すことはありません。
     * 結果は拡張ポイントのIDの辞書順で返ります。
     * @throws CoreException
     */
    IExtensionPoint[] getExtensionPoints()
        throws CoreException;


    /**
     * 指定されたIDに対応する拡張ポイントを返します。
     * <p>指定されたIDに対応する拡張ポイントが存在しない場合はnullを返します。
     * </p>
     * <p>このメソッドが返すIExtensionPointオブジェクトは、
     * それがインポート済み拡張ポイントの場合のみ
     * getElementClassAccessor()メソッドの返り値が非nullになります。
     * インポート済みでない拡張ポイントについては、
     * getElementClassAccessor()はnullを返します。
     * </p>
     *
     * @return IExtensionPoint。
     * @throws CoreException
     */
    IExtensionPoint getExtensionPoint(String point)
        throws CoreException;


    /**
     * このKvasirプラグインプロジェクトにインポート済みの拡張ポイントの配列を返します。
     * <p>このメソッドが返すのは、このプロジェクトのplugin.xmlで定義されている拡張ポイントと、
     * requiresエントリに含まれているそれぞれのプラグインが提供している拡張ポイントに関する
     * IExtensionPointの配列です。
     * </p>
     *
     * @return IExtensionPointの配列。nullを返すことはありません。
     * 結果は拡張ポイントのIDの辞書順で返ります。
     */
    IExtensionPoint[] getImportedExtensionPoints()
        throws CoreException;


    IPlugin[] getPlugins()
        throws CoreException;
}
