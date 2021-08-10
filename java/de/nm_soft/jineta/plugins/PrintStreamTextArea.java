package de.nm_soft.jineta.plugins;

import javax.swing.*;
import java.io.*;

public class PrintStreamTextArea extends JTextArea {
  private static final long serialVersionUID = 3388544640964662833L;
  private PrintStream ps = null;

  public PrintStreamTextArea() {
    super();
    setEditable(false);
    ps = new PrintStream(new PSTAOutputStream(this));
  }

  public PrintStream getPrintStream() {
    return ps;
  }

  private class PSTAOutputStream extends OutputStream {
    PrintStreamTextArea master = null;
    ByteArrayOutputStream baos = null;

    public PSTAOutputStream(PrintStreamTextArea owner) {
      master = owner;
      baos = new ByteArrayOutputStream(256);
    }

    public void write(int b) {
      baos.write(b);
    }

    public void flush() {
      master.setText(baos.toString());
    }

    public void close() {
      flush();
    }
  }
}
