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
public interface PlanDao {
    @Query("SELECT * FROM plan")
    LiveData<List<Plan>> getAll();

    @Insert
    void insertAll(Plan... plans);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Plan plan);

    @Update
    void update(Plan plan);

    @Delete
    void delete(Plan plan);

    @Query("DELETE FROM plan")
    void deleteAll();

    @Query("SELECT COUNT(*) FROM plan")
    int countEntries();

    @Query("SELECT * FROM plan WHERE id= :id")
    public Plan getPlanById(int id);
}
