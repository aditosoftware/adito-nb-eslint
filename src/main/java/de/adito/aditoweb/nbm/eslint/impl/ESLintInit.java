package de.adito.aditoweb.nbm.eslint.impl;

import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import de.adito.aditoweb.nbm.eslint.options.ESLintOptions;
import lombok.NonNull;
import org.netbeans.api.actions.Savable;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.modules.OnStart;
import org.openide.util.*;
import org.openide.windows.*;

import java.awt.*;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Initializes all ESLint features
 *
 * @author s.seemann, 16.05.2022
 */
@SuppressWarnings("unused") // Annotation
@OnStart
public class ESLintInit implements Runnable
{
  private final Set<Savable> temp = new HashSet<>();
  @SuppressWarnings("FieldCanBeLocal") // strong ref
  private LookupListener listener;
  @SuppressWarnings("FieldCanBeLocal") // strong ref
  private Lookup.Result<Savable> savableResult;
  @SuppressWarnings("FieldCanBeLocal") // strong ref
  private PropertyChangeListener tcListener;

  @Override
  public void run()  //NOSONAR I won't refactor this now
  {
    // Add listener to Savable Registry, because if activated ESLint should be executed on save
    savableResult = Savable.REGISTRY.lookupResult(Savable.class);
    listener = ev -> {
      Collection<? extends Savable> currentSavables = Savable.REGISTRY.lookupAll(Savable.class);
      temp.removeAll(currentSavables);

      if (ESLintOptions.getInstance().isOnSave())
        temp.forEach(pSavable -> {
          try
          {
            Field field = pSavable.getClass().getDeclaredField("obj");
            field.setAccessible(true); //NOSONAR needed
            FileObject fo = ((DataObject) field.get(pSavable)).getPrimaryFile();

            if (canAnalyze(fo))
              IESLintExecutorFacade.getInstance().esLintAnalyze(fo);
          }
          catch (Throwable t)
          {
            // ignore, skip this savable
          }
        });
      temp.clear();
      temp.addAll(currentSavables);
    };
    savableResult.addLookupListener(listener);

    // Add listener to TopComponent Registry, because existing hints should be loaded
    tcListener = evt -> {
      if (TopComponent.Registry.PROP_TC_OPENED.equals(evt.getPropertyName()))
      {
        try
        {
          FileObject fo = ((TopComponent) evt.getNewValue()).getLookup().lookup(FileObject.class);
          if (fo != null && canAnalyze(fo))
            EventQueue.invokeLater(() -> WindowManager.getDefault().invokeWhenUIReady(() -> ESLintErrorDescriptionProvider.getInstance().publishExistingErrors(fo)));
        }
        catch (Exception e)
        {
          // ignore
        }
      }

    };
    TopComponent.getRegistry().addPropertyChangeListener(tcListener);
  }

  private static boolean canAnalyze(@NonNull FileObject pFo)
  {
    return "js".equalsIgnoreCase(pFo.getExt()) || "ts".equalsIgnoreCase(pFo.getExt());
  }
}
