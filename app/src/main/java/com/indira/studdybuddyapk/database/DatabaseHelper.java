package com.indira.studdybuddyapk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.indira.studdybuddyapk.models.MatakuliahModel;
import com.indira.studdybuddyapk.models.TugasModel;
import com.indira.studdybuddyapk.models.ChecklistItemModel;
import com.indira.studdybuddyapk.models.UserModel;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "studdybuddy.db";
    private static final int DATABASE_VERSION = 8; // Incremented version

    // Common column
    private static final String COLUMN_USER_ID_FK = "user_id";

    // Tabel Tugas
    private static final String TABLE_TASKS = "tasks";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAMA_TUGAS = "nama_tugas";
    private static final String COLUMN_DESKRIPSI = "deskripsi";
    private static final String COLUMN_DEADLINE = "deadline";
    private static final String COLUMN_PRIORITAS = "prioritas";
    private static final String COLUMN_COMPLETED = "completed";
    private static final String COLUMN_PROGRESS = "progress";
    private static final String COLUMN_MATAKULIAH_ID = "matakuliah_id";

    // Tabel Matakuliah
    private static final String TABLE_MATAKULIAH = "matakuliah";
    private static final String COLUMN_KODE_MK = "kode_mk";
    private static final String COLUMN_NAMA_MK = "nama_mk";
    private static final String COLUMN_JADWAL = "jadwal";
    private static final String COLUMN_DOSEN = "dosen";
    private static final String COLUMN_RUANGAN = "ruangan";
    private static final String COLUMN_JAM_MULAI = "jam_mulai";
    private static final String COLUMN_JAM_SELESAI = "jam_selesai";

    // Tabel Checklist
    private static final String TABLE_CHECKLIST = "checklist_items";
    private static final String COLUMN_CHECKLIST_ID = "checklist_id";
    private static final String COLUMN_TASK_ID = "task_id";
    private static final String COLUMN_ITEM_TEXT = "item_text";
    private static final String COLUMN_ITEM_DONE = "is_done";

    // Tabel Users
    private static final String TABLE_USERS = "users";
    private static final String COLUMN_USER_ID = "id";
    private static final String COLUMN_USER_EMAIL = "email";
    private static final String COLUMN_USER_PASSWORD = "password";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createUsersTable = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_EMAIL + " TEXT UNIQUE, " +
                COLUMN_USER_PASSWORD + " TEXT)";
        db.execSQL(createUsersTable);

        String createTasksTable = "CREATE TABLE " + TABLE_TASKS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                COLUMN_NAMA_TUGAS + " TEXT, " +
                COLUMN_DESKRIPSI + " TEXT, " +
                COLUMN_DEADLINE + " TEXT, " +
                COLUMN_PRIORITAS + " TEXT, " +
                COLUMN_COMPLETED + " INTEGER DEFAULT 0, " +
                COLUMN_PROGRESS + " INTEGER DEFAULT 0, " +
                COLUMN_MATAKULIAH_ID + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createTasksTable);

        String createMatakuliahTable = "CREATE TABLE " + TABLE_MATAKULIAH + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USER_ID_FK + " INTEGER, " +
                COLUMN_KODE_MK + " TEXT, " +
                COLUMN_NAMA_MK + " TEXT, " +
                COLUMN_JADWAL + " TEXT, " +
                COLUMN_DOSEN + " TEXT, " +
                COLUMN_RUANGAN + " TEXT, " +
                COLUMN_JAM_MULAI + " TEXT, " +
                COLUMN_JAM_SELESAI + " TEXT, " +
                "FOREIGN KEY(" + COLUMN_USER_ID_FK + ") REFERENCES " + TABLE_USERS + "(" + COLUMN_USER_ID + "))";
        db.execSQL(createMatakuliahTable);

        String createChecklistTable = "CREATE TABLE " + TABLE_CHECKLIST + " (" +
                COLUMN_CHECKLIST_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_TASK_ID + " INTEGER, " +
                COLUMN_ITEM_TEXT + " TEXT, " +
                COLUMN_ITEM_DONE + " INTEGER DEFAULT 0, " +
                "FOREIGN KEY(" + COLUMN_TASK_ID + ") REFERENCES " + TABLE_TASKS + "(" + COLUMN_ID + ") ON DELETE CASCADE)";
        db.execSQL(createChecklistTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 8) {
            // Simplest way for local dev is drop and recreate, 
            // but for real apps we should add columns.
            // Let's use the safer add column approach for tasks and matakuliah
            db.execSQL("ALTER TABLE " + TABLE_TASKS + " ADD COLUMN " + COLUMN_USER_ID_FK + " INTEGER DEFAULT 0");
            db.execSQL("ALTER TABLE " + TABLE_MATAKULIAH + " ADD COLUMN " + COLUMN_USER_ID_FK + " INTEGER DEFAULT 0");
        }
    }

    // User operations
    public long registerUser(UserModel user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_EMAIL, user.getEmail());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());
        long id = db.insert(TABLE_USERS, null, values);
        db.close();
        return id;
    }

    public boolean checkUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID}, COLUMN_USER_EMAIL + " = ?", new String[]{email}, null, null, null);
        int count = cursor.getCount();
        cursor.close();
        db.close();
        return count > 0;
    }

    public int authenticateUser(String email, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, new String[]{COLUMN_USER_ID}, 
                COLUMN_USER_EMAIL + " = ? AND " + COLUMN_USER_PASSWORD + " = ?", 
                new String[]{email, password}, null, null, null);
        
        int userId = -1;
        if (cursor != null && cursor.moveToFirst()) {
            userId = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID));
            cursor.close();
        }
        db.close();
        return userId;
    }

    // Task operations
    public long addTask(TugasModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_ID_FK, task.getUserId());
        values.put(COLUMN_NAMA_TUGAS, task.getNamaTugas());
        values.put(COLUMN_DESKRIPSI, task.getDeskripsi());
        values.put(COLUMN_DEADLINE, task.getDeadline());
        values.put(COLUMN_PRIORITAS, task.getPrioritas());
        values.put(COLUMN_COMPLETED, task.isCompleted() ? 1 : 0);
        values.put(COLUMN_PROGRESS, task.getProgress());
        long id = db.insert(TABLE_TASKS, null, values);
        db.close();
        return id;
    }

    public List<TugasModel> getAllTasks(int userId) {
        List<TugasModel> tasks = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_TASKS, null, COLUMN_USER_ID_FK + " = ?", new String[]{String.valueOf(userId)}, null, null, COLUMN_DEADLINE + " ASC");
        if (cursor.moveToFirst()) {
            do {
                TugasModel task = new TugasModel();
                task.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                task.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID_FK)));
                task.setNamaTugas(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_TUGAS)));
                task.setDeskripsi(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESKRIPSI)));
                task.setDeadline(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DEADLINE)));
                task.setPrioritas(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PRIORITAS)));
                task.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_COMPLETED)) == 1);
                task.setProgress(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS)));
                tasks.add(task);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return tasks;
    }

    // Matakuliah operations
    public long addMatakuliah(MatakuliahModel model) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues v = new ContentValues();
        v.put(COLUMN_USER_ID_FK, model.getUserId());
        v.put(COLUMN_KODE_MK, model.getKodeMk());
        v.put(COLUMN_NAMA_MK, model.getNamaMk());
        v.put(COLUMN_JADWAL, model.getJadwal());
        v.put(COLUMN_DOSEN, model.getDosen());
        v.put(COLUMN_RUANGAN, model.getRuangan());
        v.put(COLUMN_JAM_MULAI, model.getJamMulai());
        v.put(COLUMN_JAM_SELESAI, model.getJamSelesai());
        long id = db.insert(TABLE_MATAKULIAH, null, v);
        db.close();
        return id;
    }

    public List<MatakuliahModel> getAllMatakuliah(int userId) {
        List<MatakuliahModel> list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_MATAKULIAH, null, COLUMN_USER_ID_FK + " = ?", new String[]{String.valueOf(userId)}, null, null, COLUMN_JAM_MULAI + " ASC");
        if (cursor.moveToFirst()) {
            do {
                MatakuliahModel model = new MatakuliahModel();
                model.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                model.setUserId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_USER_ID_FK)));
                model.setKodeMk(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_KODE_MK)));
                model.setNamaMk(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAMA_MK)));
                model.setJadwal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JADWAL)));
                model.setDosen(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DOSEN)));
                model.setRuangan(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RUANGAN)));
                model.setJamMulai(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JAM_MULAI)));
                model.setJamSelesai(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JAM_SELESAI)));
                list.add(model);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return list;
    }

    // Standard update/delete methods remain mostly same but could be filtered by userId for extra safety
    public void updateTask(TugasModel task) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAMA_TUGAS, task.getNamaTugas());
        values.put(COLUMN_DESKRIPSI, task.getDeskripsi());
        values.put(COLUMN_DEADLINE, task.getDeadline());
        values.put(COLUMN_PRIORITAS, task.getPrioritas());
        db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(task.getId())});
        db.close();
    }

    public void deleteTask(int taskId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_TASKS, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.delete(TABLE_CHECKLIST, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    public void updateTaskProgress(int taskId, int progress) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_PROGRESS, progress);
        values.put(COLUMN_COMPLETED, progress == 100 ? 1 : 0);
        db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    public void updateTaskStatus(int taskId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_COMPLETED, isCompleted ? 1 : 0);
        db.update(TABLE_TASKS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});
        db.close();
    }

    public void updateTaskStatusCompletely(int taskId, boolean isCompleted) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues taskValues = new ContentValues();
            taskValues.put(COLUMN_COMPLETED, isCompleted ? 1 : 0);
            taskValues.put(COLUMN_PROGRESS, isCompleted ? 100 : 0);
            db.update(TABLE_TASKS, taskValues, COLUMN_ID + " = ?", new String[]{String.valueOf(taskId)});

            ContentValues checklistValues = new ContentValues();
            checklistValues.put(COLUMN_ITEM_DONE, isCompleted ? 1 : 0);
            db.update(TABLE_CHECKLIST, checklistValues, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)});

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void deleteMatakuliahByDay(String day, int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MATAKULIAH, COLUMN_JADWAL + " = ? AND " + COLUMN_USER_ID_FK + " = ?", new String[]{day, String.valueOf(userId)});
        db.close();
    }

    // Checklist remains task-dependent, so it's implicitly user-dependent
    public void addChecklistItem(int taskId, String itemText) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TASK_ID, taskId);
        values.put(COLUMN_ITEM_TEXT, itemText);
        values.put(COLUMN_ITEM_DONE, 0);
        db.insert(TABLE_CHECKLIST, null, values);
        db.close();
    }

    public List<ChecklistItemModel> getChecklistItems(int taskId) {
        List<ChecklistItemModel> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CHECKLIST, null, COLUMN_TASK_ID + " = ?", new String[]{String.valueOf(taskId)}, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                items.add(new ChecklistItemModel(
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_CHECKLIST_ID)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_TASK_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ITEM_TEXT)),
                        cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ITEM_DONE)) == 1
                ));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return items;
    }

    public void updateChecklistItemStatus(int checklistId, boolean isDone) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ITEM_DONE, isDone ? 1 : 0);
        db.update(TABLE_CHECKLIST, values, COLUMN_CHECKLIST_ID + " = ?", new String[]{String.valueOf(checklistId)});
        db.close();
    }
}
