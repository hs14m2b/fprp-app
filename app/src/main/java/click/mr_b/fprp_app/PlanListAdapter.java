package click.mr_b.fprp_app;

import android.content.Context;
import android.content.res.Resources;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class PlanListAdapter extends RecyclerView.Adapter<PlanListAdapter.PlanViewHolder> {

    class PlanViewHolder extends RecyclerView.ViewHolder {
        private final TextView planItemView;
        private final TextView planIdView;
        private TextView mEditQ1View;
        private TextView mEditQ2View;
        private TextView mEditQ3View;
        private TextView mEditQ4View;
        private TextView mEditQ5View;
        private TextView mEditQ6View;
        private TextView mEditRP1View;
        private TextView mEditRP2View;
        private TextView mEditRP3View;
        private TextView mEditRP4View;
        private TextView mEditRP5View;

        private PlanViewHolder(View itemView) {
            super(itemView);
            planItemView = itemView.findViewById(click.mr_b.fprp_app.R.id.planName);
            planIdView = itemView.findViewById(click.mr_b.fprp_app.R.id.planId);
            mEditQ1View = itemView.findViewById(click.mr_b.fprp_app.R.id.pd_q1);
            mEditQ2View = itemView.findViewById(click.mr_b.fprp_app.R.id.pd_q2);
            mEditQ3View = itemView.findViewById(click.mr_b.fprp_app.R.id.pd_q3);
            mEditQ4View = itemView.findViewById(click.mr_b.fprp_app.R.id.pd_q4);
            mEditQ5View = itemView.findViewById(click.mr_b.fprp_app.R.id.pd_q5);
            mEditQ6View = itemView.findViewById(click.mr_b.fprp_app.R.id.pd_q6);
            mEditRP1View = itemView.findViewById(click.mr_b.fprp_app.R.id.pd_rp1);
            mEditRP2View = itemView.findViewById(click.mr_b.fprp_app.R.id.pd_rp2);
            mEditRP3View = itemView.findViewById(click.mr_b.fprp_app.R.id.pd_rp3);
            mEditRP4View = itemView.findViewById(click.mr_b.fprp_app.R.id.pd_rp4);
            mEditRP5View = itemView.findViewById(click.mr_b.fprp_app.R.id.pd_rp5);

        }
    }

    private final LayoutInflater mInflater;
    private List<Plan> mPlans; // Cached copy of words
    private Context mContext;
    private Resources res;
    PlanEncryptionHandler planEncryptionHandler;
    PlanListAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.res = mContext.getResources();
        this.planEncryptionHandler = new PlanEncryptionHandler();
        Log.d("PlanListAdapter", "created PlanEncryptionHandler");
    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(click.mr_b.fprp_app.R.layout.recyclerview_item, parent, false);
        return new PlanViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, int position) {
        if (mPlans != null) {
            Plan current = planEncryptionHandler.decrypt(mPlans.get(position));
            holder.planItemView.setText(current.getPlanName());
            holder.planIdView.setText(current.getId() + "");
            holder.mEditQ1View.setText(current.getQuestion1());
            holder.mEditQ2View.setText(current.getQuestion2());
            holder.mEditQ3View.setText(current.getQuestion3());
            holder.mEditQ4View.setText(current.getQuestion4());
            holder.mEditQ5View.setText(current.getQuestion5());
            holder.mEditQ6View.setText(current.getQuestion6());
            holder.mEditRP1View.setText(current.getPoint1());
            holder.mEditRP2View.setText(current.getPoint2());
            holder.mEditRP3View.setText(current.getPoint3());
            holder.mEditRP4View.setText(current.getPoint4());
            holder.mEditRP5View.setText(current.getPoint5());
        } else {
            // Covers the case of data not being ready yet.
            holder.planIdView.setText("-1");
            holder.planItemView.setText("No Plans");
        }
    }

    void setPlans(List<Plan> plans){
        Log.d("PlanListAdapter", "entered setPlans");
        mPlans = plans;
        Log.d("PlanListAdapter", "set mPlans variable - now will decrypt values");
        for (int i = 0; i < mPlans.size(); i++)
        {
            Plan decPlan = planEncryptionHandler.decrypt(mPlans.get(i));
            Log.d("PlanListAdapter", "Original Plan data " + mPlans.get(i).toString());
            Log.d("PlanListAdapter", "Decrypted Plan data " + decPlan.toString());
            mPlans.set(i, decPlan);
        }
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mWords has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mPlans != null)
            return mPlans.size();
        else return 0;
    }
}