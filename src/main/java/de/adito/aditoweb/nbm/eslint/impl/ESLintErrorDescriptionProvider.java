package de.adito.aditoweb.nbm.eslint.impl;

import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import de.adito.notification.INotificationFacade;
import org.jetbrains.annotations.NotNull;
import org.netbeans.spi.editor.hints.*;
import org.openide.cookies.EditorCookie;
import org.openide.filesystems.FileObject;

import javax.swing.text.Document;
import java.beans.PropertyChangeListener;
import java.util.*;

/**
 * @author s.seemann, 12.05.2022
 */
public class ESLintErrorDescriptionProvider
{
  public void publishErrors(@NotNull ESLintResult pResult, @NotNull FileObject pFileObject)
  {
    EditorCookie ec = pFileObject.getLookup().lookup(EditorCookie.class);
    Document doc = ec != null ? ec.getDocument() : null;
    if (doc == null)
    {
      // TODO: analyze why is this null?
      INotificationFacade.INSTANCE.notify("ESLint: Could not find document", "ESLint: Could not find document", true, null);
      return; //ignore...
    }

    List<ErrorDescription> allErrors = new ArrayList<>();

    Arrays.stream(pResult.getMessages())
        .map(pMessage -> {
          List<Fix> fixes = createFixes(pMessage, pFileObject);

          Severity severity = Severity.ERROR;
          if (pMessage.getSeverity() == 1)
            severity = Severity.WARNING;

          return ErrorDescriptionFactory.createErrorDescription(pMessage.getRuleId(), severity, "ESLint: " + pMessage.getMessage(), null,
                                                                new StaticFixList(fixes), doc, pMessage.getLine());
        })
        .forEach(allErrors::add);

    HintsController.setErrors(doc, getClass().getName(), allErrors);
  }

  private static List<Fix> createFixes(@NotNull ESLintResult.Message pMessage, @NotNull FileObject pFileObject)
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

      // currently no custom fixes
      //  fixes.add(new Fix()
      //  {
      //    @Override
      //    public String getText()
      //    {
      //      return "ESLint Fix this";
      //    }
      //
      //    @Override
      //    public ChangeInfo implement() throws BadLocationException, IOException
      //    {
      //      ESLintResult.Fix fix = pMessage.getFix();
      //      int rangeStart = fix.getRangeStart();
      //      int rangeEnd = fix.getRangeEnd();
      //
      //      // Netbeans ignores \r, ESLint not => Offset must be adjusted
      //      if (pFileObject.asText().contains("\r\n"))
      //      {
      //        rangeStart = rangeStart - pMessage.getLine() + 1;
      //        rangeEnd = rangeEnd - pMessage.getLine() + 1;
      //      }
      //
      //      doc.remove(rangeStart, rangeEnd - rangeStart);
      //      doc.insertString(rangeStart, fix.getText(), null);
      //
      //      // TODO: If something has changed (e. g. text inserted), the offsets are invalid
      //      // TODO: what should happen?
      //      return null;
      //    }
      //  });
    }
    return fixes;
  }

  public static class StaticFixList implements LazyFixList
  {
    private final List<Fix> fixes;

    public StaticFixList(@NotNull List<Fix> pFixes)
    {
      fixes = pFixes;
    }

    public boolean probablyContainsFixes()
    {
      return !fixes.isEmpty();
    }

    @NotNull
    public List<Fix> getFixes()
    {
      return fixes;
    }

    public boolean isComputed()
    {
      return true;
    }

    public void addPropertyChangeListener(PropertyChangeListener l)
    {
    }

    public void removePropertyChangeListener(PropertyChangeListener l)
    {
    }
  }
}
