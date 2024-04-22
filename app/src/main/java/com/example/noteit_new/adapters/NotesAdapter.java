package com.example.noteit_new.adapters;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.constraintlayout.helper.widget.MotionEffect;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.noteit_new.Entity.Note;
import com.example.noteit_new.Listeners.NotesListener;
import com.example.noteit_new.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/* loaded from: classes.dex */
public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.ViewHolder> {
    private List<Note> notes;
    private NotesListener notesListener;
    private List<Note> notesSource;
    private Timer timer;

    public NotesAdapter(List<Note> notes, NotesListener notesListener) {
        this.notes = notes;
        this.notesListener = notesListener;
        this.notesSource = notes;
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Log.d(MotionEffect.TAG, "onCreateViewHolder: Called");
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_note, parent, false));
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public void onBindViewHolder(final ViewHolder holder, int position) {
        Log.d(MotionEffect.TAG, "onBindViewHolder: Called for position " + position);
        holder.setNote(this.notes.get(position));
        holder.layoutNote.setOnClickListener(new View.OnClickListener() { // from class: com.example.noteit.adapters.NotesAdapter$$ExternalSyntheticLambda0
            @Override // android.view.View.OnClickListener
            public final void onClick(View view) {
                NotesAdapter.this.m115x767cf9c4(holder, view);
            }
        });
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* renamed from: lambda$onBindViewHolder$0$com-example-noteit-adapters-NotesAdapter  reason: not valid java name */
    public /* synthetic */ void m115x767cf9c4(ViewHolder viewHolder, View view) {
        int bindingAdapterPosition = viewHolder.getBindingAdapterPosition();
        if (bindingAdapterPosition != -1) {
            this.notesListener.onNoteClicked(this.notes.get(bindingAdapterPosition), bindingAdapterPosition);
        }
    }

    @Override // androidx.recyclerview.widget.RecyclerView.Adapter
    public int getItemCount() {
        StringBuilder sb = new StringBuilder("getItemCount: ");
        List<Note> list = this.notes;
        Log.d(MotionEffect.TAG, sb.append(list == null ? 0 : list.size()).toString());
        List<Note> list2 = this.notes;
        if (list2 == null) {
            return 0;
        }
        return list2.size();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: classes.dex */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        RoundedImageView imageNote;
        LinearLayout layoutNote;
        TextView textDateTime;
        TextView textSubtitle;
        TextView textTitle;

        ViewHolder(View itemView) {
            super(itemView);
            this.textTitle = (TextView) itemView.findViewById(R.id.textTitle);
            this.textSubtitle = (TextView) itemView.findViewById(R.id.textSubtitle);
            this.textDateTime = (TextView) itemView.findViewById(R.id.textDateTime);
            this.layoutNote = (LinearLayout) itemView.findViewById(R.id.layoutNote);
            this.imageNote = (RoundedImageView) itemView.findViewById(R.id.imageNote);
        }

        void setNote(Note note) {
            if (note != null) {
                this.textTitle.setText(note.getTitle());
                String subtitle = note.getSubtitle();
                if (subtitle == null || subtitle.trim().isEmpty()) {
                    this.textSubtitle.setVisibility(8);
                } else {
                    this.textSubtitle.setVisibility(0);
                    this.textSubtitle.setText(subtitle);
                }
                this.textDateTime.setText(note.getDateTime());
                GradientDrawable gradientDrawable = (GradientDrawable) this.layoutNote.getBackground();
                if (note.getColor() != null) {
                    gradientDrawable.setColor(Color.parseColor(note.getColor()));
                    if (note.getColor().equals("#d0f4de")) {
                        this.textTitle.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.colorBlack));
                        this.textSubtitle.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.colorBlack));
                        this.textDateTime.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.colorBlack));
                    } else if (note.getColor().equals("#fcf6bd")) {
                        this.textTitle.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.colorBlack));
                        this.textSubtitle.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.colorBlack));
                        this.textDateTime.setTextColor(ContextCompat.getColor(this.itemView.getContext(), R.color.colorBlack));
                    }
                } else {
                    gradientDrawable.setColor(Color.parseColor("#333333"));
                }
                String imagePath = note.getImagePath();
                if (imagePath != null && !imagePath.isEmpty()) {
                    Glide.with(this.itemView.getContext()).load(new File(imagePath)).into(this.imageNote);
                    this.imageNote.setVisibility(0);
                    return;
                }
                this.imageNote.setVisibility(8);
            }
        }
    }

    public void searchNote(final String searchKeyword) {
        Timer timer = new Timer();
        this.timer = timer;
        timer.schedule(new TimerTask() { // from class: com.example.noteit.adapters.NotesAdapter.1
            @Override // java.util.TimerTask, java.lang.Runnable
            public void run() {
                if (searchKeyword.trim().isEmpty()) {
                    NotesAdapter notesAdapter = NotesAdapter.this;
                    notesAdapter.notes = notesAdapter.notesSource;
                } else {
                    ArrayList arrayList = new ArrayList();
                    for (Note note : NotesAdapter.this.notesSource) {
                        if (note.getTitle().toLowerCase().contains(searchKeyword.toLowerCase()) || note.getSubtitle().toLowerCase().contains(searchKeyword.toLowerCase()) || note.getNoteText().toLowerCase().contains(searchKeyword.toLowerCase())) {
                            arrayList.add(note);
                        }
                    }
                    NotesAdapter.this.notes = arrayList;
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() { // from class: com.example.noteit.adapters.NotesAdapter.1.1
                    @Override // java.lang.Runnable
                    public void run() {
                        NotesAdapter.this.notifyDataSetChanged();
                    }
                });
            }
        }, 500L);
    }

    public void cancelTimer() {
        Timer timer = this.timer;
        if (timer != null) {
            timer.cancel();
        }
    }
}
