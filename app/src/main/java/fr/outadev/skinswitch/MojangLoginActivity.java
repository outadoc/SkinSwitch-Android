/*
 * SkinSwitch - MojangLoginActivity
 * Copyright (C) 2014-2015  Baptiste Candellier
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package fr.outadev.skinswitch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.dd.processbutton.iml.ActionProcessButton.Mode;
import com.github.kevinsawicki.http.HttpRequest;

import fr.outadev.skinswitch.network.ChallengeRequirementException;
import fr.outadev.skinswitch.network.InvalidMojangChallengeAnswerException;
import fr.outadev.skinswitch.network.InvalidMojangCredentialsException;
import fr.outadev.skinswitch.network.LoginChallenge;
import fr.outadev.skinswitch.network.MojangConnectionHandler;
import fr.outadev.skinswitch.network.User;
import fr.outadev.skinswitch.network.UsersManager;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 *
 * @author outadoc
 */
public class MojangLoginActivity extends Activity {

	private static final int BUTTON_STATUS_DELAY = 1000;

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private AsyncTask<Void, Void, Exception> mAuthTask = null;
	private Step step;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mChallengeAnswerView;
	private View mLoginFormView;
	private View mChallengeFormView;
	private TextView mChallengeQuestionView;
	private ActionProcessButton mLoginButton;
	private ActionProcessButton mChallengeButton;

	private UsersManager usersManager;
	private User user;
	private LoginChallenge challenge;
	private MojangConnectionHandler loginManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mojang_login);

		if(Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
			getWindow().setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		}

		usersManager = new UsersManager(this);
		user = usersManager.getUser();
		challenge = null;
		loginManager = new MojangConnectionHandler(this);

		//get the initial step
		step = (Step) getIntent().getSerializableExtra("step");

		if(step == null) {
			//set it to LOGIN by default
			step = Step.LOGIN;
		}

		// Set up the login form.
		//email field
		mEmail = user.getUsername();
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		//password field
		mPassword = "";
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText(mPassword);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if(id == R.id.login || id == EditorInfo.IME_NULL) {
					saveCredentials();
					attemptLogin();
					return true;
				}

				return false;
			}

		});

		mLoginFormView = findViewById(R.id.login_form);

		//login button
		mLoginButton = (ActionProcessButton) findViewById(R.id.sign_in_button);
		mLoginButton.setMode(Mode.ENDLESS);
		mLoginButton.setProgress(0);

		mLoginButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				saveCredentials();
				attemptLogin();
			}

		});

		//allow the textviews to be clickable
		((TextView) findViewById(R.id.lbl_forgot_passwd)).setMovementMethod(LinkMovementMethod.getInstance());
		((TextView) findViewById(R.id.lbl_challenge_help)).setMovementMethod(LinkMovementMethod.getInstance());

		//challenge views
		mChallengeFormView = findViewById(R.id.challenge_form);
		mChallengeQuestionView = (TextView) findViewById(R.id.lbl_challenge_question);
		mChallengeAnswerView = (EditText) findViewById(R.id.txt_challenge_answer);

		//challenge button
		mChallengeButton = (ActionProcessButton) findViewById(R.id.b_submit_challenge);
		mChallengeButton.setMode(Mode.ENDLESS);
		mChallengeButton.setProgress(0);

		mChallengeButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptSubmitChallenge();
			}
		});

		//switch to challenge view initially if we need to
		if(step == Step.CHALLENGE) {
			mPassword = user.getPassword();
			mPasswordView.setText(mPassword);
			attemptLogin();
		}

		if(!mEmailView.getText().toString().isEmpty()
				&& mPasswordView.getText().toString().isEmpty()) {
			mPasswordView.requestFocus();
		}
	}

	@Override
	public void onRestoreInstanceState(@NonNull Bundle bundle) {
		super.onRestoreInstanceState(bundle);

		user = new User(bundle.getString("user:username"), bundle.getString("user:password"));

		mEmail = user.getUsername();
		mEmailView.setText(mEmail);

		mPassword = user.getPassword();
		mPasswordView.setText(mPassword);

		challenge = new LoginChallenge(bundle.getString("challenge:id"), bundle.getString("challenge:question"),
				bundle.getString("challenge:auth"));
		mChallengeQuestionView.setText(challenge.getQuestion());
		mChallengeAnswerView.setText(bundle.getString("challenge:answer"));

		showProgress((Step) bundle.getSerializable("step"));
	}

	@Override
	public void onSaveInstanceState(@NonNull Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		if(user != null) {
			savedInstanceState.putString("user:username", user.getUsername());
			savedInstanceState.putString("user:password", user.getPassword());
		}

		if(challenge != null) {
			savedInstanceState.putString("challenge:id", challenge.getId());
			savedInstanceState.putString("challenge:question", challenge.getQuestion());
			savedInstanceState.putString("challenge:auth", challenge.getAuthToken());
			savedInstanceState.putString("challenge:answer", mChallengeAnswerView.getText().toString());
		}

		savedInstanceState.putSerializable("step", step);

	}

	/**
	 * Attempts to sign in or register the account specified by the login form.
	 * If there are form errors (invalid email, missing fields, etc.), the
	 * errors are presented and no actual login attempt is made.
	 */
	public void attemptLogin() {
		if(mAuthTask != null) {
			return;
		}

		// Reset errors.
		mEmailView.setError(null);
		mPasswordView.setError(null);

		// Store values at the time of the login attempt.
		mEmail = mEmailView.getText().toString();
		mPassword = mPasswordView.getText().toString();

		boolean cancel = false;
		View focusView = null;

		// Check for a valid password.
		if(TextUtils.isEmpty(mPassword)) {
			mPasswordView.setError(getString(R.string.error_field_required));
			focusView = mPasswordView;
			cancel = true;
		}

		// Check for a valid email address.
		if(TextUtils.isEmpty(mEmail)) {
			mEmailView.setError(getString(R.string.error_field_required));
			focusView = mEmailView;
			cancel = true;
		}

		if(cancel) {
			// There was an error; don't attempt login and focus the first
			// form field with an error.
			focusView.requestFocus();
		} else {
			saveCredentials();
			mLoginButton.setProgress(1);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	public void attemptSubmitChallenge() {
		if(mAuthTask != null || challenge == null) {
			return;
		}

		saveCredentials();
		mChallengeButton.setProgress(1);
		mAuthTask = new SumbitChallengeTask();
		mAuthTask.execute((Void) null);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	private void showProgress(final Step step) {
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
		this.step = step;

		mLoginButton.setProgress(0);
		mChallengeButton.setProgress(0);

		mLoginFormView.animate().setDuration(shortAnimTime).alpha(step == Step.LOGIN ? 1 : 0)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						mLoginFormView.setVisibility(step == Step.LOGIN ? View.VISIBLE : View.GONE);
					}
				});

		mChallengeFormView.animate().setDuration(shortAnimTime).alpha(step == Step.CHALLENGE ? 1 : 0)
				.setListener(new AnimatorListenerAdapter() {
					@Override
					public void onAnimationEnd(Animator animation) {
						mChallengeFormView.setVisibility(step == Step.CHALLENGE ? View.VISIBLE : View.GONE);
					}
				});

		switch(step) {
			case LOGIN:
				setTitle(R.string.title_activity_mojang_login);
				mEmailView.requestFocus();
				break;
			case CHALLENGE:
				setTitle(R.string.title_activity_mojang_challenge);
				mChallengeAnswerView.requestFocus();
				break;
		}

	}

	private void saveCredentials() {
		user.setUsername(mEmail);
		user.setPassword(mPassword);
		usersManager.saveUserCredentials(user);
	}

	public enum Step {
		LOGIN, CHALLENGE
	}

	/**
	 * Represents an asynchronous login/registration task used to authenticate
	 * the user.
	 */
	public class UserLoginTask extends AsyncTask<Void, Void, Exception> {
		@Override
		protected Exception doInBackground(Void... params) {
			try {
				loginManager.loginWithCredentials(user);
			} catch(HttpRequest.HttpRequestException e) {
				MojangLoginActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MojangLoginActivity.this, getResources().getString(R.string.error_connection_login),
								Toast.LENGTH_LONG).show();
					}

				});

				cancel(true);
			} catch(Exception e) {
				return e;
			}

			return null;
		}

		@Override
		protected void onPostExecute(final Exception ex) {
			mAuthTask = null;

			if(ex == null) {
				// no problem, save the credentials and close
				usersManager.setLoggedInSuccessfully(true);
				mLoginButton.setProgress(100);

				new android.os.Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						finish();
					}
				}, BUTTON_STATUS_DELAY);
			} else if(ex instanceof InvalidMojangCredentialsException) {
				// wrong username/password, try again
				mLoginButton.setProgress(-1);
				showProgress(Step.LOGIN);
				mPasswordView.setError(getString(R.string.error_incorrect_password));
			} else if(ex instanceof ChallengeRequirementException) {
				// challenge required
				mChallengeAnswerView.setText("");
				showProgress(Step.CHALLENGE);

				challenge = ((ChallengeRequirementException) ex).getChallenge();
				mChallengeQuestionView.setText(((ChallengeRequirementException) ex).getChallenge().getQuestion());
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(Step.LOGIN);
		}
	}

	public class SumbitChallengeTask extends AsyncTask<Void, Void, Exception> {
		@Override
		protected Exception doInBackground(Void... params) {
			try {
				loginManager.validateChallenge(challenge, mChallengeAnswerView.getText().toString());
			} catch(InvalidMojangChallengeAnswerException e) {
				return e;
			} catch(HttpRequest.HttpRequestException e) {
				MojangLoginActivity.this.runOnUiThread(new Runnable() {

					@Override
					public void run() {
						Toast.makeText(MojangLoginActivity.this, getResources().getString(R.string.error_connection_challenge),
								Toast.LENGTH_LONG).show();
					}

				});

				cancel(true);
			}

			return null;
		}

		@Override
		protected void onPostExecute(final Exception ex) {
			mAuthTask = null;

			// if everything went as expected, display a success message on the
			// button, and close the activity a bit later
			if(ex == null) {
				mChallengeButton.setProgress(100);
				usersManager.setLoggedInSuccessfully(true);
				saveCredentials();

				new android.os.Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						finish();
					}
				}, BUTTON_STATUS_DELAY);

			} else {
				// if there was a problem, display it in a toast, put the button
				// in fail mode and show the login form a bit later
				Toast.makeText(MojangLoginActivity.this, ex.getMessage(), Toast.LENGTH_LONG).show();
				mChallengeButton.setProgress(-1);

				new android.os.Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						showProgress(Step.LOGIN);
					}
				}, BUTTON_STATUS_DELAY);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(Step.CHALLENGE);
		}
	}
}
