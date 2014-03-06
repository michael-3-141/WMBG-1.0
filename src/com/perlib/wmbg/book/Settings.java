package com.perlib.wmbg.book;

import com.perlib.wmbg.R;
import com.google.gson.annotations.Expose;

import android.content.Context;

public class Settings {
	@Expose private String emailMessage;
	@Expose private boolean confirmDelete;
	Context cx;

	public Settings(String emailMessage,boolean confirmDelete, Context cx) {
		super();
		this.emailMessage = emailMessage;
		this.confirmDelete = confirmDelete;
		this.cx = cx;
	}

	public Settings(Context cx) {
		super();
		this.emailMessage = cx.getString(R.string.emailBodyA) + "@book@" + cx.getString(R.string.emailBodyB);
		this.confirmDelete = true;
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
}
