package com.example.noteit_new;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.example.noteit_new.Activities.CreateNoteActivity;
import com.example.noteit_new.Entity.Note;
import com.example.noteit_new.Listeners.NotesListener;
import com.example.noteit_new.RecyclerTouchListener.RecyclerTouchListener;
import com.example.noteit_new.adapters.NotesAdapter;
import com.example.noteit_new.database.NotesDatabase;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity implements NotesListener {
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    private static final int REQUEST_CODE_SELECT_IMAGE = 4;
    private static final int REQUEST_CODE_SHOW_NOTES = 3;
    private static final int REQUEST_CODE_STORAGE_PERMISSION = 5;
    public static final int REQUEST_CODE_UPDATE_NOTE = 2;
    private AlertDialog dialogAddURL;
    private AlertDialog dialogDeleteNote;
    ImageView noNoteImage;
    TextView noNoteText;
    Note noteToDelete;
    private NotesAdapter notesAdapter;
    private List<Note> notesList;
    private RecyclerView notesRecyclerView;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private int noteClickedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noNoteImage =  findViewById(R.id.noNoteImage);
        noNoteText =  findViewById(R.id.noNoteText);
        RecyclerView recyclerView =  findViewById(R.id.recyclerViewMain);
        notesRecyclerView = recyclerView;

        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, 1));

        notesList = new ArrayList<>();

        getAllNotes(REQUEST_CODE_SHOW_NOTES, false);



        findViewById(R.id.imageAddNoteMain).setOnClickListener(view -> startActivityForResult(new Intent(MainActivity.this, CreateNoteActivity.class), 1));

        this.notesAdapter = new NotesAdapter(this.notesList, this);
        notesRecyclerView.setAdapter(notesAdapter);
        notesRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), this.notesRecyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                // Implement click handling if needed
            }

            @Override
            public void onLongClick(View view, final int position) {
                noteToDelete = notesList.get(position);
                if (dialogDeleteNote == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    View inflate = LayoutInflater.from(MainActivity.this.getApplicationContext()).inflate(R.layout.layout_delete_note, (ViewGroup) MainActivity.this.findViewById(R.id.layoutDeleteNoteContainer));
                    builder.setView(inflate);
                    dialogDeleteNote = builder.create();
                    if (dialogDeleteNote.getWindow() != null) {
                        dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
                    }
                    inflate.findViewById(R.id.textDeleteNoteDialog).setOnClickListener(view2 -> executorService.execute(() -> {
                        int noteIndex = notesList.indexOf(noteToDelete);
                        if (noteIndex != -1) {
                            NotesDatabase.getDatabase(MainActivity.this.getApplicationContext()).notesDao().deletenote(noteToDelete);
                            runOnUiThread(() -> {
                                notesList.remove(noteIndex);
                                notesAdapter.notifyItemRemoved(noteIndex);
                                dialogDeleteNote.dismiss();
                                if (notesList.isEmpty()) {
                                    noNoteImage.setVisibility(View.VISIBLE);
                                    noNoteText.setVisibility(View.VISIBLE);
                                } else {
                                    noNoteImage.setVisibility(View.GONE);
                                    noNoteText.setVisibility(View.GONE);
                                }
                            });
                        } else {
                            // Handle the case when noteToDelete is not present in the notesList
                            Log.e("MainActivity", "Note to be deleted not found in the list");
                        }
                    }));
                    inflate.findViewById(R.id.textCancel).setOnClickListener(view2 -> dialogDeleteNote.dismiss());
                }
                dialogDeleteNote.show();
            }
        }));

        ((EditText) findViewById(R.id.inputSearch)).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                notesAdapter.cancelTimer();
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (notesList.size() != 0) {
                    notesAdapter.searchNote(s.toString());
                }
            }
        });

        findViewById(R.id.imageAddNote).setOnClickListener(view -> startActivityForResult(new Intent(getApplicationContext(), CreateNoteActivity.class), 1));
        findViewById(R.id.imageAddImage).setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.READ_MEDIA_IMAGES") != 0) {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{"android.permission.READ_MEDIA_IMAGES"}, 5);
            }
            selectImage();
        });

        findViewById(R.id.imageAddWebLink).setOnClickListener(view -> showAddURLDialog());
    }

    private void selectImage() {
        Intent intent = new Intent("android.intent.action.PICK", MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, 2);
        }
    }

    private String getPathFromUri(Uri contentUri) {
        Cursor query = getContentResolver().query(contentUri, new String[]{"_data"}, null, null, null);
        if (query == null || !query.moveToFirst()) {
            return null;
        }
        String string = query.getString(query.getColumnIndexOrThrow("_data"));
        Log.d("CreateNoteActivity", "File Path: " + string);
        query.close();
        return string;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == 0) {
                selectImage();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onNoteClicked(Note note, int position) {
        this.noteClickedPosition = position;
        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
        intent.putExtra("isViewOrUpdate", true);
        intent.putExtra("note", note);
        intent.putExtra("position", this.noteClickedPosition);
        startActivityForResult(intent, 3);
    }

    public void getAllNotes(final int requestcode, final boolean isNoteDeleted) {
        this.executorService.execute(new Runnable() {
            @Override
            public void run() {
                final List<Note> list = NotesDatabase.getDatabase(getApplicationContext()).notesDao().getallnotes();
                runOnUiThread(() -> {
                    if (requestcode == REQUEST_CODE_SHOW_NOTES) {
                        notesList.addAll(list);
                        notesAdapter.notifyDataSetChanged();
                    } else if (requestcode == REQUEST_CODE_ADD_NOTE) {
                        notesList.add(0, list.get(0));
                        notesAdapter.notifyItemInserted(0);
                        notesRecyclerView.smoothScrollToPosition(0);
                    } else if (requestcode == REQUEST_CODE_UPDATE_NOTE) {
                        notesList.remove(noteClickedPosition);
                        if (isNoteDeleted) {
                            notesAdapter.notifyItemRemoved(noteClickedPosition);
                        } else {
                            notesList.add(noteClickedPosition, list.get(noteClickedPosition));
                            notesAdapter.notifyItemChanged(noteClickedPosition);
                        }
                    }
                    if (list.isEmpty()) {
                        noNoteImage.setVisibility(View.VISIBLE);
                        noNoteText.setVisibility(View.VISIBLE);
                    } else {
                        noNoteImage.setVisibility(View.GONE);
                        noNoteText.setVisibility(View.GONE);
                    }
                    notesRecyclerView.smoothScrollToPosition(0);
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!executorService.isTerminated()) {
            executorService.shutdown();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri data2;
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == -1) {
            getAllNotes(1, false);
        } else if (requestCode == 2 && resultCode == -1) {
            if (data != null) {
                getAllNotes(2, data.getBooleanExtra("isNoteDeleted", false));
            }
        } else if (requestCode == 4 && resultCode == -1 && data != null && (data2 = data.getData()) != null) {
            try {
                String pathFromUri = getPathFromUri(data2);
                Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
                intent.putExtra("isFromQuickActions", true);
                intent.putExtra("quickActionType", "image");
                intent.putExtra("imagePath", pathFromUri);
                startActivityForResult(intent, 1);
            } catch (Exception e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View inflate = LayoutInflater.from(this).inflate(R.layout.layout_add_url, (ViewGroup) findViewById(R.id.layoutAddUrlContainer));
            builder.setView(inflate);
            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            final EditText editText = (EditText) inflate.findViewById(R.id.inputURL);
            editText.requestFocus();
            inflate.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (editText.getText().toString().trim().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(editText.getText().toString()).matches()) {
                        Toast.makeText(MainActivity.this, "Enter a Valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getApplicationContext(), CreateNoteActivity.class);
                        intent.putExtra("isFromQuickActions", true);
                        intent.putExtra("quickActionType", "URL");
                        intent.putExtra("URL", editText.getText().toString().trim());
                        startActivityForResult(intent, 1);
                        dialogAddURL.dismiss();
                    }
                }
            });
            inflate.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogAddURL.dismiss();
                }
            });
        }
        dialogAddURL.show();
    }
}
