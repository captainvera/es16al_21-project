package pt.tecnico.myDrive;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.File;

import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.PlainFile;

import pt.ist.fenixframework.Atomic;
import pt.ist.fenixframework.DomainRoot;
import pt.ist.fenixframework.FenixFramework;

public class myDriveApplication {
  private static final Logger log = LogManager.getRootLogger();

  // FenixFramework will try automatic initialization when first accessed
  public static void main(String [] args) {
    log.trace("Welcome to myDrive");
    try {
      if(args.length == 0) setupDrive();
      xmlPrint();
    } finally {
      // ensure an orderly shutdown
      FenixFramework.shutdown();
    }
  }

  @Atomic
  public static void setupDrive(){
    /**
     * Basic setup to test desired functionality
     */

    log.trace("executing setupDrive()");
    log.debug("Setting root: " + FenixFramework.getDomainRoot());

    FileSystem fs = FileSystem.getInstance();

    try {
      fs.login("root","***");
    } catch(Exception e) {
      System.out.println("Couldn't login with root!");
    }

    /*
     * Criação do ficheiro README mudando o working directory
     * */
    try {
      log.debug("Showing Current Directory Path");
      System.out.println(fs.listPath());

      log.debug("Creating README");
      PlainFile readme = fs.createPlainFileByPath("/home/README");
      readme.setData("Lista de utilizadores");

      log.debug("Changing directory to /home");
      fs.changeDirectory("..");

      log.debug("Showing Current Directory Path");
      System.out.println(fs.listPath());
      log.debug("Listing current Directory");
      System.out.println(fs.listDirectory());
      log.debug("Showing result of opening README");
      System.out.println(fs.executeFile("README"));

    } catch(Exception e) {
      System.out.println("Couldn't create README!");
    }

    /*
     * Criação do caminho /usr/local/bin absolutamente
     */
    try {
      log.debug("Creating /usr/local/bin by path");
      fs.createDirectoryByPath("/usr/local/bin");
      log.debug("Listing /usr/local");
      System.out.println(fs.listFileByPathSimple("/usr/local"));

      log.debug("Removing /usr/local/bin"); 
      fs.removeFileByPath("/usr/local/bin");

      log.debug("Listing /usr/local");
      System.out.println(fs.listFileByPathSimple("/usr/local"));
    
      
      log.debug("Listing /home");
      System.out.println(fs.listFileByPathSimple("/home"));
      log.debug("Removing /home/README");
      fs.removeFileByPath("/home/README");
      log.debug("Listing /home");
      System.out.println(fs.listFileByPathSimple("/home"));
      

    } catch(Exception e) {
      System.out.println(e.getMessage());
    }

    log.trace("Successful default setup!");
  }

  @Atomic
  public static void xmlPrint() {
    log.trace("xmlPrint: " + FenixFramework.getDomainRoot());
    Document doc = FileSystem.getInstance().xmlExport();
    XMLOutputter xmlOutput = new XMLOutputter(Format.getPrettyFormat());
    try { xmlOutput.output(doc, new PrintStream(System.out));
    } catch (IOException e) { System.out.println(e); }
  }


  @Atomic
  public static void xmlScan(File file){
    log.trace("xmlScan: " + FenixFramework.getDomainRoot());
    FileSystem fs = FileSystem.getInstance();
    fs.reset();
    SAXBuilder builder = new SAXBuilder();
    try {
      Document document = (Document)builder.build(file);
      fs.xmlImport(document.getRootElement());
    } catch (JDOMException | IOException e) {
      e.printStackTrace();
    }
  }

}
