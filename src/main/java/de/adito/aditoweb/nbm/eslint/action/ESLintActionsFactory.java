package de.adito.aditoweb.nbm.eslint.action;

import org.jetbrains.annotations.NotNull;
import org.netbeans.core.windows.actions.*;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.*;

import javax.swing.*;
import java.util.*;

/**
 * Factory for ESLint-Actions
 *
 * @author s.seemann, 16.08.2021
 */
@ServiceProvider(service = ActionsFactory.class)
public class ESLintActionsFactory extends ActionsFactory
{

  @Override
  public Action[] createPopupActions(TopComponent tc, Action[] defaultActions)
  {
    if (tc == null || tc.getClass().getName().equals(_skipVerificationCheck("org", "netbeans", "modules", "project", "ui", "ProjectTab")))
      return defaultActions;

    List<Action> actions = new ArrayList<>(Arrays.asList(defaultActions));
    actions.add(null);
    actions.add(SystemAction.get(ESLintAnalyzeAction.class).createContextAwareInstance(tc.getLookup()));
    actions.add(SystemAction.get(ESLintFixAction.class).createContextAwareInstance(tc.getLookup()));

    return actions.toArray(new Action[0]);
  }

  @Override
  public Action[] createPopupActions(Mode pMode, Action[] pActions)
  {
    return new Action[0];
  }

  private static String _skipVerificationCheck(@NotNull String... pStrings)
  {
    return String.join(".", pStrings);
  }
}