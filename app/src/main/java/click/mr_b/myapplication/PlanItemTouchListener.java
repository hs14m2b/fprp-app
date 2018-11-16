package click.mr_b.myapplication;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import static android.support.v4.app.ActivityCompat.startActivityForResult;

public class PlanItemTouchListener implements RecyclerView.OnItemTouchListener {

    Context context;
    private GestureDetector gestureDetector;
    private PlanGestureListener gestureListener;
    private PlanViewModel mPlanViewModel;
    private ClickListener clickListener;

    PlanItemTouchListener(final Context context, RecyclerView recycleView, PlanViewModel planViewModel, ClickListener clickListener){
        this.context = context;
        this.mPlanViewModel = planViewModel;
        this.clickListener = clickListener;
        gestureDetector = new GestureDetector(context, new PlanGestureListener(recycleView, planViewModel));
    }

    @Override
    public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
        boolean consumed = gestureDetector.onTouchEvent(e);
        return false;
    }

    @Override
    public void onTouchEvent(RecyclerView rv, MotionEvent e) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

    }

    class PlanGestureListener extends GestureDetector.SimpleOnGestureListener{
        private RecyclerView recyclerView;
        private PlanViewModel planViewModel;

        PlanGestureListener(RecyclerView recyclerView, PlanViewModel planViewModel)
        {
            this.recyclerView = recyclerView;
            this.planViewModel = planViewModel;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            View child=recyclerView.findChildViewUnder(e.getX(),e.getY());
            if (child!= null) {
                int position = recyclerView.getChildAdapterPosition(child);
                Log.d(this.getClass().getSimpleName(), "Touch on position :" + position);
                clickListener.onClick(child, position);
                return true;
            }
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
            View child=recyclerView.findChildViewUnder(e.getX(),e.getY());
            if (child!=null) {
                int position = recyclerView.getChildAdapterPosition(child);
                //Toast.makeText(context, "Long Touch on position :" + position, Toast.LENGTH_SHORT).show();
                Log.d(this.getClass().getSimpleName(), "Long Touch on position :" + position);
                //get plan id
                TextView planIdView = child.findViewById(R.id.planId);
                try {
                    int planId = Integer.parseInt(planIdView.getText().toString());
                    //raise event, pass plan ID back
                    clickListener.onLongClick(child, planId);
                }
                catch (Exception ex)
                {
                    //Toast.makeText(context, "Failed to raise event!!!", Toast.LENGTH_SHORT).show();
                    Log.d(this.getClass().getSimpleName(), "Failed to raise event!!!");
                }
            }
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY)
        {
            View child1=recyclerView.findChildViewUnder(e1.getX(),e1.getY());
            View child2=recyclerView.findChildViewUnder(e2.getX(),e2.getY());
            if (child1!=null && child2!=null) {
                int position1 = recyclerView.getChildAdapterPosition(child1);
                int position2 = recyclerView.getChildAdapterPosition(child2);
                float xDelta = e2.getX() - e1.getX();
                if (position1 == position2) {
                    //Toast.makeText(context, "Fling on position :" + position1 + " xDelta is " + xDelta, Toast.LENGTH_SHORT).show();
                    Log.d(this.getClass().getSimpleName(), "Fling on position :" + position1 + " xDelta is " + xDelta);
                    TextView planIdView = child1.findViewById(R.id.planId);
                    if (xDelta > 500) {
                        //delete the item
                        try {
                            int planId = Integer.parseInt(planIdView.getText().toString());
                            clickListener.onSwipeRight(child1, planId);
                            return false;
                        } catch (Exception ex) {
                            return true;
                        }
                    }
                    else if (xDelta < -300)
                    {
                        try {
                            int planId = Integer.parseInt(planIdView.getText().toString());
                            clickListener.onSwipeLeft(child1, planId);
                            return false;
                        } catch (Exception ex) {
                            return true;
                        }
                    }
                }
            }
            return true;
        }

    }
}
