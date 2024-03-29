package de.adito.aditoweb.nbm.eslint.impl;

import com.google.gson.Gson;
import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import de.adito.aditoweb.nbm.nbide.nbaditointerface.cache.*;
import de.adito.notification.INotificationFacade;
import lombok.*;
import org.netbeans.spi.editor.hints.*;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;
import org.openide.util.Lookup;

import javax.swing.text.Document;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.logging.*;

/**
 * @author s.seemann, 12.05.2022
 */
public class ESLintErrorDescriptionProvider
{
  private static final ESLintErrorDescriptionProvider INSTANCE = new ESLintErrorDescriptionProvider();
  private final ICache cache;

  private ESLintErrorDescriptionProvider()
  {
    cache = Lookup.getDefault().lookup(ICacheProvider.class).get(ESLintErrorDescriptionProvider.class, ESLintErrorDescriptionProvider.class.getName(),
                                                                 16 * 1024L, 256 * 1024L);
  }

  public static ESLintErrorDescriptionProvider getInstance()
  {
    return INSTANCE;
  }

  /**
   * Publish errors if they are contained in cache
   *
   * @param pFo the corresponding fileobject
   */
  public void publishExistingErrors(@NonNull FileObject pFo)
  {
    if (cache.has(pFo.getPath()))
    {
      String val = (String) cache.get(pFo.getPath());

      if (val != null)
      {
        String[] split = val.split("\\|", 2);
        if (pFo.lastModified().getTime() == Long.parseLong(split[0]))
          publishErrors(new Gson().fromJson(split[1], ESLintResult.class), pFo);
      }
    }
  }

  /**
   * Publishes the result to the editor
   *
   * @param pResult     the ESLint analyzing result
   * @param pFileObject the corresponding fileobject
   */
  public void publishErrors(@NonNull ESLintResult pResult, @NonNull FileObject pFileObject)
  {
    EditorCookie ec = pFileObject.getLookup().lookup(EditorCookie.class);
    Document doc = ec != null ? ec.getDocument() : null;
    if (doc == null)
    {
      INotificationFacade.INSTANCE.notify("ESLint: Could not find document", "ESLint: Could not find document", true, null);
      return; //ignore...
    }

    List<ErrorDescription> allErrors = new ArrayList<>();

    Arrays.stream(pResult.getMessages())
        .map(pMessage -> {
          if (pMessage.isFatal())
          {
            Logger.getLogger(ESLintErrorDescriptionProvider.class.getName()).log(Level.INFO, () -> "ESLint: Skipping following hint, because it is fatal: " + pMessage);
            return null;
          }

          try
          {
            List<Fix> fixes = createFixes(pMessage, pFileObject);

            Severity severity = Severity.ERROR;
            if (pMessage.getSeverity() == 1)
              severity = Severity.WARNING;

            return ErrorDescriptionFactory.createErrorDescription(pMessage.getRuleId(), severity, "ESLint: " + pMessage.getMessage(), null,
                                                                  new StaticFixList(fixes), doc, pMessage.getLine());
          }
          catch (Exception e)
          {
            INotificationFacade.INSTANCE.error(e);
            return null;
          }
        })
        .filter(Objects::nonNull)
        .forEach(allErrors::add);

    cache.put(pFileObject.getPath(), pFileObject.lastModified().getTime() + "|" + new Gson().toJson(pResult));
    HintsController.setErrors(doc, getClass().getName(), allErrors);
  }

  private static List<Fix> createFixes(@NonNull ESLintResult.Message pMessage, @NonNull FileObject pFileObject)
  {
    List<Fix> fixes = new ArrayList<>();
    if (pMessage.getFix() != null)
    {
      fixes.add(new Fix()
      {
        @Override
        public String getText()
        {
          return "ESLint Fix All";
        }

        @Override
        public ChangeInfo implement()
        {
          IESLintExecutorFacade.getInstance().esLintFix(pFileObject);
          return null;
        }
      });
    }
    return fixes;
  }

  @RequiredArgsConstructor
  public static class StaticFixList implements LazyFixList
  {
    @Getter
    @NonNull
    private final List<Fix> fixes;

    public boolean probablyContainsFixes()
    {
      return !fixes.isEmpty();
    }

    public boolean isComputed()
    {
      return true;
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
      // not needed
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
      // not needed
    }
  }
}