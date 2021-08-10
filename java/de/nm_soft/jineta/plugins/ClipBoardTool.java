package de.nm_soft.jineta.plugins;

import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.datatransfer.*;

import de.nm_soft.jineta.*;

public class ClipBoardTool extends JFrame implements Plugin, FlavorListener, WindowFocusListener {

  static final long serialVersionUID = 2005200001;

  VMainWindow owner;
  Clipboard myClipboard;
  JTextArea ta = new JTextArea();

  public EPlugin getPluginType() {
    return EPlugin.EDITTOOL;
  }

  public boolean getWindowsOnly() {
    return ANYSYSTEM;
  }

  public javax.swing.AbstractAction getAction(VMainWindow owner) {
    return new PluginAction(owner, this);
  }

  public javax.swing.KeyStroke getAccelerator() {
    return javax.swing.KeyStroke.getKeyStroke('C', java.awt.event.InputEvent.ALT_MASK);
  }

  // setup GUI
  protected void initComponents() {
    myClipboard = getToolkit().getSystemClipboard();
    setIconImage(VRes.loadIcon("ClipBoard").getImage());
    setTitle(VUtil.getLocale("vMenuEditClipTool"));
    setAlwaysOnTop(true);
    setSize(300, 300);
    setVisible(false);
    setLayout(new BorderLayout());
    ta.setWrapStyleWord(true);
    ta.setLineWrap(true);
    ta.setFont(getFont());
    add(new JScrollPane(ta), BorderLayout.CENTER);
    JPanel p = new JPanel();
    p.setBorder(BorderFactory.createLoweredBevelBorder());
    p.setLayout(new GridLayout(4, 2));
    JButton b = new JButton(new CtSendTo(owner.tfName, VUtil.getLocale("edit_Name"), KeyEvent.VK_N));
    b.setHorizontalAlignment(JButton.LEFT); p.add(b);
    b = new JButton(new CtSendTo(owner.tfCategory, VUtil.getLocale("edit_Category"), KeyEvent.VK_R));
    b.setHorizontalAlignment(JButton.LEFT); p.add(b);
    b = new JButton(new CtSendTo(owner.tfTaste, VUtil.getLocale("edit_Taste"), KeyEvent.VK_E));
    b.setHorizontalAlignment(JButton.LEFT); p.add(b);
    b = new JButton(new CtSendTo(owner.taIngredients, VUtil.getLocale("edit_Ingredients"), KeyEvent.VK_T));
    b.setHorizontalAlignment(JButton.LEFT); p.add(b);
    b = new JButton(new CtSendTo(owner.taTools, VUtil.getLocale("edit_Tools"), KeyEvent.VK_Z));
    b.setHorizontalAlignment(JButton.LEFT); p.add(b);
    b = new JButton(new CtSendTo(owner.taDecoration, VUtil.getLocale("edit_Decorations"), KeyEvent.VK_D));
    b.setHorizontalAlignment(JButton.LEFT); p.add(b);
    b = new JButton(new CtSendTo(owner.taProcedure, VUtil.getLocale("edit_Procedure"), KeyEvent.VK_U));
    b.setHorizontalAlignment(JButton.LEFT); p.add(b);
    b = new JButton(new CtSendTo(owner.taComments, VUtil.getLocale("edit_Comment"), KeyEvent.VK_I));
    b.setHorizontalAlignment(JButton.LEFT); p.add(b);
    add(p, BorderLayout.SOUTH);
    myClipboard.addFlavorListener(this);
    ta.paste(); ta.setCaretPosition(0);
    ta.setEditable(false);
    addWindowFocusListener(this);
  }

  // send selection to JTextComponent
  class CtSendTo extends TextAction {

    static final long serialVersionUID = 2005200001;

    JTextComponent tc;

    CtSendTo(JTextComponent aTc, String aName, int aKey) {
      super(aName);
      putValue(Action.SMALL_ICON, VRes.loadIcon("ONE2ONE"));
      putValue(Action.MNEMONIC_KEY, aKey);
      tc = aTc;
    }

    public void actionPerformed(ActionEvent e) {
      tc.setText(getTextComponent(e).getSelectedText());
    }
  }

  // for interface FlavorListener
  public void flavorsChanged(FlavorEvent e) {
    if (isVisible()) {
      ta.setText(null);
      ta.setEditable(true);
      ta.paste(); ta.setCaretPosition(0);
      ta.setEditable(false);
    }
  }

  // for interface WindowFocusListener
  public void windowGainedFocus(WindowEvent e) {
    ta.setText(null);
    ta.setEditable(true);
    ta.paste(); ta.setCaretPosition(0);
    ta.setEditable(false);
  }

  public void windowLostFocus(WindowEvent e) {}

  class PluginAction extends javax.swing.AbstractAction {

    static final long serialVersionUID = 2005200001;

    ClipBoardTool mcbt = null;

    PluginAction(VMainWindow owner, ClipBoardTool cbt) {
      super(VUtil.getLocale("vMenuEditClipTool"), VRes.loadIcon("ClipBoard"));
      mcbt = cbt;
      cbt.owner = owner;
      initComponents();
    }

    public void actionPerformed(java.awt.event.ActionEvent e) {
      mcbt.setVisible(true);
    }

  }
}
