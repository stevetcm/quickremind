package com.orangemuffin.quickremind.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orangemuffin.quickremind.R;
import com.orangemuffin.quickremind.activities.ReminderEditActivity;
import com.orangemuffin.quickremind.database.ReminderDatabase;
import com.orangemuffin.quickremind.fragments.FragmentEnded;
import com.orangemuffin.quickremind.fragments.FragmentUpcoming;
import com.orangemuffin.quickremind.models.Reminder;
import com.orangemuffin.quickremind.receivers.AlarmReceiver;
import com.orangemuffin.quickremind.utils.DateAndTimeUtil;
import com.orangemuffin.quickremind.utils.NotificationUtil;

import java.util.List;

/* Created by OrangeMuffin on 6/26/2017 */
public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ViewHolder> {
    private List<Reminder> reminderList;
    private Context context;
    private Fragment parentFragment;

    public ReminderAdapter(List<Reminder> reminderList, Context context, Fragment parentFragment) {
        this.parentFragment = parentFragment;
        this.reminderList = reminderList;
        this.context = context;
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    @Override
    public void onBindViewHolder(ReminderAdapter.ViewHolder viewHolder, int i) {
        Reminder reminder = reminderList.get(i);
        viewHolder.card_content.setText(reminder.getContent());

        int duedate = DateAndTimeUtil.getDueDate(reminder.getDate());

        //modify display date according to due date
        if (duedate == 0) {
            viewHolder.card_date_text.setText("Today");
        } else if (duedate == 1) {
            viewHolder.card_date_text.setText("Tomorrow");
        } else if (duedate == -1) {
            viewHolder.card_date_text.setText("Yesterday");
        } else {
            viewHolder.card_date_text.setText(reminder.getDate());
        }

        //display converted time
        viewHolder.card_time_text.setText(DateAndTimeUtil.convertTime("hh:mm a", reminder.getTime()));

        int separator_id = viewHolder.card_separator.getId();
        int content_id = viewHolder.card_content.getId();

        //configure repeat display
        if (reminder.getRepeat().equals("true")) {
            viewHolder.card_repeat_img.setVisibility(View.VISIBLE);
        } else {
            viewHolder.card_repeat_img.setVisibility(View.GONE);
        }
        if (reminder.getRepeat().equals("true") && reminder.getPersistent().equals("true")) {
            viewHolder.card_separator.setVisibility(View.VISIBLE);

            //rescale left and top margin on persistent icon for symmetry display
            RelativeLayout.LayoutParams params = rescaleView("dual", separator_id, content_id);
            viewHolder.card_persistent_img.setLayoutParams(params);
        } else {
            viewHolder.card_separator.setVisibility(View.GONE);
        }

        //configure persistent display
        if (reminder.getPersistent().equals("true")) {
            viewHolder.card_persistent_img.setVisibility(View.VISIBLE);

            //if single icon, need to reset params programmatically
            if (reminder.getRepeat().equals("false")) {
                RelativeLayout.LayoutParams params = rescaleView("single", separator_id, content_id);
                viewHolder.card_persistent_img.setLayoutParams(params);
            }
        } else {
            viewHolder.card_persistent_img.setVisibility(View.GONE);
        }
    }

    @Override
    public ReminderAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.reminder_cardview, viewGroup, false);
        return new ViewHolder(rootView);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView card_content, card_date_text, card_time_text;
        ImageView card_date_img, card_time_img, card_repeat_img, card_persistent_img, overflow_more;
        View card_separator;

        RelativeLayout relativeLayout;

        public ViewHolder(View view) {
            super(view);

            relativeLayout = (RelativeLayout) view.findViewById(R.id.card_element);

            //setting up onClick for the entire layout
            relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDisplay(getAdapterPosition());
                }
            });

            card_content = (TextView) view.findViewById(R.id.card_content);
            card_date_text = (TextView) view.findViewById(R.id.card_date_text);
            card_time_text = (TextView) view.findViewById(R.id.card_time_text);
            card_date_img = (ImageView) view.findViewById(R.id.card_date_img);
            card_time_img = (ImageView) view.findViewById(R.id.card_time_img);
            card_repeat_img = (ImageView) view.findViewById(R.id.card_repeat_img);
            card_persistent_img = (ImageView) view.findViewById(R.id.card_persistent_img);
            overflow_more = (ImageView) view.findViewById(R.id.overflow_more);
            card_separator = view.findViewById(R.id.card_separator);

            //setting up onClick for overflow icon
            overflow_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showPopupMenu(overflow_more, getAdapterPosition());
                }
            });
        }
    }

    //show reminder display dialog on layout click
    public void showDisplay(final int position) {
        final Reminder reminder = reminderList.get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.reminder_display_dialog, null);
        dialog.setView(dialogView);

        TextView content_text = (TextView) dialogView.findViewById(R.id.content_text);
        TextView date_text = (TextView) dialogView.findViewById(R.id.date_text);
        TextView date_text2 = (TextView) dialogView.findViewById(R.id.date_text2);
        TextView time_text = (TextView) dialogView.findViewById(R.id.time_text);
        TextView date_actual = (TextView) dialogView.findViewById(R.id.date_actual_text);
        TextView repeat_text = (TextView) dialogView.findViewById(R.id.repeat_text);
        ImageView repeat_img = (ImageView) dialogView.findViewById(R.id.repeat_img);
        TextView persistent_text = (TextView) dialogView.findViewById(R.id.persistent_text);
        ImageView persistent_img = (ImageView) dialogView.findViewById(R.id.persistent_img);

        //setting up static content
        content_text.setText(reminder.getContent());
        date_text.setText(DateAndTimeUtil.dateToDay(reminder.getDate()));
        date_text2.setText(DateAndTimeUtil.convertDate("dd MMMM yyyy", reminder.getDate()));
        time_text.setText("at " + DateAndTimeUtil.convertTime("hh:mm a", reminder.getTime()));

        //setting up due date indicator
        int duedate = DateAndTimeUtil.getDueDate(reminder.getDate());
        if (duedate == 0) {
            date_actual.setText("Today");
        } else if (duedate == 1) {
            date_actual.setText("Tomorrow");
        } else if (duedate == -1) {
            date_actual.setText("Yesterday");
        } else if (duedate > 1) {
            date_actual.setText("In " + duedate + " days");
        } else {
            date_actual.setText(Math.abs(duedate) + " days ago");
        }

        //setting up repeat display
        if (reminder.getRepeat().equals("false")) {
            repeat_img.setImageResource(R.drawable.ic_repeat_off_gray);
            repeat_text.setText("Do not Repeat");
        } else {
            repeat_text.setText("Every " + reminder.getRepeatNo() + " " + reminder.getRepeatType());
        }

        //setting up persistent display
        if (reminder.getPersistent().equals("true")) {
            persistent_text.setText("Persistent");
        } else {
            persistent_img.setImageResource(R.drawable.ic_pushpin_off_gray);
            persistent_text.setText("Not Persistent");
        }

        //setting up dialog buttons
        dialog.setPositiveButton("Ok", null);
        dialog.setNegativeButton("EDIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openEditActivity(reminder.getId());
            }
        });
        dialog.setNeutralButton("DELETE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteReminder(reminder, position);
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void showPopupMenu(final View view, final int position) {
        final PopupMenu popupMenu = new PopupMenu(context, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.menu_reminder, popupMenu.getMenu());

        final Reminder reminder = reminderList.get(position);

        //remove "stop repeating" button on conditions
        if (parentFragment instanceof FragmentEnded) {
            popupMenu.getMenu().getItem(0).setVisible(false);
        } else if (!reminder.getRepeat().equals("true")) {
            popupMenu.getMenu().getItem(0).setVisible(false);
        }

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.stop:
                        new AlarmReceiver().cancelRepeating(context, reminder, new ReminderDatabase(context));
                        return true;
                    case R.id.show:
                        NotificationUtil.createNotification(context, reminder);
                        return true;
                    case R.id.edit:
                        openEditActivity(reminder.getId());
                        return true;
                    case R.id.delete:
                        deleteReminder(reminder, position);
                        return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    public void openEditActivity(final int reminderID) {
        Intent intent = new Intent(context, ReminderEditActivity.class);
        intent.putExtra("REMINDER_ID", reminderID);
        context.startActivity(intent);
        ((Activity) context).overridePendingTransition(R.anim.slide_up, R.anim.anim_stay);
    }

    public void deleteReminder(final Reminder reminder, final int position) {
        AlertDialog.Builder dialogInner = new AlertDialog.Builder(context);
        String warning_text = "Are you sure you want to delete this reminder?";
        dialogInner.setMessage(warning_text);
        dialogInner.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ReminderDatabase rb = new ReminderDatabase(context);

                //cancel reminder alarm using its ID
                new AlarmReceiver().cancelAlarm(context, reminder.getId());

                //delete reminder in the database
                rb.deleteReminder(reminder);

                //remove reminder in the displayed list
                reminderList.remove(position);

                //notify adapter of removed item
                notifyItemRemoved(position);
                notifyDataSetChanged();

                //update display if list is now empty
                if (parentFragment instanceof FragmentUpcoming) {
                    ((FragmentUpcoming) parentFragment).checkEmpty();
                } else if (parentFragment instanceof FragmentEnded) {
                    ((FragmentEnded) parentFragment).checkEmpty();
                }
            }
        });
        dialogInner.setNegativeButton("No", null);
        dialogInner.show();
    }

    public RelativeLayout.LayoutParams rescaleView(String mode, int separator_id, int content_id) {
        int wrap_content = RelativeLayout.LayoutParams.WRAP_CONTENT;
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(wrap_content, wrap_content);
        if (mode.equals("dual")) { //display of 2 icon
            params.setMargins(0, (int) dpToPixel(6), 0, 0);
        } else { //single icon, reset params
            params.setMargins((int) dpToPixel(13), (int) dpToPixel(6), 0, 0);
        }
        params.width = (int) dpToPixel(17);
        params.height = (int) dpToPixel(17);
        params.addRule(RelativeLayout.RIGHT_OF, separator_id);
        params.addRule(RelativeLayout.BELOW, content_id);

        return params;
    }

    public float dpToPixel(int dp) {
        Resources r = Resources.getSystem();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }
}
