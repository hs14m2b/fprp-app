package click.mr_b.myapplication;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class PasswordRepository {

    private PasswordDao mPasswordDao;
    private int mPasswordCount = 0;
    private Password mPassword;

    PasswordRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mPasswordDao = db.passwordDao();
        mPasswordCount = mPasswordDao.countEntries();
        if (mPasswordCount > 0)
        {
            mPassword = getPassword();
        }
    }

    public void insert (Password password) {
        new insertAsyncTask(mPasswordDao).execute(password);
    }

    public void delete (Password password) {
        new deleteAsyncTask(mPasswordDao).execute(password);
    }

    private static class insertAsyncTask extends AsyncTask<Password, Void, Void> {

        private PasswordDao mAsyncTaskDao;

        insertAsyncTask(PasswordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Password... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Password, Void, Void> {

        private PasswordDao mAsyncTaskDao;

        deleteAsyncTask(PasswordDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Password... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    public Password getPassword(){return mPasswordDao.getPassword();}

    public int countEntries(){return mPasswordCount;}

    public void deleteAll() {mPasswordDao.deleteAll();}
}