package click.mr_b.myapplication;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface PasswordDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Password password);

    @Update
    void update(Password password);

    @Delete
    void delete(Password password);

    @Query("DELETE FROM password")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM password")
    int countEntries();

    @Query("SELECT * FROM password WHERE id=0")
    public Password getPassword();
}
