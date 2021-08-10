package de.nm_soft.jineta;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.prefs.Preferences;

public class VStart {
  //Construct the application
  public VStart(boolean normal) {
    // modify classpath
    /*System.setProperty("java.class.path",
      System.getProperty("java.class.path")
      + System.getProperty("path.separator")
      + System.getenv("CLASSPATH"));*/
    Preferences p = Preferences.userNodeForPackage(getClass());
    if (normal) {
      String laf = p.get(VMainWindow.propLaf, null);
      if (laf != null)
        try {
          UIManager.setLookAndFeel(laf);
        } catch (Exception ex) {
          VError.writeErrorLog(ex);
        }
      JFrame splash = new JFrame();
      splash.setUndecorated(true);
      JTextPane tp = new JTextPane();
      tp.setEditable(false);
      tp.setContentType("text/html; charset=UTF-8");
      tp.setText(VUtil.getLocale("msg_Welcome"));
      JPanel pa = new JPanel();
      pa.setBorder(BorderFactory.createCompoundBorder(
        new SoftBevelBorder(BevelBorder.RAISED), new SoftBevelBorder(BevelBorder.LOWERED)));
      pa.add(tp);
      splash.add(pa);
      splash.pack();
      Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
      Dimension dlgSize = splash.getPreferredSize();
      Point loc = splash.getLocation();
      splash.setLocation((screenSize.width - dlgSize.width) / 2 + loc.x, (screenSize.height - dlgSize.height) / 2 + loc.y - dlgSize.height - 50);
      splash.setVisible(true);
      new VMainWindow();
      splash.dispose();
    } else {
      try {
        p.removeNode();
      } catch (Exception e) {}
    }
  }

  //Main method
  public static void main(String[] args) {
      try {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      }
      catch(Exception e) {
        VError.writeErrorLog(e);
      }
    if (args.length == 1 && args[0].equalsIgnoreCase("-u")) {
      new VStart(false);
    } else {
      new VStart(true);
    }
  }
}
