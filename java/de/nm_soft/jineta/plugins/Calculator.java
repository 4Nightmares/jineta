package de.nm_soft.jineta.plugins;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import java.util.regex.*;
import java.util.*;
import java.text.NumberFormat;

import de.nm_soft.jineta.*;

public class Calculator extends JFrame
  implements Plugin, javax.swing.event.ChangeListener {

  static final long serialVersionUID = 2005200001;

  JSpinner spSrc;
  JSpinner spDst;
  JList<String> listDest;
  Recipe rez = null;
  Vector<String> names = null;
  Vector<Float> amounts = null;

  public EPlugin getPluginType() {
    return EPlugin.TOOL;
  }

  public boolean getWindowsOnly() {
    return ANYSYSTEM;
  }

  public AbstractAction getAction(VMainWindow owner) {
    return new CalculatorAction(owner);
  }

  public Calculator() {}

  public Calculator(Recipe aRecipe) {
    super();
    rez = aRecipe;
    if (rez != null) {
      setDefaultCloseOperation(DISPOSE_ON_CLOSE);
      setAlwaysOnTop(false);
      setTitle(VUtil.getLocale("vMenuEditCalc"));
      setIconImage(VRes.loadIcon("Calc").getImage());
      setup();
      pack();
      setVisible(true);
    } else
      dispose();
  }

  public javax.swing.KeyStroke getAccelerator() {
    return null;
  }

  private void setList(JList<String> aList, Vector<Float> aAmounts) {
    Vector<String> v = new Vector<String>(aAmounts.size());
    Enumeration<String> n = names.elements();
    String s;
    NumberFormat nf = NumberFormat.getInstance();
    for (Object e : aAmounts) {
      s = (String)(n.nextElement());
      Float f = (Float)e;
      if (f.floatValue() >= 0.0)
        v.add(nf.format(f) + s);
      else
        v.add(s);
    }
    aList.setListData(v);
  }

  private void setup() {
    Box b = Box.createVerticalBox();
    Box c = Box.createHorizontalBox();
    c.setBorder(new SoftBevelBorder(BevelBorder.LOWERED));
    c.add(new JLabel(rez.getCategory() + ": " + rez.getName()));
    c.add(Box.createHorizontalGlue());
    b.add(c);

    Matcher m;
    NumberFormat nf = NumberFormat.getInstance();
    names = new Vector<String>(rez.getIngredients().size());
    amounts = new Vector<Float>(rez.getIngredients().size());
    String s;
    Pattern num = Pattern.compile("[0-9]+[.,]?[0-9]*");
    Pattern fract = Pattern.compile("([0-9]+[ ]{1})?([0-9]+/[0-9]+){1}");
    Pattern i = Pattern.compile("[0-9]+");
    for (Object e : rez.getIngredients()) {
      s = (String)e;
      s = s.replaceAll("<(.|/.)>", "").replace("�", "0.5").replace("�", "0.25").replace("�", "0.75");
      m = fract.matcher(s);
      if (m.find()) {
        String frac = m.group();
        String t = s.substring(m.end());
        m = i.matcher(frac);
        m.find();
        float g = Float.parseFloat(m.group());
        m.find();
        float z = Float.parseFloat(m.group());
        if (m.find()) {
          float n = Float.parseFloat(m.group());
          amounts.add(new Float(g + z/n));
        } else
          amounts.add(new Float(g/z));
        names.add(t);
      } else {
        m = num.matcher(s);
        if (m.find()) {
          try {
            amounts.add(new Float(m.group()));
          } catch (Exception ex) {
            try {
              amounts.add(new Float(nf.parse(m.group()).floatValue()));
            } catch (Exception exc) {
              amounts.add(new Float(-1.0));
            }
          }
          names.add(s.substring(m.end()));
        } else {
          amounts.add(new Float(-1.0));
          names.add(s);
        }
      }
    }

    c = Box.createHorizontalBox();
    Box d = Box.createVerticalBox();
    d.setBorder(BorderFactory.createEtchedBorder());

    Pattern port = Pattern.compile("[0-9]+(?=[ ]+P(orti|ers){1}on(en)?)");
    s = rez.toText();
    m = port.matcher(s);
    int po = 1;
    if (m.find())
      po = Integer.parseInt(m.group());
    else {
      port = Pattern.compile("(Menge|Yield|Serv(ings|es)): ([0-9]+)");
      m = port.matcher(s);
      if (m.find())
        po = Integer.parseInt(m.group(3));
    }

    spSrc = new JSpinner(new SpinnerNumberModel(po, 1, 200, 1));
    spSrc.addChangeListener(this);
    d.add(spSrc);
    JList<String> listSource = new JList<String>();
    setList(listSource, amounts);
    d.add(new JScrollPane(listSource));
    c.add(d);

    d = Box.createVerticalBox();
    d.setBorder(BorderFactory.createEtchedBorder());
    spDst = new JSpinner(new SpinnerNumberModel(1, 1, 200, 1));
    spDst.addChangeListener(this);
    d.add(spDst);

    listDest = new JList<String>();
    setList(listDest, amounts);
    d.add(new JScrollPane(listDest));
    c.add(d);

    b.add(c);

    add(b);

    stateChanged(null);
    pack();
    spSrc.setMaximumSize(spSrc.getSize());
    spDst.setMaximumSize(spDst.getSize());
  }

  private class CalculatorAction extends AbstractAction {

    static final long serialVersionUID = 2005200009;
    VMainWindow mw;

    CalculatorAction(VMainWindow owner) {
      super(VUtil.getLocale("vMenuEditCalc"), VRes.loadIcon("Calc"));
      mw = owner;
    }

    public void actionPerformed(ActionEvent e) {
      new Calculator(mw.active);
    }
  }

  public void stateChanged(javax.swing.event.ChangeEvent e) {
    int vs = ((Number)spSrc.getValue()).intValue();
    int vd = ((Number)spDst.getValue()).intValue();
    Vector<Float> results = new Vector<Float>(amounts.size());
    Float f;
    for (Object en : amounts) {
      f = (Float)en;
      results.add(new Float(f.floatValue()/vs*vd));
    }
    setList(listDest, results);
  }
}
