/*
 * Copyright (c) 2010-2018, sikuli.org, sikulix.com - MIT license
 */

package org.sikuli.script;

import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.Word;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SikulixTest {

  private static Screen scr = new Screen();

  private static void p(String msg, Object... args) {
    System.out.println(String.format(msg, args));
  }

  private static String showBase = "/Users/raimundhocke/IdeaProjects/SikuliX114/API/src/main/resources/ImagesAPI.sikuli";
  private static String showLink;
  private static int showWait;
  private static int showBefore;
  private static boolean isShown = false;

  private static void show(String image) {
    show(image, 3, 0);
  }

  private static void show(String image, int wait) {
    show(image, wait, 0);
  }

  private static void showStop() {
    scr.type("w", Key.CMD);
    isShown = false;
  }

  private static void show(String image, int wait, int before) {
    if (!image.endsWith(".png")) {
      image += ".png";
    }
    showLink = "file://" + showBase + "/" + image;
    showWait = wait;
    showBefore = before;
    Thread runnable = new Thread() {
      @Override
      public void run() {
        if (before > 0) {
          RunTime.pause(showBefore);
        }
        App.openLink(showLink);
        if (wait > 0) {
          RunTime.pause(showWait);
          //p("%s", App.focusedWindow());
          scr.type("w", Key.CMD);
        } else {
          isShown = true;
        }
      }
    };
    runnable.start();
  }

  public static void main(String[] args) {
    RunTime runTime = RunTime.get();
    ImagePath.setBundlePath(showBase);
    Match match = null;
    String testImage = "findBase";

    String browser = "edge";
    if (runTime.runningMac) {
      browser = "safari";
    }

    List<Integer> runTest = new ArrayList<>();
    //runTest.add(1);
    //runTest.add(2);
    //runTest.add(3);
    //runTest.add(4);
    //runTest.add(5);
    runTest.add(6);

    if (runTest.contains(1)) {
      p("***** starting test1 scr.exists(testImage)");
      show(testImage, 0);
      //scr.hover();
      match = scr.exists(testImage);
      match.highlight(2);
      p("***** ending test");
    }
    if (runTest.contains(2)) {
      p("***** start test2 findChange");
      Finder finder = new Finder(testImage);
      String imgChange = "findChange3";
      List<Region> changes = finder.findChanges(imgChange); //, 100);
      for (Region change : changes) {
        getInset(match, change).highlight(1);
      }
      showStop();
      p("***** endOf test2");
    }
    if (runTest.contains(3)) {
      p("***** start test3 text OCR");
      App.focus(browser);
      scr.wait(1.0);
      TextRecognizer tr = TextRecognizer.start();
      Region reg = scr.selectRegion();
      String text = "";
      if (Do.SX.isNotNull(reg)) {
        text = reg.text().trim();
      }
      p("read:\n%s", text);
      p("***** endOf test3");
    }
    if (runTest.contains(4)) {
      p("***** start test4 findWord");
      App.focus(browser);
      scr.wait(1.0);
      TextRecognizer tr = TextRecognizer.start();
      Region reg = scr.selectRegion();
      reg.highlight(1);
      String aWord = Do.input("Give me a word");
      Match mText = reg.findWord(aWord);
      if (Do.SX.isNotNull(mText)) {
        mText.highlight(2);
      }
      reg.findWords(aWord).show(2);
      p("***** endOf test4");
    }
    if (runTest.contains(5)) {
      p("***** start test5 findLine");
      App.focus(browser);
      scr.wait(1.0);
      TextRecognizer tr = TextRecognizer.start();
      Region reg = scr.selectRegion();
      reg.highlight(1);
      String aRegex = Do.input("Give me a RegEx");
      List<Match> matches = reg.findLines(Finder.asRegEx(aRegex)).show(3);
      for (Match found : matches) {
        p("**** line: %s", found.getText());
      }
      p("***** endOf test5");
    }
    if (runTest.contains(6)) {
      p("***** start test6 Region.find(someText)");
      String aText = Do.input("Give me a phrase");
      App.focus(browser);
      scr.wait(1.0);
      TextRecognizer tr = TextRecognizer.start();
      Region reg = App.focusedWindow();
      reg.highlight(1);
/*
      match = reg.findText(aText);
      if (Do.SX.isNotNull(match)) {
        match.highlight(2);
      }
*/
      Iterator<Match> allText = reg.findAllText(aText);
      if (allText.hasNext()) {
        while (allText.hasNext()) {
          allText.next().highlight(1);
        }
      }
      p("***** endOf test6");
    }
    if (isShown) {
      showStop();
    }
  }

  private static Region getInset(Region base, Region inset) {
    return new Region(base.x + inset.x, base.y + inset.y, inset.w, inset.h);
  }
}
