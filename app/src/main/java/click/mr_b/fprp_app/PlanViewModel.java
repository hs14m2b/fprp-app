package click.mr_b.fprp_app;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.util.Log;

import java.util.List;

public class PlanViewModel extends AndroidViewModel {
    private PlanRepository mRepository;
    private LiveData<List<Plan>> mAllPlans;
    PlanEncryptionHandler planEncryptionHandler;

    public PlanViewModel (Application application) {
        super(application);
        mRepository = new PlanRepository(application);
        mAllPlans = mRepository.getAllPlans();
        this.planEncryptionHandler = new PlanEncryptionHandler();
        Log.d("PlanViewModel", "created PlanEncryptionHandler");
    }


    LiveData<List<Plan>> getAllPlans() {
        return mAllPlans;
    }

    public void insert(Plan plan) {
        Log.d("PlanViewModel - insert", "Entered insert plan for plan number " + plan.getId());
        Log.d("PlanViewModel - insert", "Plan details " + plan.toString());
        Plan encPlan = planEncryptionHandler.encrypt(plan);
        Log.d("PlanViewModel - insert", "Encrypted the plan " + encPlan.toString());
        mRepository.insert(encPlan);
    }

    public void delete(Plan plan) {mRepository.delete(plan);}

    public void deleteAll(){mRepository.deleteAll();}

    public Plan getPlanById(int id){
        Log.d("PlanViewModel - getPlanById", "Entered getPlanById plan for plan number " + id);
        Plan plan = mRepository.getPlanById(id);
        Log.d("PlanViewModel - getPlanById", "Plan details " + plan.toString());
        Plan decPlan = planEncryptionHandler.decrypt(plan);
        Log.d("PlanViewModel - getPlanById", "Decrypted plan details " + decPlan.toString());
        return decPlan;
    }
}
