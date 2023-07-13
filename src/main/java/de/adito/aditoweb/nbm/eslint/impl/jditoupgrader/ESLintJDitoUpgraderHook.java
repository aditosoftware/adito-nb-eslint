package de.adito.aditoweb.nbm.eslint.impl.jditoupgrader;

import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import de.adito.nbm.jditoupgrader.api.IJDitoUpgraderHook;
import de.adito.notification.INotificationFacade;
import lombok.NonNull;
import org.jetbrains.annotations.Nullable;
import org.netbeans.api.project.*;
import org.openide.*;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Hook to execute ESLint after JDitoUpgrades
 *
 * @author w.glanzer, 12.07.2023
 */
@ServiceProvider(service = IJDitoUpgraderHook.class)
public class ESLintJDitoUpgraderHook implements IJDitoUpgraderHook
{

  private static final long UPGRADE_ESLINTFIX_TIMEOUT = Long.parseLong(System.getProperty("adito.jditoupgrader.eslint.timeout", "20000"));
  private static final String UPGRADE_YES_OPTION = NbBundle.getMessage(ESLintJDitoUpgraderHook.class, "TEXT_ESLintUpgraderHook_YesOption");
  private static final String UPGRADE_NO_OPTION = NbBundle.getMessage(ESLintJDitoUpgraderHook.class, "TEXT_ESLintUpgraderHook_NoOption");

  @Override
  public void afterAllUpgrade(@NonNull List<File> pUpgradedFiles, @Nullable Exception pException)
  {
    if (pException == null)
    {
      NotifyDescriptor descr = new NotifyDescriptor(new OverviewPanel(pUpgradedFiles), NbBundle.getMessage(ESLintJDitoUpgraderHook.class, "TITLE_ESLintUpgraderHook"),
                                                    NotifyDescriptor.YES_NO_OPTION, NotifyDescriptor.QUESTION_MESSAGE,
                                                    new Object[]{UPGRADE_YES_OPTION, UPGRADE_NO_OPTION}, UPGRADE_YES_OPTION);
      Object result = DialogDisplayer.getDefault().notify(descr);
      if (Objects.equals(result, UPGRADE_YES_OPTION))
      {
        try
        {
          IESLintExecutorFacade.getInstance().esLintFix(pUpgradedFiles).get(pUpgradedFiles.size() * UPGRADE_ESLINTFIX_TIMEOUT, TimeUnit.MILLISECONDS);
        }
        catch (Exception e) //NOSONAR ignore interruption state here
        {
          INotificationFacade.INSTANCE.error(e);
        }
      }
    }
  }

  /**
   * Panel to show in a dialog
   */
  private static class OverviewPanel extends JPanel
  {
    /**
     * Creates the panel, based on some upgraded files
     *
     * @param pUpgradedFiles Files that the upgrader upgradet successfully
     */
    public OverviewPanel(@NonNull List<File> pUpgradedFiles)
    {
      super(new BorderLayout(10, 10));
      Path commonRoot = findCommonRoot(pUpgradedFiles);
      setPreferredSize(new Dimension(400, 250));
      add(new JLabel(NbBundle.getMessage(ESLintJDitoUpgraderHook.class, "TEXT_ESLintUpgraderHook")), BorderLayout.NORTH);
      add(new JScrollPane(new JList<>(pUpgradedFiles.stream()
                                          .map(pFile -> {
                                            if (commonRoot != null)
                                              return commonRoot.relativize(pFile.toPath()).toString();
                                            return pFile.getAbsolutePath();
                                          })
                                          .sorted(String.CASE_INSENSITIVE_ORDER)
                                          .toArray())), BorderLayout.CENTER);
    }

    /**
     * Searches a common root of the given files.
     *
     * @param pFiles Files to search the root for
     * @return the common root or null, if no root was found
     */
    @Nullable
    private Path findCommonRoot(@NonNull List<File> pFiles)
    {
      return pFiles.stream()
          .map(FileUtil::toFileObject)
          .filter(Objects::nonNull)
          .map(FileOwnerQuery::getOwner)
          .filter(Objects::nonNull)
          .map(Project::getProjectDirectory)
          .filter(Objects::nonNull)
          .map(FileUtil::toFile)
          .filter(Objects::nonNull)
          .findFirst()
          .map(File::toPath)
          .orElse(null);
    }
  }

}
