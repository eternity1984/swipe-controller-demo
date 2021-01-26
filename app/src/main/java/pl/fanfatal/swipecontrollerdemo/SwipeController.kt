package pl.fanfatal.swipecontrollerdemo;

import android.graphics.*
import android.view.MotionEvent;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

enum class ButtonsState {
    GONE,
    LEFT_VISIBLE,
    RIGHT_VISIBLE
}

class SwipeController(
        private val buttonsActions: SwipeControllerActions
): ItemTouchHelper.Callback() {

    companion object {
        const val buttonWidth: Float = 300f;
    }

    private var swipeBack: Boolean = false;
    private var buttonShowedState: ButtonsState = ButtonsState.GONE;
    private var buttonInstance: RectF? = null;
    private var currentItemViewHolder: RecyclerView.ViewHolder? = null;

    override fun getMovementFlags(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT);
    }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

    }

    override fun convertToAbsoluteDirection(flags: Int, layoutDirection: Int): Int {
        if (swipeBack) {
            swipeBack = buttonShowedState != ButtonsState.GONE;
            return 0
        }
        return super.convertToAbsoluteDirection(flags, layoutDirection)
    }

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (buttonShowedState != ButtonsState.GONE) {
                var newX: Float = dX;
                if (buttonShowedState == ButtonsState.LEFT_VISIBLE) newX = newX.coerceAtLeast(buttonWidth);
                if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) newX = newX.coerceAtMost(-buttonWidth);
                super.onChildDraw(c, recyclerView, viewHolder, newX, dY, actionState, isCurrentlyActive);
            } else {
                setTouchListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }
        if (buttonShowedState == ButtonsState.GONE) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
        currentItemViewHolder = viewHolder;
    }

    private fun setTouchListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { _, event ->
            swipeBack = event.action == MotionEvent.ACTION_CANCEL || event.action == MotionEvent.ACTION_UP;
            if (swipeBack) {
                if (dX < -buttonWidth) buttonShowedState = ButtonsState.RIGHT_VISIBLE;
                else if (dX > buttonWidth) buttonShowedState = ButtonsState.LEFT_VISIBLE;

                if (buttonShowedState != ButtonsState.GONE) {
                    setTouchDownListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                    setItemsClickable(recyclerView, false);
                }
            }
            return@setOnTouchListener false;
        }
    }

    private fun setTouchDownListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                setTouchUpListener(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
            return@setOnTouchListener false;
        }
    }

    private fun setTouchUpListener(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
        recyclerView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                super.onChildDraw(c, recyclerView, viewHolder, 0F, dY, actionState, isCurrentlyActive);
                recyclerView.setOnTouchListener { _, _ -> false }
                setItemsClickable(recyclerView, true);
                swipeBack = false;

                if (buttonsActions != null && buttonInstance != null && buttonInstance!!.contains(event.getX(), event.getY())) {
                    if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
                        buttonsActions.onLeftClicked(viewHolder.getAdapterPosition());
                    } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
                        buttonsActions.onRightClicked(viewHolder.getAdapterPosition());
                    }
                }
                buttonShowedState = ButtonsState.GONE;
                currentItemViewHolder = null;
            }

            return@setOnTouchListener false;
        }
    }

    private fun setItemsClickable(recyclerView: RecyclerView, isClickable: Boolean) {
        for (i in 0 until recyclerView.childCount) {
            recyclerView.getChildAt(i).isClickable = isClickable;
        }
    }

    private fun drawButtons(c: Canvas, viewHolder: RecyclerView.ViewHolder)
    {
        val buttonWidthWithoutPadding: Float = buttonWidth - 20f;
        val corners: Float = 16f;

        val itemView: View = viewHolder.itemView;
        val p: Paint = Paint();

        val leftButton = RectF(
                itemView.left.toFloat(), itemView.top.toFloat(),
                itemView.left + buttonWidthWithoutPadding, itemView.bottom.toFloat());

        p.color = Color.BLUE;
        c.drawRoundRect(leftButton, corners, corners, p);
        drawText("EDIT", c, leftButton, p);

        val rightButton = RectF(itemView.right - buttonWidthWithoutPadding,
                itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat());

        p.color = Color.RED;

        c.drawRoundRect(rightButton, corners, corners, p);
        drawText("DELETE", c, rightButton, p);

        buttonInstance = null;
        if (buttonShowedState == ButtonsState.LEFT_VISIBLE) {
            buttonInstance = leftButton;
        } else if (buttonShowedState == ButtonsState.RIGHT_VISIBLE) {
            buttonInstance = rightButton;
        }
    }

    private fun drawText(text: String, c: Canvas, button: RectF, p: Paint) {
        val textSize: Float = 60F;
        p.color = Color.WHITE;
        p.isAntiAlias = true;
        p.textSize = textSize;

        val textWidth: Float = p.measureText(text);
        c.drawText(text, button.centerX() - (textWidth / 2f), button.centerY() + (textSize / 2f), p);
    }

    fun onDraw(c: Canvas) {
        currentItemViewHolder?.let { drawButtons(c, it) }
    }
}

