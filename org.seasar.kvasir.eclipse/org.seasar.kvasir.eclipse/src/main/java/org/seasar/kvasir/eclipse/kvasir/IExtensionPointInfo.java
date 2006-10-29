package org.seasar.kvasir.eclipse.kvasir;

import net.skirnir.xom.BeanAccessor;


/**
 * 拡張ポイントに関する情報を表すインタフェースです。
 * 
 * @author YOKOTA Takehiko
 */
public interface IExtensionPointInfo
{
    /**
     * 拡張ポイントのIDを返します。
     * 
     * @return 拡張ポイントのID。
     */
    String getId();


    /**
     * 拡張ポイントにcontributeできるElementを解釈するためのBeanAccessorを返します。
     * 
     * @return BeanAccessor。
     */
    BeanAccessor getElementClassAccessor();
}
