package de.adito.aditoweb.nbm.eslint.options;

import de.adito.swing.*;
import info.clearthought.layout.*;
import org.jetbrains.annotations.NotNull;
import org.openide.util.NbBundle;

import javax.swing.*;
import java.awt.*;

/**
 * OptionsPanel for {@link ESLintOptions}
 *
 * @author s.seemann, 16.05.2022
 */
public class ESLintOptionsPanel extends JPanel implements Scrollable
{
  private final JCheckBox onSaveCheckBox;
  private ESLintOptions options;

  @NbBundle.Messages({
      "LBL_Execution=Execution",
      "LBL_OnSave=Analyze files after save"
  })
  public ESLintOptionsPanel()
  {
    super(new BorderLayout());

    double fill = TableLayoutConstants.FILL;
    double pref = TableLayoutConstants.PREFERRED;
    int gap = 5;

    double[] cols = {pref, gap, fill};
    double[] rows = {pref,
                     gap,
                     pref};

    setLayout(new TableLayout(cols, rows));
    TableLayoutUtil tlu = new TableLayoutUtil(this);

    tlu.add(0, 0, 2, 0, new LinedDecorator(Bundle.LBL_Execution(), null));

    onSaveCheckBox = new JCheckBox(Bundle.LBL_OnSave());
    tlu.add(0, 2, onSaveCheckBox);
  }

  @Override
  public Dimension getPreferredScrollableViewportSize()
  {
    return getPreferredSize();
  }

  @Override
  public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction)
  {
    return 16;
  }

  @Override
  public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction)
  {
    return 16;
  }

  @Override
  public boolean getScrollableTracksViewportWidth()
  {
    return true;
  }

  @Override
  public boolean getScrollableTracksViewportHeight()
  {
    return false;
  }

  public void setCurrent(@NotNull ESLintOptions pOptions)
  {
    options = pOptions;
    onSaveCheckBox.setSelected(pOptions.isOnSave());
  }

  public ESLintOptions getCurrent()
  {
    options.setOnSave(onSaveCheckBox.isSelected());
    return options;
  }
}
