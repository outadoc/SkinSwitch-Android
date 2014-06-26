package fr.outadev.skinswitch;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import fr.outadev.skinswitch.network.MojangConnectionManager;
import fr.outadev.skinswitch.network.login.ChallengeRequirementException;
import fr.outadev.skinswitch.network.login.InvalidMojangChallengeAnswerException;
import fr.outadev.skinswitch.network.login.InvalidMojangCredentialsException;
import fr.outadev.skinswitch.network.login.LoginChallenge;
import fr.outadev.skinswitch.storage.User;
import fr.outadev.skinswitch.storage.UsersManager;

/**
 * Activity which displays a login screen to the user, offering registration as
 * well.
 */
public class MojangLoginActivity extends Activity {

	/**
	 * Keep track of the login task to ensure we can cancel it if requested.
	 */
	private AsyncTask<Void, Void, Exception> mAuthTask = null;

	private int step;

	// Values for email and password at the time of the login attempt.
	private String mEmail;
	private String mPassword;

	// UI references.
	private EditText mEmailView;
	private EditText mPasswordView;
	private EditText mChallengeAnswerView;

	private View mLoginFormView;
	private View mLoginStatusView;
	private View mChallengeFormView;

	private TextView mLoginStatusMessageView;
	private TextView mChallengeQuestionView;

	private UsersManager usersManager;
	private User user;

	private LoginChallenge challenge;
	private MojangConnectionManager loginManager;

	private static final int STEP_LOGIN = 0;
	private static final int STEP_LOADING = 1;
	private static final int STEP_CHALLENGE = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_mojang_login);

		usersManager = new UsersManager(this);
		user = usersManager.getUser();
		challenge = null;
		loginManager = new MojangConnectionManager();

		// Set up the login form.
		mEmail = user.getUsername();
		mEmailView = (EditText) findViewById(R.id.email);
		mEmailView.setText(mEmail);

		mPassword = user.getPassword();
		mPasswordView = (EditText) findViewById(R.id.password);
		mPasswordView.setText(mPassword);
		mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
				if(id == R.id.login || id == EditorInfo.IME_NULL) {
					attemptLogin();
					return true;
				}
				return false;
			}
		});

		mLoginFormView = findViewById(R.id.login_form);
		mLoginStatusView = findViewById(R.id.login_status);
		mLoginStatusMessageView = (TextView) findViewById(R.id.login_status_message);

		findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptLogin();
			}
		});

		mChallengeFormView = findViewById(R.id.challenge_form);
		mChallengeQuestionView = (TextView) findViewById(R.id.lbl_challenge_question);
		mChallengeAnswerView = (EditText) findViewById(R.id.txt_challenge_answer);

		findViewById(R.id.b_submit_challenge).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				attemptSubmitChallenge();
			}
		});
	}

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
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

		savedInstanceState.putInt("step", step);
	}

	@Override
	public void onRestoreInstanceState(Bundle bundle) {
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

		showProgress(bundle.getInt("step", 0));
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
			// Show a progress spinner, and kick off a background task to
			// perform the user login attempt.
			mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
			showProgress(STEP_LOADING);
			mAuthTask = new UserLoginTask();
			mAuthTask.execute((Void) null);
		}
	}

	public void attemptSubmitChallenge() {
		if(mAuthTask != null || challenge == null) {
			return;
		}

		mLoginStatusMessageView.setText(R.string.login_progress_signing_in);
		showProgress(STEP_LOADING);
		mAuthTask = new SumbitChallengeTask();
		mAuthTask.execute((Void) null);
	}

	/**
	 * Shows the progress UI and hides the login form.
	 */
	private void showProgress(final int step) {
		int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);
		this.step = step;

		mLoginStatusView.animate().setDuration(shortAnimTime).alpha(step == STEP_LOADING ? 1 : 0)
		        .setListener(new AnimatorListenerAdapter() {
			        @Override
			        public void onAnimationEnd(Animator animation) {
				        mLoginStatusView.setVisibility(step == STEP_LOADING ? View.VISIBLE : View.GONE);
			        }
		        });

		mLoginFormView.animate().setDuration(shortAnimTime).alpha(step == STEP_LOGIN ? 1 : 0)
		        .setListener(new AnimatorListenerAdapter() {
			        @Override
			        public void onAnimationEnd(Animator animation) {
				        mLoginFormView.setVisibility(step == STEP_LOGIN ? View.VISIBLE : View.GONE);
			        }
		        });

		mChallengeFormView.animate().setDuration(shortAnimTime).alpha(step == STEP_CHALLENGE ? 1 : 0)
		        .setListener(new AnimatorListenerAdapter() {
			        @Override
			        public void onAnimationEnd(Animator animation) {
				        mChallengeFormView.setVisibility(step == STEP_CHALLENGE ? View.VISIBLE : View.GONE);
			        }
		        });

		switch(step) {
			case STEP_LOGIN:
				setTitle(R.string.title_activity_mojang_login);
				mEmailView.requestFocus();
				break;
			case STEP_CHALLENGE:
				setTitle(R.string.title_activity_mojang_challenge);
				mChallengeAnswerView.requestFocus();
				break;
			case STEP_LOADING:
				setTitle(R.string.title_activity_mojang_loading);
				break;
		}

	}

	private void saveCredentials() {
		user.setUsername(mEmail);
		user.setPassword(mPassword);
		usersManager.saveUserCredentials(user);
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
				saveCredentials();
				finish();
			} else if(ex instanceof InvalidMojangCredentialsException) {
				// wrong username/password, try again
				showProgress(STEP_LOGIN);
				mPasswordView.setError(getString(R.string.error_incorrect_password));
			} else if(ex instanceof ChallengeRequirementException) {
				// challenge required
				mChallengeAnswerView.setText("");
				showProgress(STEP_CHALLENGE);
				saveCredentials();

				challenge = ((ChallengeRequirementException) ex).getChallenge();
				mChallengeQuestionView.setText(((ChallengeRequirementException) ex).getChallenge().getQuestion());
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(STEP_LOGIN);
		}
	}

	public class SumbitChallengeTask extends AsyncTask<Void, Void, Exception> {
		@Override
		protected Exception doInBackground(Void... params) {
			try {
				loginManager.validateChallenge(challenge, mChallengeAnswerView.getText().toString());
			} catch(InvalidMojangChallengeAnswerException e) {
				return e;
			}

			return null;
		}

		@Override
		protected void onPostExecute(final Exception ex) {
			mAuthTask = null;

			if(ex == null) {
				Toast.makeText(MojangLoginActivity.this, "Yay, right answer. ^-^", Toast.LENGTH_LONG).show();
				saveCredentials();
				finish();
			} else {
				Toast.makeText(MojangLoginActivity.this, ((InvalidMojangChallengeAnswerException) ex).getMessage(),
				        Toast.LENGTH_LONG).show();
				showProgress(STEP_LOGIN);
			}
		}

		@Override
		protected void onCancelled() {
			mAuthTask = null;
			showProgress(STEP_CHALLENGE);
		}
	}
}
