
package pt.tecnico.myDrive.services;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import pt.tecnico.myDrive.domain.FileSystem;
import pt.ist.fenixframework.Atomic;

public abstract class myDriveService {
  protected static final Logger log = LogManager.getRootLogger();

  public myDriveService() {
  }

  public static FileSystem getFileSystem(){
    return FileSystem.getInstance();
  }

  @Atomic
  public final void execute() {
    dispatch();
  }

  protected abstract void dispatch();

}
