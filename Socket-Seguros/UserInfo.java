public class UserInfo {
    private String user;
    private String password;

    public UserInfo(){
        this.user = "default_user";
        this.password = "default_password";
    }

    public UserInfo(String user, String password){
        this.user = user;
        this.password = password;
    }

    public static UserInfo of(String user, String password){
        return new UserInfo(user, password);
    }

    public String getUser(){
        return this.user;
    }

    public String getPassword(){
        return this.password;
    }

    public Boolean equals(UserInfo u1){
        Boolean res = false;
        if(u1.getPassword().equals(this.getPassword()) && u1.getUser().equals(this.getUser())) res = true;
        return res;
    }
        
}

