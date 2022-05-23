package de.adito.aditoweb.nbm.eslint.impl.git;

import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import de.adito.aditoweb.nbm.eslint.impl.ESLintResult;
import de.adito.aditoweb.nbm.nbide.nbaditointerface.git.IBeforeCommitAction;
import de.adito.notification.INotificationFacade;
import org.netbeans.api.progress.BaseProgressUtils;
import org.openide.*;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import javax.swing.*;
import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Implementation of {@link IBeforeCommitAction}. Analyzes all changed files before commit and executes optionally ESLint fix.
 *
 * @author s.seemann, 19.05.2022
 */
@ServiceProvider(service = IBeforeCommitAction.class)
public class ESLintCommitAction implements IBeforeCommitAction
{
  @Override
  public boolean performAction(List<File> pFilesToCommit)
  {
    AtomicBoolean returnValue = new AtomicBoolean(false);
    BaseProgressUtils.showProgressDialogAndRun(() -> {
      try
      {
        ESLintResult[] result = IESLintExecutorFacade.getInstance().esLintAnalyze(pFilesToCommit).get();

        if (result == null)
          return;

        int countMessages = Arrays.stream(result)
            .map(pResult -> pResult.getMessages().length)
            .mapToInt(pInt -> pInt)
            .sum();

        // no warnings found => return
        if (countMessages == 0)
        {
          returnValue.set(true);
          return;
        }

        // ask user what should happen
        JButton fixBtn = new JButton(NbBundle.getMessage(ESLintCommitAction.class, "LBL_Btn_Fix"));
        JButton ignoreBtn = new JButton(NbBundle.getMessage(ESLintCommitAction.class, "LBL_Btn_Ignore"));
        JButton cancelBtn = new JButton(NbBundle.getMessage(ESLintCommitAction.class, "LBL_Btn_Cancel"));

        Object dlgResult = DialogDisplayer.getDefault().notify(new DialogDescriptor(
            "<html>" + NbBundle.getMessage(ESLintCommitAction.class, "LBL_FoundWarnings", countMessages) + "</html>",
            NbBundle.getMessage(ESLintCommitAction.class, "LBL_Title_FoundWarnings"),
            true, new Object[]{fixBtn, ignoreBtn, cancelBtn},
            fixBtn, DialogDescriptor.BOTTOM_ALIGN, null, null));

        // ignore results => return
        if (Objects.equals(ignoreBtn, dlgResult))
        {
          returnValue.set(true);
          return;
        }

        // warnings should be fixed
        if (Objects.equals(fixBtn, dlgResult))
        {
          IESLintExecutorFacade.getInstance().esLintFix(pFilesToCommit).get();
          returnValue.set(true);
        }
      }
      catch (InterruptedException | ExecutionException pE) // NOSONAR
      {
        INotificationFacade.INSTANCE.error(pE);
      }
    }, NbBundle.getMessage(ESLintCommitAction.class, "LBL_Progress"));

    return returnValue.get();
  }

  @Override
  public String getName()
  {
    return NbBundle.getMessage(ESLintCommitAction.class, "LBL_CommitAction");
  }

  @Override
  public String getTooltip()
  {
    return NbBundle.getMessage(ESLintCommitAction.class, "LBL_CommitAction");
  }
}
