package de.adito.aditoweb.nbm.eslint.impl;

import org.netbeans.api.actions.Savable;
import org.openide.*;
import org.openide.loaders.DataObject;
import org.openide.util.UserQuestionException;

import java.io.IOException;

/**
 * @author s.seemann, 16.05.2022
 */
public class SaveUtil
{

  /**
   * Saves all unsaved
   */
  public static void saveUnsavedStates()
  {
    // Everything from 'Savable.REGISTRY'
    for (Savable savable : Savable.REGISTRY.lookupAll(Savable.class))
      if (!_save(savable))
        return;
    // Old implementations are probably only in 'DataObject.getRegistry()'.
    for (DataObject dataObject : DataObject.getRegistry().getModifiedSet())
      for (Savable savable : dataObject.getLookup().lookupAll(Savable.class))
        if (!_save(savable))
          return;
  }

  private static boolean _save(Savable pSavable)
  {
    try
    {
      try
      {
        pSavable.save();
      }
      catch (UserQuestionException e)
      {
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(e.getLocalizedMessage(), NotifyDescriptor.YES_NO_CANCEL_OPTION);
        Object res = DialogDisplayer.getDefault().notify(nd);
        if (NotifyDescriptor.YES_OPTION.equals(res))
          e.confirmed();
        else if (NotifyDescriptor.CANCEL_OPTION.equals(res))
          return false;
      }
    }
    catch (IOException e)
    {
      //noinspection ThrowableResultOfMethodCallIgnored
    }
    return true;
  }
}
