package com.indira.studdybuddyapk.models;

public class ChecklistItemModel {
    private int id;
    private int taskId;
    private String itemText;
    private boolean isDone;

    public ChecklistItemModel(int id, int taskId, String itemText, boolean isDone) {
        this.id = id;
        this.taskId = taskId;
        this.itemText = itemText;
        this.isDone = isDone;
    }

    public int getId() { return id; }
    public int getTaskId() { return taskId; }
    public String getItemText() { return itemText; }
    public boolean isDone() { return isDone; }
    public void setDone(boolean done) { isDone = done; }
}
