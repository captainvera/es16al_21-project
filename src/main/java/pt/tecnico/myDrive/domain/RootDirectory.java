package pt.tecnico.myDrive.domain;

import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.User;

public class RootDirectory extends RootDirectory_Base {

    public RootDirectory() {
        super();
    }

  public RootDirectory(String name, Directory parent, Integer id, User owner){
    init(name, parent, id, owner);
  }

  /**
   * The parent of the root directory should actually be itself, but since
   * FenixFramework adds objects to both sides of the relation, and we don't
   * want that, this should do it.
   */

  @Override
  public void setParent(Directory parent){
    /* do nothing */
  }

  @Override
  public Directory getParent() { return this; }

  @Override
  public void remove() {
    removeAllFiles();
  }

}