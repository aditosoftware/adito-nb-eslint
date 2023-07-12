package de.adito.aditoweb.nbm.eslint.impl;

import lombok.*;
import org.netbeans.api.actions.Savable;
import org.openide.loaders.DataObject;

import java.util.logging.*;

/**
 * @author s.seemann, 16.05.2022
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SaveUtil
{

  /**
   * Saves all unsaved
   */
  public static void saveUnsavedStates()
  {
    // Everything from 'Savable.REGISTRY'
    for (Savable savable : Savable.REGISTRY.lookupAll(Savable.class))
      save(savable);

    // Old implementations are probably only in 'DataObject.getRegistry()'.
    for (DataObject dataObject : DataObject.getRegistry().getModifiedSet())
      for (Savable savable : dataObject.getLookup().lookupAll(Savable.class))
        save(savable);
  }

  private static void save(Savable pSavable)
  {
    try
    {
      pSavable.save();
    }
    catch (Exception e)
    {
      Logger.getLogger(SaveUtil.class.getName()).log(Level.WARNING, "", e);
    }
  }
}
