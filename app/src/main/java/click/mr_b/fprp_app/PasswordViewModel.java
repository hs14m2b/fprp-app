package click.mr_b.fprp_app;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

public class PasswordViewModel extends AndroidViewModel {
    private PasswordRepository mRepository;

    public PasswordViewModel (Application application) {
        super(application);
        mRepository = new PasswordRepository(application);
    }

    public void insert(Password password) { mRepository.insert(password); }

    public void delete(Password password) {mRepository.delete(password);}

    public void deleteAll() {mRepository.deleteAll();}

    public Password getPassword(){return mRepository.getPassword();}

    public int countEntries(){return mRepository.countEntries();}
}
