package com.smargav.api.net;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Email {
	public static void startEmailActivity(Context context, String[] emailTo, String[] emailCC, String subject, String emailText,
			List<String> filePaths) {
		// need to "send multiple" to get more than one attachment
		final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("text/plain");
		if (emailTo != null) {
			emailIntent.putExtra(Intent.EXTRA_EMAIL, emailTo);
		}
		if (emailCC != null) {
			emailIntent.putExtra(Intent.EXTRA_CC, emailCC);
		}
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
		// has to be an ArrayList
		if (filePaths != null) {
			ArrayList<Uri> uris = new ArrayList<Uri>();
			// convert from paths to Android friendly Parcelable Uri's
			for (String file : filePaths) {
				File fileIn = new File(file);
				Uri u = Uri.fromFile(fileIn);
				uris.add(u);
			}
			emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

		}
		context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
	}
	public static Intent getEmailIntent( String[] emailTo, String[] emailCC, String subject, String emailText,
			List<String> attachments) {
		// need to "send multiple" to get more than one attachment
		final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("text/plain");
		if (emailTo != null) {
			emailIntent.putExtra(Intent.EXTRA_EMAIL, emailTo);
		}
		if (emailCC != null) {
			emailIntent.putExtra(Intent.EXTRA_CC, emailCC);
		}
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
		// has to be an ArrayList
		if (attachments != null) {
			ArrayList<Uri> uris = new ArrayList<Uri>();
			// convert from paths to Android friendly Parcelable Uri's
			for (String file : attachments) {
				File fileIn = new File(file);
				Uri u = Uri.fromFile(fileIn);
				uris.add(u);
			}
			emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

		}
		return (Intent.createChooser(emailIntent, "Send mail..."));
	}
	
	public static Intent getEmailIntent(String[] emailTo, String subject, String emailText,
			List<String> attachments) {
		// need to "send multiple" to get more than one attachment
		final Intent emailIntent = new Intent(Intent.ACTION_SEND_MULTIPLE);
		emailIntent.setType("text/plain");
		if (emailTo != null) {
			emailIntent.putExtra(Intent.EXTRA_EMAIL, emailTo);
		}
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, subject);
		emailIntent.putExtra(Intent.EXTRA_TEXT, emailText);
		// has to be an ArrayList
		if (attachments != null) {
			ArrayList<Uri> uris = new ArrayList<Uri>();
			// convert from paths to Android friendly Parcelable Uri's
			for (String file : attachments) {
				File fileIn = new File(file);
				Uri u = Uri.fromFile(fileIn);
				uris.add(u);
			}
			emailIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);

		}
		return (Intent.createChooser(emailIntent, "Send mail..."));
	}
	
	
}
