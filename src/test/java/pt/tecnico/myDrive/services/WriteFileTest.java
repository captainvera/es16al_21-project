package pt.tecnico.myDrive.service;

import org.junit.Test;
//exceptions
import pt.tecnico.myDrive.exceptions.FileExistsException;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.IllegalRemovalException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;
import pt.tecnico.myDrive.exceptions.NotAPlainFileException;
import pt.tecnico.myDrive.exceptions.CannotWriteToDirectoryException;
import pt.tecnico.myDrive.exceptions.NotADirectoryException;
//assert
import static org.junit.Assert.*;
//service
import pt.tecnico.myDrive.services.WriteFileService;
//domain
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.GuestUser;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.Login;


public class WriteFileTest extends AbstractServiceTest {
	/* (non-Javadoc)
	 * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
	 */

	private FileSystem _fs;
	private User _user, _otherUser;
	private Login _login, _otherUserLogin;
	private Directory dir1, dir2;
	private App app;
	private PlainFile pf, plainfile1, plainfile2, plainfile3, plainfile4;
	private Link linksucc1, linksucc2, linkfail1, linkfail2, linkpathsucc1, linkpathsucc2, link2linksucc1, link2linkfail1;

  private User _anotherOne;
  private Login _guestLogin;
  private GuestUser _guestUser;
  private long _guestToken;

	@Override
	protected void populate() throws Exception{
      try{

        _fs = FileSystem.getInstance();
        _user = new User(_fs, "litxoe5sQu3nt0u");
        _user.setHomeDirectory(new Directory(_fs, "litxoe5sQu3nt0u", _fs.getHomeDirectory(), _user));

        _otherUser = new User(_fs, "esquentadore5sQu3nt0u");
        _otherUser.setHomeDirectory(new Directory(_fs, "esquentadore5sQu3nt0u", _fs.getHomeDirectory(), _otherUser));

        _login = new Login(_fs, _user, _user.getHomeDirectory(), 123l);
        _otherUserLogin = new Login(_fs,_otherUser, _otherUser.getHomeDirectory(),1337l);
        _anotherOne = new User (_fs, "swaglordus", "swaglordus", "swaglordus", "rwxdrwxd");
        _anotherOne.setHomeDirectory(new Directory(_fs, "swaglordus", _fs.getHomeDirectory(), _anotherOne));

        _guestToken = 120398l;
        _guestUser = _fs.getGuestUser();
        _guestLogin = new Login(_fs, _guestUser, _guestUser.getHomeDirectory(), _guestToken);


        /* We'll have something like this
         * |- app
         * |- pf
         * |
         * |- link2fail1 -> linkfail1
         * |- linkfail1 -> dir1/dir2
         * |- linkfail2 -> .
         * |
         * |- link2linksucc1 -> linksuc1
         * |- linksucc1 -> dir1/plainfile1
         * |- linksucc2 -> dir1/dir2/plainfile2
         * |
         * |- dir1
         *     |- plainfile1
         *     |- dir2
         *          |- linkpathsucc1 -> ../plainfile1
         *          |- linkpathsucc2 -> plainfile2
         *          |- plainfile2
         * */
        dir1 = new Directory (_fs, "dir1", _user.getHomeDirectory(), _user);
        dir2 = new Directory (_fs, "dir2", dir1                    , _user);

        app = new App       (_fs, "app", _user.getHomeDirectory(), _user, "app_Data");
        pf = new PlainFile (_fs, "pf" , _user.getHomeDirectory(), _user, "pf_Data");

        linksucc1 = new Link      (_fs, "linksucc1", _user.getHomeDirectory(), _user, "dir1/plainfile1");
        linksucc2 = new Link      (_fs, "linksucc2", _user.getHomeDirectory(), _user, "dir1/dir2/plainfile2");

        linkpathsucc1 = new Link      (_fs, "linkpathsucc1", dir2, _user, "../plainfile1");
        linkpathsucc2 = new Link      (_fs, "linkpathsucc2", dir2, _user, "plainfile2");

        linkfail1 = new Link      (_fs, "linkfail1" , _user.getHomeDirectory(), _user, "dir1/dir2");
        linkfail2 = new Link      (_fs, "linkfail2" , _user.getHomeDirectory(), _user, ".");

        link2linksucc1 = new Link      (_fs, "link2linksucc1", _user.getHomeDirectory(), _user, "linksucc1");
        link2linkfail1 = new Link      (_fs, "link2linkfail1" , _user.getHomeDirectory(), _user, "linkfail1");

        plainfile1 = new PlainFile (_fs, "plainfile1", dir1, _user, "plainfile1_Data");
        plainfile2 = new PlainFile (_fs, "plainfile2", dir2, _user, "plainfile2_Data");

        plainfile3 = new PlainFile (_fs, "plainfile3", _anotherOne.getHomeDirectory(), _anotherOne, "plainfile3_Data");

        plainfile4 = new PlainFile (_fs, "plainfile4", _guestUser.getHomeDirectory(), _guestUser, "plainfile4_Data");

        new PlainFile (_fs, "plainfile4", _anotherOne.getHomeDirectory(), _guestUser, "plainfile3_Data");

	  } catch(Exception e) {
	    e.printStackTrace();
	  }
	}

  @Test
  public void writeApp() throws Exception {
    WriteFileService wfs = new WriteFileService(123l, "app", "content");
    wfs.execute();
    assertEquals("App data is incorrect!", app.getData(_user) , "content" );
  }

  @Test
  public void writePF() throws Exception {
    WriteFileService wfs = new WriteFileService(123l, "pf", "content");
    wfs.execute();

    assertEquals("PlainFile data is incorrect!", pf.getData(_user), "content");
  }

  @Test
  public void writePFByPath() throws Exception {
    WriteFileService wfs = new WriteFileService(123l, "dir1/plainfile1", "content");
    wfs.execute();

    assertEquals("PlainFile data is incorrect!", plainfile1.getData(_user),"content");
  }

  @Test
  public void writeLinkSucc1() throws Exception {
    WriteFileService wfs = new WriteFileService(123l, "linksucc1","content");
    wfs.execute();

    assertEquals("Link in current directory data is incorrect!", plainfile1.getData(_user), "content");
  }

  @Test
  public void writeLinkSucc2() throws Exception {
    WriteFileService wfs = new WriteFileService(123l, "linksucc2","content");
    wfs.execute();

    assertEquals("Link in current directory data is incorrect!", "content", plainfile2.getData(_user) );
  }

  @Test
  public void writeLinkByRelPathSucc1() throws Exception {
    WriteFileService wfs = new WriteFileService(123l, "dir1/dir2/linkpathsucc1","content");
    wfs.execute();

    assertEquals("Link by relative path data is incorrect!", plainfile1.getData(_user),"content");
  }

  @Test
  public void writeLinkByRelPathSucc2() throws Exception {
    WriteFileService wfs = new WriteFileService(123l, "dir1/dir2/linkpathsucc2","content");
    wfs.execute();

    assertEquals("Link by relative path data is incorrect!", plainfile2.getData(_user), "content");
  }

  @Test
  public void WriteLinkByAbsPathSucc() throws Exception {
    WriteFileService wfs = new WriteFileService(123l, "/home/litxoe5sQu3nt0u/linksucc1","content");
    wfs.execute();

    assertEquals("Link by absolute path data is incorrect!", plainfile1.getData(_user), "content");
  }

  @Test
  public void link2linkSucc () throws Exception {
    WriteFileService wfs = new WriteFileService(123l, "link2linksucc1", "content");
    wfs.execute();

    assertEquals("Link to another link data is incorrect!", plainfile1.getData(_user), "content");

  }

  @Test
  public void testGuestWriteFile () throws Exception {
      _guestLogin.setCurrentDirectory(_guestUser.getHomeDirectory(), _guestUser);

      WriteFileService wfs = new WriteFileService(_guestToken,"plainfile4","content");
      wfs.execute();

    assertEquals("Link to another link data is incorrect!", plainfile4.getData(_guestUser), "content");

  }

  //EXCEPTIONS
  @Test(expected = NotAPlainFileException.class)
    public void link2linkFail() throws Exception {
      WriteFileService wfs = new WriteFileService(123l, "link2linkfail1", "content");
      wfs.execute();
    }


  @Test(expected = NotAPlainFileException.class)
    public void writeLinkDirectoryPath() throws Exception {
      WriteFileService wfs = new WriteFileService(123l, "linkfail1","content");
      wfs.execute();
    }

  @Test(expected = NotAPlainFileException.class)
    public void writeLinkDirectorySelf() throws Exception {
      WriteFileService wfs = new WriteFileService(123l, "linkfail2","content");
      wfs.execute();
    }

  @Test(expected = NotAPlainFileException.class)
    public void writeDirectory() throws Exception {
      WriteFileService wfs = new WriteFileService(123l, "dir1","content");
      wfs.execute();
    }

  @Test(expected = NotAPlainFileException.class)
    public void writeDirectoryPath() throws Exception {
      WriteFileService wfs = new WriteFileService(123l, "dir1/dir2","content");
      wfs.execute();
    }


  @Test(expected = InsufficientPermissionsException.class)
    public void writeOtherUserPlainFile() throws Exception {
      WriteFileService wfs = new WriteFileService(1337l, "/home/litxoe5sQu3nt0u/dir1/plainfile1","content");
      wfs.execute();
    }

  @Test(expected = InsufficientPermissionsException.class)
    public void writeOtherUserApp() throws Exception {
      WriteFileService wfs = new WriteFileService(1337l, "/home/litxoe5sQu3nt0u/app","content");
      wfs.execute();
    }
  @Test(expected = InsufficientPermissionsException.class)
    public void writeOtherUserLink() throws Exception {
      WriteFileService wfs = new WriteFileService(1337l, "/home/litxoe5sQu3nt0u/linksucc2","content");
      wfs.execute();
    }

  @Test(expected = FileUnknownException.class)
    public void writeNonExistingPlainfile() throws Exception {
      WriteFileService wfs = new WriteFileService(123l, "LIIIIII_O","content");
      wfs.execute();
    }

  @Test(expected = FileUnknownException.class)
    public void writeInvalidPathToAPlainFile() throws Exception {
      WriteFileService wfs = new WriteFileService(123l, "/ecoponto/humano","content");
      wfs.execute();
    }

  @Test(expected = InsufficientPermissionsException.class)
    public void testGuestWritingOtherOwner() throws Exception {
      _guestLogin.setCurrentDirectory(_anotherOne.getHomeDirectory(), _guestUser);

      WriteFileService wfs = new WriteFileService(_guestToken,"plainfile3","content");
      wfs.execute();
    }
}
