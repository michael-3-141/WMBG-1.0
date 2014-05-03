package com.perlib.wmbg.interfaces;

import com.perlib.wmbg.book.Book;

/**
 * The Interface OnDownloadComplete.
 */
public interface OnDownloadComplete {
	
	/**
	 * On task finished.
	 *
	 * @param result the result
	 */
	void OnTaskFinished(Book result);
}
