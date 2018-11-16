package click.mr_b.fprp_app;

import android.view.View;

public interface ClickListener{
    public void onClick(View view, int position);
    public void onLongClick(View view,int planId);
    public void onSwipeRight(View view, int planId);
    public void onSwipeLeft(View view, int planId);
}
