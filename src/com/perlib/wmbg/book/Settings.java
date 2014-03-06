package com.perlib.wmbg.book;

import com.perlib.wmbg.R;
import com.google.gson.annotations.Expose;

import android.content.Context;

public class Settings {
	@Expose private String emailMessage;
	@Expose private boolean confirmDelete;
	@Expose private int swipeMode;
	Context cx;
	public static final int MODE_DELETE_ITEM = 0;
	public static final int MODE_RETURN_ITEM = 1;

	public Settings(String emailMessage,boolean confirmDelete,int swipeMode, Context cx) {
		super();
		this.emailMessage = emailMessage;
		this.confirmDelete = confirmDelete;
		this.swipeMode = swipeMode;
		this.cx = cx;
	}

	public Settings(Context cx) {
		super();
		this.emailMessage = cx.getString(R.string.emailBodyA) + "@book@" + cx.getString(R.string.emailBodyB);
		this.confirmDelete = true;
		this.swipeMode = 1;
		this.cx = cx;
	}

	public String getEmailMessage() {
		return emailMessage;
	}

	public void setEmailMessage(String emailMessage) {
		this.emailMessage = emailMessage;
	}

	public boolean isConfirmDelete() {
		return confirmDelete;
	}

	public void setConfirmDelete(boolean confirmDelete) {
		this.confirmDelete = confirmDelete;
	}

	public int getSwipeMode() {
		return swipeMode;
	}

	public void setSwipeMode(int swipeMode) {
		this.swipeMode = swipeMode;
	}
}
