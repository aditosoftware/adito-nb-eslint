package de.adito.aditoweb.nbm.eslint.action;

import org.jetbrains.annotations.NotNull;
import org.openide.awt.*;
import org.openide.util.*;
import org.openide.util.actions.Presenter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Action for Popup Menu
 *
 * @author s.seemann, 17.05.2022
 */
@ActionReferences({
    @ActionReference(path = "Editors/text/javascript/Popup", separatorBefore = 550, position = 600, separatorAfter = 650),
    @ActionReference(path = "Editors/text/typescript/Popup", separatorBefore = 550, position = 600, separatorAfter = 650)
})
@ActionID(category = "adito/eslint", id = "de.adito.aditoweb.nbm.eslint.action.ESLintMenuAction")
@ActionRegistration(displayName = "#LBL_ESLint")
@NbBundle.Messages("LBL_ESLint=ESLint")
public class ESLintMenuAction extends AbstractAction implements Presenter.Popup
{
  @Override
  public void actionPerformed(ActionEvent e)
  {
    throw new RuntimeException("not implemented");
  }

  @Override
  public JMenuItem getPopupPresenter()
  {
    JMenu main = new JMenu(NbBundle.getMessage(ESLintMenuAction.class, "LBL_ESLint"));
    Utilities.actionsForPath("Plugins/ESLint/Actions").forEach(pAction -> {
      if (pAction != null)
      {
        if (pAction instanceof ESLintAction)
          main.add(new ActionWrapper((ESLintAction) pAction));
        else
          main.add(pAction);
      }
      else
        main.addSeparator();
    });
    return main;
  }

  /**
   * Wrapper for ESLintActions. In the popup menu always the icon of the bright theme is used.
   */
  private static class ActionWrapper implements Action
  {
    private final ESLintAction delegate;
    private ImageIcon icon;

    private ActionWrapper(@NotNull ESLintAction pDelegate)
    {
      delegate = pDelegate;

      try
      {
        URL resource = getClass().getResource("/" + delegate.iconResource());
        if (resource != null)
          icon = new ImageIcon(ImageIO.read(resource));
      }
      catch (IOException pE)
      {
        throw new RuntimeException(pE);
      }
    }

    @Override
    public boolean accept(Object sender)
    {
      return delegate.accept(sender);
    }

    @Override
    public Object getValue(String key)
    {
      if (icon != null && Objects.equals(key, Action.SMALL_ICON))
        return icon;
      return delegate.getValue(key);
    }

    @Override
    public void putValue(String key, Object value)
    {
      delegate.putValue(key, value);
    }

    @Override
    public void setEnabled(boolean b)
    {
      delegate.setEnabled(b);
    }

    @Override
    public boolean isEnabled()
    {
      return delegate.isEnabled();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener)
    {
      delegate.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener)
    {
      delegate.removePropertyChangeListener(listener);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
      delegate.actionPerformed(e);
    }
  }
}
