package click.mr_b.myapplication;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

@Database(entities = {Plan.class, Password.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PlanDao planDao();
    public abstract PasswordDao passwordDao();

    private static AppDatabase INSTANCE;

    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "plan_database")
                            .allowMainThreadQueries()
                            .addCallback(sRoomDatabaseCallback)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final PlanDao mDao;

        PopulateDbAsync(AppDatabase db) {
            mDao = db.planDao();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            if (mDao.countEntries() == 0) {
                mDao.deleteAll();
                Plan plan = new Plan("Example Plan...");
                plan.setQuestion1("I am unable to sleep well");
                plan.setQuestion2("My negative behaviours include binge drinking at the weekends");
                plan.setQuestion3("Wanting to be part of the crowd");
                plan.setQuestion4("My limitations include...");
                plan.setQuestion5("I can change how much I drink");
                plan.setQuestion6("My problem is that I don't want to appear boring");
                plan.setPoint1("I will drink shandy");
                plan.setPoint2("I will only have single shots");
                plan.setPoint3("I will only drink alcohol at the weekends");
                plan.setPoint4("I will not watch TV in bed");
                plan.setPoint5("I will do more physical exercise");
                mDao.insert(plan);
            }
            return null;
        }
    }
}