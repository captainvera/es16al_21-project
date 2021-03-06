/**
 *
 */
package pt.tecnico.myDrive.service;

import org.junit.*;
import static org.junit.Assert.*;

import pt.tecnico.myDrive.service.AbstractServiceTest;
import pt.ist.fenixframework.FenixFramework;
import pt.tecnico.myDrive.domain.FileSystem;
import pt.tecnico.myDrive.domain.User;
import pt.tecnico.myDrive.domain.GuestUser;
import pt.tecnico.myDrive.domain.Login;
import pt.tecnico.myDrive.domain.Directory;
import pt.tecnico.myDrive.domain.PlainFile;
import pt.tecnico.myDrive.domain.Link;
import pt.tecnico.myDrive.domain.App;
import pt.tecnico.myDrive.exceptions.FileUnknownException;
import pt.tecnico.myDrive.exceptions.NotADirectoryException;
import pt.tecnico.myDrive.exceptions.InsufficientPermissionsException;

import pt.tecnico.myDrive.services.ChangeDirectoryService;

public class ChangeDirectoryTest extends AbstractServiceTest {

	private FileSystem _fs;
  private User _user1;
	private User _user2;
  private Login _login1;
	private Directory dir1;
	private Directory dir2;
  private Directory dir3;

  private Login _guestLogin;
  private GuestUser _guestUser;
  private long _guestToken;

	/* (non-Javadoc)
	 * @see pt.tecnico.myDrive.service.AbstractServiceTest#populate()
	 */

	@Override
	protected void populate() {
		try{
      _fs = FileSystem.getInstance();
      _user1 = new User(_fs, "user1e5sQu3nt0u", "user1e5sQu3nt0u", "1234567890hawd");
      _user1.setHomeDirectory(new Directory(_fs, "user1e5sQu3nt0u", _fs.getHomeDirectory(), _user1));
      _user2 = new User(_fs, "user2e5sQu3nt0u", "user2e5sQu3nt0u", "1234567890hawd");
      _user2.setHomeDirectory(new Directory(_fs, "user2e5sQu3nt0u", _fs.getHomeDirectory(), _user2));
      _user2 = new User(_fs, "user2e5sQu3nt0u", "user2e5sQu3nt0u", "1234567890hawd");

      _login1 = new Login(_fs, _user1, _user1.getHomeDirectory(), 123l);

      _guestToken = 120398l;
      _guestUser = _fs.getGuestUser();
      _guestLogin = new Login(_fs, _guestUser, _guestUser.getHomeDirectory(), _guestToken);

      /* We'll have something like this
       * |- app
       * |- pf
       * |- linkAbsolute
       * |- linkRelative
       * |- linkToNotADirectory
       * |- linkToInsufficientPermissions
       * |- linkToFileUnknown
       * |- dir1
       *     |- plainfile1
       *     |- linkToLink
       *     |- linkToLinkBad
       *     |- dir2
       *          |- plainfile2
       * */

      dir1 = new Directory (_fs, "dir1", _user1.getHomeDirectory(), _user1);
      dir2 = new Directory (_fs, "dir2", dir1, _user2);
      dir3 = new Directory (_fs, "dir3", _guestUser.getHomeDirectory(), _guestUser);

      new App       (_fs, "app", _user1.getHomeDirectory(), _user1, "app_Data");
      new PlainFile (_fs, "pf" , _user1.getHomeDirectory(), _user1, "pf_Data");

      new Link      (_fs, "linkAbsolute", _user1.getHomeDirectory(), _user1, "/home/user1e5sQu3nt0u/dir1");
      new Link      (_fs, "linkRelative", _user1.getHomeDirectory(), _user1, "dir1");
      new Link      (_fs, "linkToNotADirectory", _user1.getHomeDirectory(), _user1, "dir1/plainfile1");
      new Link      (_fs, "linkToInsufficientPermissions", _user1.getHomeDirectory(), _user1, "dir1/dir2");
      new Link      (_fs, "linkToFileUnknown", _user1.getHomeDirectory(), _user1, "dir1/erro");

      new Link      (_fs, "linkToLink", dir1, _user1, "/home/user1e5sQu3nt0u/linkAbsolute");
      new Link      (_fs, "linkToLinkBad", dir1, _user1, "/home/user1e5sQu3nt0u/linkToFileUnknown");

      new PlainFile (_fs, "plainfile1", dir1, _user1, "plainfile1_Data");
      new PlainFile (_fs, "plainfile2", dir2, _user2, "plainfile2_Data");

		}catch (Exception e){
			e.printStackTrace();
		}
	}

	@Test
	public void changeDirectoryAbsolute(){
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "/home/user1e5sQu3nt0u/dir1");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
	}

	@Test
	public void changeDirectoryRelative(){
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "dir1");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
	}

	@Test
	public void testGuestchangeDirectoryAbsolute(){
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(_guestToken, "/home/user1e5sQu3nt0u/dir1");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
	}

	@Test
	public void testGuestchangeDirectoryRelative(){
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(_guestToken, "dir3");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir3.getPath(), result);
	}

	@Test
	public void changeDirectoryLinkAbsoluteToAbsolute(){
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "/home/user1e5sQu3nt0u/linkAbsolute");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
	}

	@Test
	public void changeDirectoryLinkRelativeToAbsolute(){
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "linkAbsolute");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
	}

	@Test
	public void changeDirectoryLinkAbsoluteToRelative(){
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "/home/user1e5sQu3nt0u/linkRelative");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
	}

	@Test
	public void changeDirectoryLinkRelativeToRelative(){
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "linkRelative");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);
	}

	@Test
	public void changeDirectoryLinkToLink(){
			ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "dir1/linkToLink");

			changeDirectory.execute();
			String result = changeDirectory.result();

			assertEquals("Directory doesn't match expected", dir1.getPath(), result);

	}

	@Test(expected = FileUnknownException.class)
	public void fileUnknown() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "/test");
		changeDirectory.execute();
	}

	@Test(expected = NotADirectoryException.class)
	public void plainFile() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "pf");
		changeDirectory.execute();
	}

	@Test(expected = NotADirectoryException.class)
	public void app() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "app");
		changeDirectory.execute();
	}

	@Test(expected = InsufficientPermissionsException.class)
	public void insufficientPermissions() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "dir1/dir2");
		changeDirectory.execute();
	}

	@Test(expected = FileUnknownException.class)
	public void linkToLinkError() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "dir1/linkToLinkBad");
		changeDirectory.execute();
	}

	@Test(expected = NotADirectoryException.class)
	public void linkToNotADirectory() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "linkToNotADirectory");
		changeDirectory.execute();
	}

	@Test(expected = InsufficientPermissionsException.class)
	public void linkToInsufficientPermissions() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "linkToInsufficientPermissions");
		changeDirectory.execute();
	}

	@Test(expected = FileUnknownException.class)
	public void linkToFileUnknown() throws Exception{
		ChangeDirectoryService changeDirectory = new ChangeDirectoryService(123l, "linkToFileUnknown");
		changeDirectory.execute();
	}
}
