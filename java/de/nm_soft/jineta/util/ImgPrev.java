package de.nm_soft.jineta.util;

import javax.swing.*;
import java.beans.*;
import java.awt.*;
import java.io.File;

/* ImagePreview.java by FileChooserDemo2.java. */
public class ImgPrev extends JComponent implements PropertyChangeListener {

  private static final long serialVersionUID = 1L;
  ImageIcon tmb = null;
  File file = null;
  int xscale = 0;
  int maxx = 0;

  public ImgPrev(JFileChooser fc) {
    setPreferredSize(new Dimension(100, 50));
    fc.addPropertyChangeListener(this);
  }

  public void propertyChange(PropertyChangeEvent e) {
    String prop = e.getPropertyName();

    if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(prop)) {
      file = null;
      tmb = null;
      repaint();
    } else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(prop)) {
      file = (File) e.getNewValue();
      tmb = null;
      repaint();
    }
  }

  protected void paintComponent(Graphics g) {
    if ((tmb == null && file != null) || //No tmb
         (tmb != null && //tmb
             (getWidth() < xscale || //must be scaled down
             (getWidth() > xscale && maxx != xscale)))){ //Must be scaled up
      ImageIcon tmpIcon = new ImageIcon(file.getPath());
      if (tmpIcon.getImage() != null) {
        maxx = tmpIcon.getIconWidth();
        if (maxx > getWidth()) {
          xscale = getWidth();
          tmb = new ImageIcon(tmpIcon.getImage().getScaledInstance(getWidth(), -1, Image.SCALE_DEFAULT));
        } else {
          xscale = maxx;
          tmb = tmpIcon;
        }
      }
    }
    if (tmb != null) {
      int x = getWidth() / 2 - tmb.getIconWidth() / 2;
      int y = getHeight() / 2 - tmb.getIconHeight() / 2;

      if (y < 0) {
        y = 0;
      }

      if (x < 5) {
        x = 5;
      }
      tmb.paintIcon(this, g, x, y);
    }
  }
}