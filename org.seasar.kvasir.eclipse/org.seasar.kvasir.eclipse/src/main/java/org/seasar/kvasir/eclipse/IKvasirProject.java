package org.seasar.kvasir.eclipse;

import org.eclipse.core.runtime.CoreException;
import org.seasar.kvasir.base.plugin.descriptor.ExtensionPoint;
import org.seasar.kvasir.eclipse.kvasir.IExtensionPointInfo;


public interface IKvasirProject
{
    String PLUGIN_FILE_NAME = "plugin.xml";

    String PLUGIN_RESOURCES_PATH = "src/main/plugin";

    String PLUGIN_FILE_PATH = PLUGIN_RESOURCES_PATH + "/" + PLUGIN_FILE_NAME;

    String BUILD_PATH = "build";

    String WEBAPP_PATH = BUILD_PATH + "/webapp";

    String WEBAPP_WEBINF_CLASSES_PATH = WEBAPP_PATH + "/WEB-INF/classes";

    String TEST_PLUGINS_PATH = WEBAPP_PATH + "/kvasir/plugins";

    String TEST_PLUGIN_TARGET_PATH = TEST_PLUGINS_PATH + "/TARGET";

    String TEST_PLUGIN_LIB_PATH = TEST_PLUGIN_TARGET_PATH + "/lib";

    String METAINF_KVASIR = "META-INF/kvasir/";


    /**
     * このKvasirプラグインプロジェクトで利用できる拡張ポイントの配列を返します。
     * <p>このメソッドが返すのは、このプロジェクトのplugin.xmlで定義されている拡張ポイントや、
     * requiresエントリに含まれているそれぞれのプラグインが提供している拡張ポイントに加えて、
     * テスト環境に含まれていてまだ
     * requiresエントリに含まれていないプラグインが提供されている拡張ポイントを含みます。
     * </p>
     * <p>このメソッドが返すExtensionPointオブジェクトについて、
     * getElementClass()メソッドは呼び出してはいけません。
     * 拡張ポイントのElementClassに関する情報を取得したい場合は、
     * （plugin.xmlにrequiresエントリを追加した上で）
     * {@link #getExtensionPointInfo(String)}
     * が返すIExtensionPointInfoオブジェクトから取り出して下さい。
     * </p>
     *
     * @return ExtensionPointの配列。nullを返すことはありません。
     * 結果は拡張ポイントのIDの辞書順で返ります。
     * @throws CoreException
     */
    ExtensionPoint[] getExtensionPoints()
        throws CoreException;


    /**
     * 指定されたIDに対応する拡張ポイントを返します。
     * <p>このメソッドが返すのは、このプロジェクトのplugin.xmlで定義されている拡張ポイントや、
     * requiresエントリに含まれているそれぞれのプラグインが提供している拡張ポイントに加えて、
     * テスト環境に含まれていてまだ
     * requiresエントリに含まれていないプラグインが提供されている拡張ポイントを含みます。
     * </p>
     * <p>指定されたIDに対応する拡張ポイントが存在しない場合はnullを返します。
     * </p>
     * <p>このメソッドが返すExtensionPointオブジェクトについて、
     * getElementClass()メソッドは呼び出してはいけません。
     * 拡張ポイントのElementClassに関する情報を取得したい場合は、
     * （plugin.xmlにrequiresエントリを追加した上で）
     * {@link #getExtensionPointInfo(String)}
     * が返すIExtensionPointInfoオブジェクトから取り出して下さい。
     * </p>
     *
     * @return ExtensionPoint。
     * @throws CoreException
     */
    ExtensionPoint getExtensionPoint(String point)
        throws CoreException;


    /**
     * このKvasirプラグインプロジェクトで利用できる拡張ポイントに関する情報の配列を返します。
     * <p>このメソッドが返すのは、このプロジェクトのplugin.xmlで定義されている拡張ポイントと、
     * requiresエントリに含まれているそれぞれのプラグインが提供している拡張ポイントに関する情報を表す
     * IExtensionPointInfoの配列です。
     * </p>
     * <p>requiresエントリに含まれていないプラグインが持つ拡張ポイントの情報を取得したい場合は、
     * {@link #getExtensionPoint(String)}
     * が返すExtensionPointオブジェクトから取り出して下さい。
     * </p>
     *
     * @return IExtensionPointの配列。nullを返すことはありません。
     * 結果は拡張ポイントのIDの辞書順で返ります。
     */
    IExtensionPointInfo[] getExtensionPointInfos()
        throws CoreException;


    /**
     * 指定されたIDに対応する拡張ポイントに関する情報を返します。
     * <p>このメソッドが返すのは、このプロジェクトのplugin.xmlで定義されている拡張ポイントと、
     * requiresエントリに含まれているそれぞれのプラグインが提供している拡張ポイントに関する情報だけです。
     * </p>
     * <p>指定されたIDに対応する拡張ポイントが存在しない場合はnullを返します。
     * </p>
     * <p>requiresエントリに含まれていないプラグインが持つ拡張ポイントの情報を取得したい場合は、
     * {@link #getExtensionPoint(String)}
     * が返すExtensionPointオブジェクトから取り出して下さい。
     * </p>
     *
     * @return IExtensionPoint。
     */
    IExtensionPointInfo getExtensionPointInfo(String point)
        throws CoreException;
}
