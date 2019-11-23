package click.mr_b.fprp_app;

// SwipeController.java
import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper.Callback;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import static android.support.v7.widget.helper.ItemTouchHelper.*;

class SwipeController extends Callback {

    private ClickListener clickListener;
    private RecyclerView recyclerView;
    private RecyclerView.ViewHolder viewHolder;

    SwipeController(ClickListener clickListener)
    {
        this.clickListener = clickListener;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        return makeMovementFlags(0, LEFT | RIGHT);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        Log.d("SwipeController", "Item swiped " + viewHolder.getLayoutPosition());
    }

    private boolean swipeBack = false;
    private boolean triggerDelete = false;
    @Override
    public int convertToAbsoluteDirection(int flags, int layoutDirection) {
        Log.d("SwipeController", "convertToAbsoluteDirection " + flags + " " + layoutDirection);
        if (swipeBack) {
            swipeBack = false;
            if (triggerDelete) {

                TextView planIdView = this.viewHolder.itemView.findViewById(click.mr_b.fprp_app.R.id.planId);
                try {
                    int planId = Integer.parseInt(planIdView.getText().toString());
                    this.clickListener.onSwipeRight(this.viewHolder.itemView, planId);
                }
                catch (Exception ex) {
                    Log.e("SwipeController", "Unable to establish plan id from position");
                }
            }
            return 0;
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection);
    }

    @Override
    public void onChildDraw(Canvas c,
                            RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        Log.d("SwipeController", "dX is " + dX);
        if (dX > 400 || dX < -400) triggerDelete = true;
        if (actionState == ACTION_STATE_SWIPE) {
            setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }

    private void setTouchListener(Canvas c,
                                  RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder,
                                  float dX, float dY,
                                  int actionState, boolean isCurrentlyActive) {
        this.recyclerView = recyclerView;
        this.viewHolder = viewHolder;

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeBack = event.getAction() == MotionEvent.ACTION_CANCEL || event.getAction() == MotionEvent.ACTION_UP;
                return false;
            }
        });
    }}