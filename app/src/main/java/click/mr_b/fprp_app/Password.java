package click.mr_b.fprp_app;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import java.security.MessageDigest;

@Entity
public class Password {

    @PrimaryKey(autoGenerate = false)
    private int id = 0;
    public int getId(){return id;}
    public void setId(int id) {this.id = 0;}

    @ColumnInfo(name = "pwdHash")
    public String pwdHash;
    public String getPwdHash() {
        return pwdHash;
    }
    public void setPwdHash(String password) {
        this.pwdHash = getHash(password);
    }


    Password(){
        pwdHash = "";
    }

    Password(String password){
        this.pwdHash = getHash(password);
    }

    public static String getHash(String stringToHash)
    {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(stringToHash.getBytes());
            String encryptedString = new String(messageDigest.digest());
            return encryptedString;
        }
        catch (Exception ex)
        {
            Log.d(Password.class.getSimpleName(), "Unable to calculate password hash");
            return "";
        }
    }

}
