package pt.tecnico.myDrive.presentation;

import java.util.TreeMap;

public class MyDriveShell extends Shell {

  private String _activeUser;
  private Session _activeSession;

  private TreeMap<String, Session> _sessions = new TreeMap<String, Session>();

  public static void main(String[] args) throws Exception {
    MyDriveShell sh = new MyDriveShell();
    sh.execute();
  }

  public MyDriveShell() { 
    super("MyDrive");
    new Login(this);
    new ChangeWorkingDirectory(this);
    new ListDirectory(this);
    new Execute(this);
    new Write(this);
    new Environment(this);
    new Key(this);
  }

  public void addSession(String username, long token){
    if(_sessions.containsKey(username)){
      _sessions.remove(username);
    }
    _sessions.put(username, new Session(token, "/home/"+username));
  }

  public void setActiveSession(String username){
    Session session= _sessions.get(username);
    if(session != null){
      _activeUser = username;
      _activeSession = session;

      setUser(username);
      setDir(session.getCurrentDirectory());
    }else{
      println("No session present for user " + username); 
    }
  };

  public String getActiveUser(){
    return _activeUser;
  }

  public long getActiveToken(){
    return _activeSession.getToken();
  }

  public Session getActiveSession(){
    return _activeSession;
  }

  public void changeCurrentDirectory(String currentDir){
    _activeSession.setCurrentDirectory(currentDir);
    setDir(currentDir);
  }
}