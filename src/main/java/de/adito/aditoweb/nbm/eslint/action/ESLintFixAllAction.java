package de.adito.aditoweb.nbm.eslint.action;

import de.adito.aditoweb.nbm.eslint.api.IESLintExecutorFacade;
import lombok.NonNull;
import org.openide.awt.*;
import org.openide.filesystems.FileObject;
import org.openide.util.NbBundle;

import javax.swing.*;
import javax.swing.text.JTextComponent;

/**
 * Action for ESLint Fix
 *
 * @author s.seemann, 16.08.2021
 */
@ActionReferences({
    @ActionReference(path = "Editors/text/typescript/Toolbars/Default", position = 50000),
    @ActionReference(path = "Editors/text/javascript/Toolbars/Default", position = 50000),
    @ActionReference(path = "Plugins/ESLint/Actions", position = 200)
})
@ActionID(category = "adito/eslint", id = "de.adito.aditoweb.nbm.eslint.action.ESLintFixAction")
@ActionRegistration(displayName = "#LBL_ESLintFixAllAction", iconBase = "de/adito/aditoweb/nbm/eslint/action/paintbrush.png")
@NbBundle.Messages({"LBL_ESLintFixAllActionTooltip=ESLint: Fix All", "LBL_ESLintFixAllAction=Fix All"})
public class ESLintFixAllAction extends ESLintAction
{

  public ESLintFixAllAction()
  {
    putValue(Action.SHORT_DESCRIPTION, NbBundle.getMessage(ESLintAnalyzeAction.class, "LBL_ESLintFixAllActionTooltip"));
  }

  @Override
  public String iconResource()
  {
    return "de/adito/aditoweb/nbm/eslint/action/paintbrush.png";
  }

  @Override
  public void actionPerformed(@NonNull JTextComponent pTextComponent, @NonNull FileObject pFo)
  {
    //int caretPos = Optional.ofNullable(textComponent).map(JTextComponent::getCaret).map(Caret::getDot).orElse(0);
    //IESLintExecutorFacade.getInstance().esLintFix(pFo, () -> SwingUtilities.invokeLater(
    //    () -> Optional.ofNullable(textComponent).map(JTextComponent::getCaret).ifPresent(pCaret -> pCaret.setDot(caretPos))));
    IESLintExecutorFacade.getInstance().esLintFix(pFo);
  }
}
