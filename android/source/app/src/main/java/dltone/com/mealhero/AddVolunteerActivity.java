package dltone.com.mealhero;

import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.jasypt.util.password.BasicPasswordEncryptor;

import java.util.List;

public class AddVolunteerActivity extends Activity implements LoaderManager.LoaderCallbacks<Object>
{

    private AutoCompleteTextView nameBox;
    private AutoCompleteTextView emailBox;
    private EditText passwordBox;
    private View mProgressView;
    private View addVolunteerView;
    private Volunteer mVolunteer;

    private UserLoginTask mAuthTask = null;

    public final static String VOLUNTEER = "dltone.com.mealhero.VOLUNTEER";
    MealHeroApplication MHA;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_volunteer);
        nameBox = (AutoCompleteTextView) findViewById(R.id.volunteer_name);
        emailBox = (AutoCompleteTextView) findViewById(R.id.volunteer_email);
        passwordBox = (EditText) findViewById(R.id.volunteer_password);

        populateAutoComplete();
        passwordBox.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent)
            {
                if (id == R.id.add_user || id == EditorInfo.IME_NULL)
                {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.addvolunteer_button);
        mEmailSignInButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                attemptLogin();
            }
        });

        //addVolunteerView = findViewById(R.id.addVolunteer_form);
        MHA = (MealHeroApplication) getApplication();
    }

        public void attemptLogin()
        {
            if (mAuthTask != null) {
                return;
            }

            // Reset errors.
            nameBox.setError(null);
            emailBox.setError(null);
            passwordBox.setError(null);


            // Store values at the time of the login attempt.
            String name = nameBox.getText().toString();
            String email = emailBox.getText().toString();
            String password = passwordBox.getText().toString();

            boolean cancel = false;
            View focusView = null;

            // Check for a valid password, if the user entered one.
            if (!TextUtils.isEmpty(password) && !isPasswordValid(password))
            {
                passwordBox.setError(getString(R.string.error_invalid_password));
                focusView = passwordBox;
                cancel = true;
            }

            // Check for a valid email address.
            if (TextUtils.isEmpty(email)) {
                emailBox.setError(getString(R.string.error_field_required));
                focusView = emailBox;
                cancel = true;
            } else if (!isEmailValid(email)) {
                emailBox.setError(getString(R.string.error_invalid_email));
                focusView = emailBox;
                cancel = true;
            }

            if (cancel)
            {
                // There was an error; don't attempt login and focus the first
                // form field with an error.
                focusView.requestFocus();
            } else
            {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                //showProgress(true);
                mAuthTask = new UserLoginTask(name, email, password);
                mAuthTask.execute((Void) null);
            }
        }

        private boolean isEmailValid(String email) {
            //TODO: Replace this with your own logic
            return email.contains("@");
        }

        private boolean isPasswordValid(String password) {
            //TODO: Replace this with your own logic
            return password.length() > 4;
        }

    private void populateAutoComplete()
    {
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_volunteer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Object> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Object> loader, Object data) {

    }

    @Override
    public void onLoaderReset(Loader<Object> loader) {

    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean>
    {

        private final String mName;
        private final String mEmail;
        private final String mPassword;

        UserLoginTask(String name, String email, String password)
        {
            mName = name;
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            BasicPasswordEncryptor bpe = new BasicPasswordEncryptor();

            // TODO: register the new account here.

            String encryptedPassword = bpe.encryptPassword(mPassword);

            Volunteer volunteerToBeAdded = new Volunteer(mName, "", encryptedPassword, mEmail, "Volunteer", null);
            mVolunteer = volunteerToBeAdded;

            VolunteerProvider.RegisterVolunteer(volunteerToBeAdded);

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success)
        {
            mAuthTask = null;
            //showProgress(false);

            if (success)
            {
                MHA.setClientList(ClientProvider.GetClients());
                Intent intent = null;
                Bundle b = null;

                intent = new Intent(AddVolunteerActivity.this, AdministrationActivity.class);
                b = new Bundle();

                b.putSerializable(VOLUNTEER, mVolunteer);
                intent.putExtras(b);
                startActivity(intent);
                finish();
            }
            else
            {
                passwordBox.setError(getString(R.string.error_incorrect_password));
                passwordBox.requestFocus();
            }
        }

        @Override
        protected void onCancelled()
        {
            mAuthTask = null;
            //showProgress(false);
        }
    }
}



