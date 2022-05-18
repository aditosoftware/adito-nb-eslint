package de.adito.aditoweb.nbm.eslint.options;

import org.netbeans.spi.options.OptionsPanelController;
import org.openide.util.*;

import javax.swing.*;
import java.beans.PropertyChangeListener;

/**
 * Controller for {@link ESLintOptionsPanel}
 *
 * @author s.seemann, 16.05.2022
 */
@NbBundle.Messages("LBL_ESLintOptionsPanelController_Title=ESLint")
@OptionsPanelController.SubRegistration(id = "eslint",
    displayName = "#LBL_ESLintOptionsPanelController_Title",
    location = "Adito",
    position = 155,
    keywords = "eslint lint typescript javascript adito",
    keywordsCategory = "Adito")
public class ESLintOptionsPanelController extends OptionsPanelController
{
  private final ESLintOptionsPanel panel = new ESLintOptionsPanel();

  @Override
  public void update()
  {
    panel.setCurrent(ESLintOptions.getInstance());
  }

  @Override
  public void applyChanges()
  {
    ESLintOptions.update(panel.getCurrent());
  }

  @Override
  public void cancel()
  {
    update();
  }

  @Override
  public boolean isValid()
  {
    return true;
  }

  @Override
  public boolean isChanged()
  {
    return !ESLintOptions.getInstance().equals(panel.getCurrent());
  }

  @Override
  public JComponent getComponent(Lookup masterLookup)
  {
    return panel;
  }

  @Override
  public HelpCtx getHelpCtx()
  {
    return null;
  }

  @Override
  public void addPropertyChangeListener(PropertyChangeListener l)
  {
    // not used
  }

  @Override
  public void removePropertyChangeListener(PropertyChangeListener l)
  {
    // not used
  }
}
