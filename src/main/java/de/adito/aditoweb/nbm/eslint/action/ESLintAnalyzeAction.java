package de.adito.aditoweb.nbm.eslint.action;

import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import lombok.NonNull;
import org.jetbrains.annotations.*;
import org.openide.awt.*;
import org.openide.filesystems.FileObject;
import org.openide.util.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Action for ESLint Analyze
 *
 * @author s.seemann, 16.08.2021
 */
@ActionReferences({
    @ActionReference(path = "Editors/text/typescript/Toolbars/Default", separatorBefore = 48900, position = 49000),
    @ActionReference(path = "Editors/text/javascript/Toolbars/Default", separatorBefore = 48900, position = 49000),
    @ActionReference(path = "Plugins/ESLint/Actions", position = 100)
})
@ActionID(category = "adito/eslint", id = "de.adito.aditoweb.nbm.eslint.action.ESLintAnalyzeAction")
@ActionRegistration(displayName = "#LBL_ESLintAnalyzeAction", iconBase = "de/adito/aditoweb/nbm/eslint/action/glasses.png")
@NbBundle.Messages({"LBL_ESLintAnalyzeActionTooltip=ESLint: Analyze", "LBL_ESLintAnalyzeAction=Analyze"})
public class ESLintAnalyzeAction extends ESLintAction
{
  public ESLintAnalyzeAction()
  {
    putValue(Action.SHORT_DESCRIPTION, NbBundle.getMessage(ESLintAnalyzeAction.class, "LBL_ESLintAnalyzeActionTooltip"));
  }

  @Override
  public String iconResource()
  {
    return "de/adito/aditoweb/nbm/eslint/action/glasses.png";
  }

  @Override
  public void actionPerformed(@NonNull JTextComponent pTextComponent, @NonNull FileObject pFo)
  {
    IESLintExecutorFacade.getInstance().esLintAnalyze(pFo);
  }
}
