package com.example.taskmanager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taskmanager.adapters.BoxWithTasks;
import com.example.taskmanager.dataBase.AppExecuted;
import com.example.taskmanager.dataBase.MyConstants;
import com.example.taskmanager.dataBase.MyDbManager;
import com.example.taskmanager.taskSchema.TaskFields;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class WriteNewTaskActivity extends AppCompatActivity {
    private final int PICK_IMAGE_CODE = 132;
    private boolean editTaskOrNo = true;
    private String uriPhotoFromUser = "empty";
    private BoxWithTasks boxWithTasks;
    private ImageView imgUserGallery;
    private ConstraintLayout constraintLayoutWithPhoto;
    private ImageButton imgBtnEditPhoto, imgBtnDeletePhoto;
    private FloatingActionButton btnAddUserPhoto;
    private EditText edTxtNameTask, edTxtMainTextTask;
    private Button btnSaveTask;
    private MyDbManager myDbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_task_and_name_task);
        initAllComponents();
        getMyIntent();


        btnSaveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddTaskToDb();
            }
        });
        imgBtnEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickEditImage();
            }
        });
        imgBtnDeletePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickDeleteImage();
            }
        });
        btnAddUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickAddImage();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_CODE && data != null) {
            uriPhotoFromUser = data.getData().toString();
            imgUserGallery.setImageURI(data.getData());
            getContentResolver().takePersistableUriPermission(data.getData(), Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        myDbManager.openDb();
    }

    private void initAllComponents() {
        myDbManager = new MyDbManager(this);
        edTxtNameTask = findViewById(R.id.edTxtNameTask);
        edTxtMainTextTask = findViewById(R.id.edTxtMainTextTask);
        btnSaveTask = findViewById(R.id.btnSaveTask);
        imgBtnDeletePhoto = findViewById(R.id.imgBtnDeletePhoto);
        imgUserGallery = findViewById(R.id.imgUserGallery);
        imgBtnEditPhoto = findViewById(R.id.imgBtnEditPhoto);
        btnAddUserPhoto = findViewById(R.id.btnAddUserPhoto);
        constraintLayoutWithPhoto = findViewById(R.id.constraintLayoutWithPhoto);
    }

    private void getMyIntent() {
        Intent getIntent = getIntent();
        if (getIntent != null) {
            boxWithTasks = (BoxWithTasks) getIntent.getSerializableExtra(MyConstants.BOX_WITH_ITEM_INTENT);
            editTaskOrNo = getIntent.getBooleanExtra(MyConstants.STATE_EDIT_OR_NOT, true);

            if (!editTaskOrNo) {
                edTxtNameTask.setText(boxWithTasks.getNameTask());
                edTxtMainTextTask.setText(boxWithTasks.getMainTextTask());
                if (!boxWithTasks.getUriPhotoFromUser().equals("empty")) {
                    uriPhotoFromUser = boxWithTasks.getUriPhotoFromUser();
                    constraintLayoutWithPhoto.setVisibility(View.VISIBLE);
                    imgUserGallery.setImageURI(Uri.parse(boxWithTasks.getUriPhotoFromUser()));
                    imgBtnDeletePhoto.setVisibility(View.GONE);
                    imgBtnEditPhoto.setVisibility(View.GONE);
                }
            }
        }
    }

    private void onClickAddTaskToDb() {
        String nameTask = edTxtNameTask.getText().toString();
        String mainTextTask = edTxtMainTextTask.getText().toString();
        if (nameTask.equals("") || mainTextTask.equals("")) {
            Toast.makeText(WriteNewTaskActivity.this, R.string.nothing_to_write, Toast.LENGTH_LONG).show();
        } else {

            if (editTaskOrNo) {
                AppExecuted.getInstance().getSecondIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        myDbManager.writeToDb(nameTask, mainTextTask, uriPhotoFromUser);
                    }
                });
                Toast.makeText(WriteNewTaskActivity.this, R.string.task_created, Toast.LENGTH_LONG).show();
            } else {
                myDbManager.updateItemInList(nameTask, mainTextTask, uriPhotoFromUser, boxWithTasks.getId());
                Toast.makeText(WriteNewTaskActivity.this, R.string.task_created, Toast.LENGTH_LONG).show();
            }
            myDbManager.closeDb();
            finish();
        }

    }

    private void onClickDeleteImage() {
        imgUserGallery.setImageResource(R.drawable.ic_launcher_foreground);
        uriPhotoFromUser = "empty";
        constraintLayoutWithPhoto.setVisibility(View.GONE);
        btnAddUserPhoto.setVisibility(View.VISIBLE);
    }

    private void onClickEditImage() {
        Intent openGallery = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        openGallery.setType("image/*");
        startActivityForResult(openGallery, PICK_IMAGE_CODE);
    }

    private void onClickAddImage() {
        constraintLayoutWithPhoto.setVisibility(View.VISIBLE);
        btnAddUserPhoto.setVisibility(View.GONE);
    }
}