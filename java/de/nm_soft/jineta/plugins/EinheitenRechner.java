package de.nm_soft.jineta.plugins;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;


import de.nm_soft.jineta.*;

public class EinheitenRechner extends JFrame
  implements Plugin, ChangeListener, DocumentListener, ItemListener {

  static final long serialVersionUID = 2005200001;

  private final static String vNames[] = {
    "ml", "cl", "dl", "l", "BL", "TL", "EL", "Schuss", "Doppelter",
    "mm³", "cm³", "dm³", "m³",
    "in³", "ft³", "yd³", "gal", "RT"
  };

  private final static double vFactor[] = {
    0.001, // ml
    0.010, // cl
    0.100, // dl
    1.000, // l
    0.005, // BL
    0.005, // TL
    0.010, // EL
    0.010, // Schuß
    0.040, // Doppelter
    1e-6,  // mm³
    1e-3,  // cm³
    1.000, // dm³
    1e3,   // m³
    1.63871e-2, // in³
    0.02832e3,  // ft³
    0.76456e3,  // yd³
    3.78541,    // gal
    2.832e3     // RT
  };

  private final static String wNames[] = {
    "mg", "g", "Pfund", "kg", "Zentner", "t", "Karat", "u",
    "gr", "dram", "oz", "lb", "long cwt", "sh cwt", "long tn", "sh tn"
  };

  private final static double wFactor[] = {
       1e-6,        // mg
       1e-3,        // g
       0.500000000, // Pfund
       1.000000000, // kg
      50.000000000, // Zentner
    1000.000000000, // t
       0.2e-3,      // Karat
       1.6605402e-27,// u
       6.4799e-5,   // gr
       1.77184e-3,  // dram
       2.83495e-2,  // oz
       0.453590000, // lb
      50.802300000, // long cwt
      45.359200000, // sh cwt
    1016.050000000, // long tn
     907.185000000  // sh ton
  };

  private final static String lNames[] = {
    "mm", "cm", "dm", "m", "km",
    "in/Zoll", "ft", "yd", "mile", "n mile",
    "ly", "pc", "AE", "Angström", "sm"
  };

  private final static double lFactor[] = {
    1e-3,   // mm
    1e-2,   // cm
    1e-1,   // dm
    1.0,    // m
    1e3,    // km
    0.0254, // in
    0.3048, // ft
    0.9144, // yd
    1609.34,// mile
    1852.0, // n mile
    9.4605e15,    // ly
    3.0857e16,    // pc
    1.4959787e11, // AE
    1e-10,  // Angström
    1852.0  // sm
  };

  JTextField taFromAmount, taToAmount;
  JComboBox<String> taFromUnit, taToUnit;
  JRadioButton rbLength, rbVolume, rbWeight;
  JSpinner sFromFactor, sToFactor;

  public EPlugin getPluginType() {
    return EPlugin.TOOL;
  }

  public boolean getWindowsOnly() {
    return ANYSYSTEM;
  }

  public AbstractAction getAction(VMainWindow owner) {
    return new EinheitenRechnerAction(owner);
  }

  public EinheitenRechner() {
  }

  public EinheitenRechner(JFrame owner) {
    setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    setAlwaysOnTop(false);
    setTitle(VUtil.getLocale("vMenuEditUnitCalc"));
    setIconImage(VRes.loadIcon("Calc").getImage());
    setup();
    pack();
    setVisible(true);
  }

  public javax.swing.KeyStroke getAccelerator() {
    return null;
  }

  private void setup() {
    Box c = Box.createVerticalBox();
    Box t = Box.createHorizontalBox();
    Box m = Box.createHorizontalBox();
    Box b = Box.createHorizontalBox();
    t.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    t.add(taFromAmount = new JTextField("1", 15));
    taFromAmount.getDocument().addDocumentListener(this);
    t.add(taFromUnit = new JComboBox<String>(vNames));
    taFromUnit.addItemListener(this);
    taFromUnit.setEditable(false);
    t.add(new JLabel("/"));
    sFromFactor = new JSpinner(new SpinnerNumberModel(1, 1, 29, 1));
    ((JSpinner.NumberEditor)sFromFactor.getEditor()).getTextField().setEditable(false);
    sFromFactor.addChangeListener(this);
    t.add(sFromFactor);
    m.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    m.add(taToAmount = new JTextField("1", 15));
    taToAmount.setEditable(false);
    m.add(taToUnit = new JComboBox<String>(vNames));
    taToUnit.addItemListener(this);
    taToUnit.setEditable(false);
    m.add(new JLabel("/"));
    sToFactor = new JSpinner(new SpinnerNumberModel(1, 1, 29, 1));
    ((JSpinner.NumberEditor)sToFactor.getEditor()).getTextField().setEditable(false);
    sToFactor.addChangeListener(this);
    m.add(sToFactor);
    b.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    ButtonGroup bgType = new ButtonGroup();
    bgType.add(rbLength = new JRadioButton(VUtil.getLocale("lLength")));
    bgType.add(rbVolume = new JRadioButton(VUtil.getLocale("lVolume"), true));
    bgType.add(rbWeight = new JRadioButton(VUtil.getLocale("lWeight")));
    rbLength.addChangeListener(this);
    rbWeight.addChangeListener(this);
    rbVolume.addChangeListener(this);
    b.add(rbLength);
    b.add(Box.createHorizontalGlue());
    b.add(rbVolume);
    b.add(Box.createHorizontalGlue());
    b.add(rbWeight);
    c.add(t);
    c.add(m);
    c.add(b);
    add(c);
  }

  private class EinheitenRechnerAction extends AbstractAction {

    static final long serialVersionUID = 2005200009;
    JFrame mw;

    EinheitenRechnerAction(JFrame owner) {
      super(VUtil.getLocale("vMenuEditUnitCalc"), VRes.loadIcon("Calc"));
      mw = owner;
    }

    public void actionPerformed(ActionEvent e) {
      new EinheitenRechner(mw);
    }
  }

  boolean ol = false, ow = false, ov = false;

  public void stateChanged(ChangeEvent e) {
    if (e.getSource().equals(sToFactor) || e.getSource().equals(sFromFactor)) {
      itemStateChanged(null);
    } else
    if (ol != rbLength.isSelected() ||
      ow != rbWeight.isSelected() ||
      ov != rbVolume.isSelected()) {
      ol = rbLength.isSelected();
      ow = rbWeight.isSelected();
      ov = rbVolume.isSelected();
      taFromUnit.removeAllItems();
      taToUnit.removeAllItems();
      if (ol) {
        for (String s : lNames) {
          taFromUnit.addItem(s);
          taToUnit.addItem(s);
        }
      }
      if (ow) {
        for (String s : wNames) {
          taFromUnit.addItem(s);
          taToUnit.addItem(s);
        }
      }
      if (ov) {
        for (String s : vNames) {
          taFromUnit.addItem(s);
          taToUnit.addItem(s);
        }
      }
    }
  }

  public void changedUpdate(DocumentEvent e) {
    itemStateChanged(null);
  }

  public void insertUpdate(DocumentEvent e) {
    itemStateChanged(null);
  }

  public void removeUpdate(DocumentEvent e) {
    itemStateChanged(null);
  }

  public void itemStateChanged(ItemEvent e) {
    try {
      double f1 = 0.0, f2 = 0.0;
      if (rbLength.isSelected()) {
        f1 = lFactor[taFromUnit.getSelectedIndex()];
        f2 = lFactor[taToUnit.getSelectedIndex()];
      }
      if (rbWeight.isSelected()) {
        f1 = wFactor[taFromUnit.getSelectedIndex()];
        f2 = wFactor[taToUnit.getSelectedIndex()];
      }
      if (rbVolume.isSelected()) {
        f1 = vFactor[taFromUnit.getSelectedIndex()];
        f2 = vFactor[taToUnit.getSelectedIndex()];
      }
      f1 /= ((SpinnerNumberModel)sFromFactor.getModel()).getNumber().doubleValue();
      f2 /= ((SpinnerNumberModel)sToFactor.getModel()).getNumber().doubleValue();
      taToAmount.setText(
        String.valueOf(Double.valueOf(taFromAmount.getText()) * f1 / f2)
      );
    } catch (Exception ex) {
      taToAmount.setText(ex.getMessage());
    }
  }
}
