package com.example.noteit_new.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.noteit_new.Entity.Note;
import com.example.noteit_new.MainActivity;
import com.example.noteit_new.R;
import com.example.noteit_new.database.NotesDatabase;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.makeramen.roundedimageview.RoundedImageView;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CreateNoteActivity extends AppCompatActivity {

    private Note alreadyAvailableNote;
    private AlertDialog dialogAddURL;
    private AlertDialog dialogDeleteNote;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();
    private EditText inputNoteSubtitle;
    private EditText inputNoteText;
    private EditText inputNoteTitle;
    private LinearLayout layoutWebURL;
    private RoundedImageView noteImage;
    private String selectedImagePath;
    private String selectedNoteColor;
    private String subtitle;
    private String text;
    private TextView textDateTime;
    private TextView textWebURL;
    private String title;
    private View viewSubtitleIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        inputNoteTitle = findViewById(R.id.inputNoteTitle);
        inputNoteSubtitle = findViewById(R.id.inputNoteSubtitle);
        inputNoteText = findViewById(R.id.inputNote);
        textDateTime = findViewById(R.id.textDateTime);
        viewSubtitleIndicator = findViewById(R.id.viewSubitileIndicator);
        noteImage = findViewById(R.id.noteImage);
        textWebURL = findViewById(R.id.textWebURL);
        layoutWebURL = findViewById(R.id.layoutWebURL);

        textDateTime.setText(new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss", Locale.getDefault()).format(new Date()));

        ImageView imageBack = findViewById(R.id.imageBack);
        imageBack.setOnClickListener(view -> onBackPressed());

        selectedNoteColor = "#FDBE3B";
        selectedImagePath = null;

        if (getIntent().getBooleanExtra("isViewOrUpdate", false)) {
            alreadyAvailableNote = (Note) getIntent().getSerializableExtra("note");
            setViewOrUpdateNote();
        }

        findViewById(R.id.imageRemoveURL).setOnClickListener(view -> {
            textWebURL.setText(null);
            layoutWebURL.setVisibility(View.GONE);
        });

        findViewById(R.id.imageRemoveNoteImage).setOnClickListener(view -> {
            noteImage.setImageBitmap(null);
            noteImage.setVisibility(View.GONE);
            findViewById(R.id.imageRemoveNoteImage).setVisibility(View.GONE);
            selectedImagePath = null;
        });

        if (getIntent().getBooleanExtra("isFromQuickActions", false)) {
            String stringExtra = getIntent().getStringExtra("quickActionType");
            if (stringExtra != null) {
                if (stringExtra.equals("image")) {
                    String stringExtra2 = getIntent().getStringExtra("imagePath");
                    selectedImagePath = stringExtra2;
                    noteImage.setImageBitmap(BitmapFactory.decodeFile(stringExtra2));
                    noteImage.setVisibility(View.VISIBLE);
                    findViewById(R.id.imageRemoveNoteImage).setVisibility(View.VISIBLE);
                } else if (stringExtra.equals("URL")) {
                    textWebURL.setText(getIntent().getStringExtra("URL"));
                    layoutWebURL.setVisibility(View.VISIBLE);
                }
            }
        }
        initmiscellaneous();
        setSubtitleIndicatorColor();
    }

    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainActivity.class);
        String trim = inputNoteTitle.getText().toString().trim();
        String trim2 = inputNoteText.getText().toString().trim();
        if (TextUtils.isEmpty(trim) && TextUtils.isEmpty(trim2)) {
            startActivity(intent);
            finish();
        } else if (TextUtils.isEmpty(trim)) {
            Toast.makeText(this, "Title cannot be empty", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(trim2)) {
            Toast.makeText(this, "Note cannot be empty", Toast.LENGTH_SHORT).show();
        } else if(!TextUtils.isEmpty(trim) && !TextUtils.isEmpty(trim2)) {
            saveNote();
            startActivity(intent);
            finish();
        }
    }

    private void setViewOrUpdateNote() {
        inputNoteTitle.setText(alreadyAvailableNote.getTitle());
        inputNoteSubtitle.setText(alreadyAvailableNote.getSubtitle());
        inputNoteText.setText(alreadyAvailableNote.getNoteText());
        textDateTime.setText(alreadyAvailableNote.getDateTime());
        selectedNoteColor = alreadyAvailableNote.getColor();
        if (alreadyAvailableNote.getImagePath() != null && !alreadyAvailableNote.getImagePath().trim().isEmpty()) {
            noteImage.setImageBitmap(BitmapFactory.decodeFile(alreadyAvailableNote.getImagePath()));
            findViewById(R.id.imageRemoveNoteImage).setVisibility(View.VISIBLE);
            noteImage.setVisibility(View.VISIBLE);
            selectedImagePath = alreadyAvailableNote.getImagePath();
        }
        if (alreadyAvailableNote.getWebLink() != null && !alreadyAvailableNote.getWebLink().trim().isEmpty()) {
            textWebURL.setText(alreadyAvailableNote.getWebLink());
            layoutWebURL.setVisibility(View.VISIBLE);
            findViewById(R.id.imageRemoveURL).setVisibility(View.VISIBLE);
        }
    }

    private void initmiscellaneous() {
        LinearLayout linearLayout =  findViewById(R.id.layoutMiscellaneous);
        final BottomSheetBehavior<LinearLayout> bottomSheetBehavior = BottomSheetBehavior.from(linearLayout);
        findViewById(R.id.viewArrowMiscellaneout).setOnClickListener(view -> {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                view.setBackgroundResource(R.drawable.arrow_down);
            }else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                view.setBackgroundResource(R.drawable.arrow_up);
            }
        });

        final ImageView imageView =  linearLayout.findViewById(R.id.imageColor1);
        final ImageView imageView2 =  linearLayout.findViewById(R.id.imageColor2);
        final ImageView imageView3 =  linearLayout.findViewById(R.id.imageColor3);
        final ImageView imageView4 =  linearLayout.findViewById(R.id.imageColor4);
        final ImageView imageView5 =  linearLayout.findViewById(R.id.imageColor5);
        linearLayout.findViewById(R.id.viewNoteColor1).setOnClickListener(view -> {
            selectedNoteColor = "#FDBE3B";
            imageView.setImageResource(R.drawable.done);
            imageView2.setImageResource(0);
            imageView3.setImageResource(0);
            imageView4.setImageResource(0);
            imageView5.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        linearLayout.findViewById(R.id.viewNoteColor2).setOnClickListener(view -> {
            selectedNoteColor = "#e9ff70";
            imageView.setImageResource(0);
            imageView2.setImageResource(R.drawable.done);
            imageView3.setImageResource(0);
            imageView4.setImageResource(0);
            imageView5.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        linearLayout.findViewById(R.id.viewNoteColor3).setOnClickListener(view -> {selectedNoteColor = "#ff9770";
            imageView.setImageResource(0);
            imageView2.setImageResource(0);
            imageView3.setImageResource(R.drawable.done);
            imageView4.setImageResource(0);
            imageView5.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        linearLayout.findViewById(R.id.viewNoteColor4).setOnClickListener(view -> {
            selectedNoteColor = "#ff70a6";
            imageView.setImageResource(0);
            imageView2.setImageResource(0);
            imageView3.setImageResource(0);
            imageView4.setImageResource(R.drawable.done);
            imageView5.setImageResource(0);
            setSubtitleIndicatorColor();
        });
        linearLayout.findViewById(R.id.viewNoteColor5).setOnClickListener(view -> {
            selectedNoteColor = "#70d6ff";
            imageView.setImageResource(0);
            imageView2.setImageResource(0);
            imageView3.setImageResource(0);
            imageView4.setImageResource(0);
            imageView5.setImageResource(R.drawable.done);
            setSubtitleIndicatorColor();
        });
        Note note = alreadyAvailableNote;
        if (note != null && note.getColor() != null && !alreadyAvailableNote.getColor().trim().isEmpty()) {
            String color = alreadyAvailableNote.getColor();
            switch (color) {
                case "#70d6ff":
                    linearLayout.findViewById(R.id.viewNoteColor5).performClick();
                    break;
                case "#e9ff70":
                    linearLayout.findViewById(R.id.viewNoteColor2).performClick();
                    break;
                case "#ff70a6":
                    linearLayout.findViewById(R.id.viewNoteColor4).performClick();
                    break;
                case "#ff9770":
                    linearLayout.findViewById(R.id.viewNoteColor3).performClick();
                    break;
            }

        }
        linearLayout.findViewById(R.id.layoutAddImage).setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), "android.permission.READ_MEDIA_IMAGES") != 0) {
                ActivityCompat.requestPermissions(CreateNoteActivity.this, new String[]{"android.permission.READ_MEDIA_IMAGES"}, 5);
            }
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            linearLayout.findViewById(R.id.viewArrowMiscellaneout).setBackgroundResource(R.drawable.arrow_up);
            selectImage();
        });
        linearLayout.findViewById(R.id.layoutAddUrl).setOnClickListener(view -> {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            showAddURLDialog();
        });
        if (alreadyAvailableNote != null) {
            linearLayout.findViewById(R.id.layoutDeleteNote).setVisibility(View.VISIBLE);
            linearLayout.findViewById(R.id.layoutDeleteNote).setOnClickListener(view -> {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                showDeleteNoteDialog();
            });
        }
    }

    private void showDeleteNoteDialog() {
        if (dialogDeleteNote == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View inflate = LayoutInflater.from(this).inflate(R.layout.layout_delete_note,  findViewById(R.id.layoutDeleteNoteContainer));
            builder.setView(inflate);
            AlertDialog create = builder.create();
            dialogDeleteNote = create;
            if (create.getWindow() != null) {
                dialogDeleteNote.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }
            inflate.findViewById(R.id.textDeleteNoteDialog).setOnClickListener(view -> executorService.execute(new Runnable() {
                @Override
                public void run() {
                    NotesDatabase.getDatabase(getApplicationContext()).notesDao().deletenote(alreadyAvailableNote);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            intent.putExtra("isNoteDeleted", true);
                            startActivity(intent);
                            finish();
                            Log.e("CreateNoteActivity", "Note Deleted : " + alreadyAvailableNote.getTitle());
                        }
                    });
                }
            }));
            inflate.findViewById(R.id.textCancel).setOnClickListener(view -> dialogDeleteNote.dismiss());
        }
        dialogDeleteNote.show();
    }

    private void setSubtitleIndicatorColor() {
        ((GradientDrawable) this.viewSubtitleIndicator.getBackground()).setColor(Color.parseColor(this.selectedNoteColor));
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

    private void showAddURLDialog() {
        if (dialogAddURL == null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(CreateNoteActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.layout_add_url, (ViewGroup) findViewById(R.id.layoutAddUrlContainer));
            builder.setView(view);
            dialogAddURL = builder.create();
            if (dialogAddURL.getWindow() != null) {
                dialogAddURL.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            }

            final EditText editText = view.findViewById(R.id.inputURL);
            editText.requestFocus();

            view.findViewById(R.id.textAdd).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (editText.getText().toString().trim().isEmpty()) {
                        Toast.makeText(CreateNoteActivity.this, "Enter URL", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.WEB_URL.matcher(editText.getText().toString()).matches()) {
                        Toast.makeText(CreateNoteActivity.this, "Enter a Valid URL", Toast.LENGTH_SHORT).show();
                    } else {
                        textWebURL.setText(editText.getText().toString());
                        layoutWebURL.setVisibility(View.VISIBLE);
                        dialogAddURL.dismiss();
                    }
                }
            });

            view.findViewById(R.id.textCancel).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogAddURL.dismiss();
                }
            });
        }
        dialogAddURL.show();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 2 || resultCode != RESULT_OK) {
            Log.e("CreateNoteActivity", "Invalid request code or result code");
        } else if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    if (inputStream != null) {
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        if (bitmap != null) {
                            String path = getPathFromUri(uri);
                            this.selectedImagePath = path;
                            this.noteImage.setImageBitmap(BitmapFactory.decodeFile(path));
                            findViewById(R.id.imageRemoveNoteImage).setVisibility(View.VISIBLE);
                            this.noteImage.setVisibility(View.VISIBLE);
                        } else {
                            Log.e("CreateNoteActivity", "Failed to decode bitmap from input stream");
                        }
                    } else {
                        Log.e("CreateNoteActivity", "Input stream is null");
                    }
                } catch (Exception e) {
                    Log.e("CreateNoteActivity", "Exception: " + e.getMessage());
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e("CreateNoteActivity", "Selected image URI is null");
            }
        } else {
            Log.e("CreateNoteActivity", "Intent data is null");
        }
    }

    private void saveNote() {
        title = inputNoteTitle.getText().toString().trim();
        subtitle = inputNoteSubtitle.getText().toString().trim();
        text = inputNoteText.getText().toString().trim();
        String dateTime = textDateTime.getText().toString().trim();

        final Note note = new Note(title, subtitle, text, dateTime);
        note.setColor(selectedNoteColor);
        note.setImagePath(selectedImagePath);
        if (layoutWebURL.getVisibility() == View.VISIBLE) {
            note.setWebLink(textWebURL.getText().toString());
        }
        if (alreadyAvailableNote != null) {
            note.setId(alreadyAvailableNote.getId());
        }
        executorService.execute(() -> {
            try {
                NotesDatabase.getDatabase(getApplicationContext()).notesDao().insertnotes(note);
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CreateNoteActivity.this, "Failed to save note", Toast.LENGTH_SHORT).show();
                        Log.e("CreateNoteActivity", "Failed to save note: " + e.getMessage());
                    }
                });
            }
        });
    }
}