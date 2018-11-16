package click.mr_b.fprp_app;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import java.util.List;

public class PlanRepository {

    private PlanDao mPlanDao;
    private LiveData<List<Plan>> mAllPlans;

    PlanRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        mPlanDao = db.planDao();
        mAllPlans = mPlanDao.getAll();
    }

    LiveData<List<Plan>> getAllPlans() {
        return mAllPlans;
    }


    public void insert (Plan plan) {
        new insertAsyncTask(mPlanDao).execute(plan);
    }

    public void delete (Plan plan) {
        new deleteAsyncTask(mPlanDao).execute(plan);
    }

    public void deleteAll () {
        new deleteAllAsyncTask(mPlanDao).execute(new Plan());
    }

    private static class insertAsyncTask extends AsyncTask<Plan, Void, Void> {

        private PlanDao mAsyncTaskDao;

        insertAsyncTask(PlanDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Plan... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }
    }

    private static class deleteAsyncTask extends AsyncTask<Plan, Void, Void> {

        private PlanDao mAsyncTaskDao;

        deleteAsyncTask(PlanDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Plan... params) {
            mAsyncTaskDao.delete(params[0]);
            return null;
        }
    }

    private static class deleteAllAsyncTask extends AsyncTask<Plan, Void, Void> {

        private PlanDao mAsyncTaskDao;

        deleteAllAsyncTask(PlanDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(Plan...params) {
            mAsyncTaskDao.deleteAll();
            return null;
        }
    }

    public Plan getPlanById(int id){return mPlanDao.getPlanById(id);}
}