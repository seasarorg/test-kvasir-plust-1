/**
 * 
 */
package org.seasar.kvasir.plust.decorator;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.seasar.kvasir.plust.KvasirPlugin;


/**
 * @author shida
 *
 */
public class PlustProjectDecorator
    implements ILightweightLabelDecorator
{

    public void decorate(Object element, IDecoration decoration)
    {
        IProject project = (IProject)element;
        try {
            String[] natureIds = project.getDescription().getNatureIds();
            for (int i = 0; i < natureIds.length; i++) {
                String id = natureIds[i];
                if (KvasirPlugin.NATURE_ID.equals(id)) {
                    decoration.addOverlay(KvasirPlugin
                        .getImageDescriptor(KvasirPlugin.IMG_DECORATION));
                }
            }
        } catch (CoreException e) {
            e.printStackTrace();
        }
    }


    public void addListener(ILabelProviderListener listener)
    {

    }


    public void dispose()
    {
        // TODO Auto-generated method stub

    }


    public boolean isLabelProperty(Object element, String property)
    {
        return false;
    }


    public void removeListener(ILabelProviderListener listener)
    {

    }

}
