package com.perlib.wmbg.book;

import com.perlib.wmbg.R;
import com.google.gson.annotations.Expose;

import android.content.Context;

public class Settings {
	@Expose private String emailMessage;
	Context cx;

	public Settings(String emailMessage, Context cx) {
		super();
		this.emailMessage = emailMessage;
		this.cx = cx;
	}

	public Settings(Context cx) {
		super();
		this.emailMessage = cx.getString(R.string.emailBodyA) + "@book@" + cx.getString(R.string.emailBodyB);
		this.cx = cx;
	}

	public String getEmailMessage() {
		return emailMessage;
	}

	public void setEmailMessage(String emailMessage) {
		this.emailMessage = emailMessage;
	}
}
