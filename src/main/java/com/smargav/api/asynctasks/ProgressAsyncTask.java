package com.smargav.api.asynctasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

/**
 *
 * @author "Amit S"
 *
 * @param <V>
 *            - Variables
 * @param <R>
 *            - Result
 */
public abstract class ProgressAsyncTask<V, R> extends AsyncTask<V, String, R> {
	public static final int SUCCESS = 0x100;
	public static final int FAILED = 0x101;
	public static final int EXCEPTION = 0x102;
	public static final int NO_NETWORK = 0x103;
	public static final int NO_RESULT = 0x104;
	public Context ctx;
	public ProgressDialog dialog;

	private boolean initDialog = true;

	public ProgressAsyncTask(Context ctx) {
		this.ctx = ctx;
	}

	@Override
	public void onPreExecute() {
		if (initDialog) {
			dialog = new ProgressDialog(ctx);
			dialog.setIndeterminate(true);
			dialog.setMessage("Processing...");
			dialog.setCancelable(false);
			dialog.show();
		}
	}

	public void onProgressUpdate(String... progress) {
		if (progress != null && progress.length > 0 && dialog != null) {
			dialog.setMessage(progress[0]);
		}
		super.onProgressUpdate(progress);
	}

	@Override
	public void onPostExecute(R result) {
		if (dialog != null) {
			dialog.dismiss();
		}
		super.onPostExecute(result);
	}

	public boolean isInitDialog() {
		return initDialog;
	}

	public void setInitDialog(boolean initDialog) {
		this.initDialog = initDialog;
	}

	public void publishProgress(String status){
		super.publishProgress(status);
	}
}
