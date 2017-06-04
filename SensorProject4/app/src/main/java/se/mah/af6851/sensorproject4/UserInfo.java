package se.mah.af6851.sensorproject4;

/**
 * Created by Amar on 2017-02-21.
 */

public class UserInfo {

    private String _name, _password;
    private int _steps;

    public UserInfo(String _name, String _password, int _steps){
        this._name = _name;
        this._password = _password;
        this._steps = _steps;
    }
    public UserInfo(int _steps){
        this._steps = _steps;
    }

    public String get_name(){
        return _name;
    }
    public void set_name(String _name){
        this._name = _name;
    }

    public String get_password(){
        return _password;
    }
    public void set_password(String _password){
        this._password = _password;
    }
    public int get_steps(){
        return _steps;
    }
    public void set_steps(int _steps){
        this._steps = _steps;
    }
}
