package dltone.com.mealhero;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import android.widget.Toast;
import org.jasypt.util.password.BasicPasswordEncryptor;

import java.util.ArrayList;


public class AddVolunteerActivity extends AppCompatActivity
{

    private AutoCompleteTextView nameBox;
    private AutoCompleteTextView emailBox;
    private EditText passwordBox;
    private View mProgressView;
    private View addVolunteerView;
    private Volunteer mVolunteer;
    private static final int MENU_ADMIN = Menu.FIRST;
    private static final int MENU_SETTINGS = Menu.FIRST + 1;
    private static final int MENU_LOGOUT = Menu.FIRST + 2;
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
        mProgressView = findViewById(R.id.volunteer_progress);
        addVolunteerView = findViewById(R.id.volunteer_form);
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

        passwordBox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.add_user || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_add_volunteer, menu);
        return true;
    }

    /**
     * Gets called every time the user presses the menu button.
     * Use if your menu is dynamic.
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        menu.clear();
        //if (mVolunteerToDisplay.getPermission().equalsIgnoreCase("Admin"))
        menu.add(0, MENU_ADMIN, Menu.NONE, R.string.action_admin);

        menu.add(0, MENU_SETTINGS, Menu.NONE, R.string.action_settings);
        menu.add(0, MENU_LOGOUT, Menu.NONE, R.string.action_logout);

        getMenuInflater().inflate(R.menu.menu_add_volunteer, menu);

        return super.onPrepareOptionsMenu(menu);

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

    public void attemptLogin()
    {
        if (mAuthTask != null)
        {
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
        if (TextUtils.isEmpty(email))
        {
            emailBox.setError(getString(R.string.error_field_required));
            focusView = emailBox;
            cancel = true;
        } else if (!isEmailValid(email))
        {
            emailBox.setError(getString(R.string.error_invalid_email));
            focusView = emailBox;
            cancel = true;
        }
        if (cancel)
        {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }
        else
        {
            //Check for existing volunteer.
            for(int i = 0; i < MHA.getVolunteerList().size(); i++) {
                if(MHA.getVolunteerList().get(i).getEmail().equals(emailBox.getText().toString().trim())) {
                    emailBox.setError("Email address is taken.");
                    emailBox.requestFocus();
                    return;
                }
            }

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(name, email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email)
    {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    private boolean isPasswordValid(String password)
    {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show)
    {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2)
        {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_longAnimTime);
            int longAnimationTime = 1000; //need longer
            addVolunteerView.setVisibility(show ? View.GONE : View.VISIBLE);
            addVolunteerView.animate().setDuration(longAnimationTime).alpha(show ? 0 : 1).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation) {
                    addVolunteerView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(longAnimationTime).alpha(show ? 1 : 0).setListener(new AnimatorListenerAdapter()
            {
                @Override
                public void onAnimationEnd(Animator animation)
                {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        }
        else
        {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            addVolunteerView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
        protected void onPreExecute()
        {

        }

        @Override
        protected Boolean doInBackground(Void... params)
        {
            BasicPasswordEncryptor bpe = new BasicPasswordEncryptor();

            // TODO: register the new account here.

            String encryptedPassword = bpe.encryptPassword(mPassword);

            Volunteer volunteerToBeAdded = new Volunteer(mName, encryptedPassword, mEmail, "Volunteer", new ArrayList<String>());
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
                Intent intent = null;
                Bundle b = null;

                intent = new Intent(AddVolunteerActivity.this, AdministrationActivity.class);
                b = new Bundle();

                b.putSerializable(VOLUNTEER, mVolunteer);
                intent.putExtras(b);

                MHA.setVolunteerList(VolunteerProvider.GetVolunteers()); //Refresh
                Toast.makeText(MHA.getApplicationContext(), "New volunteer was created Successfully!", Toast.LENGTH_LONG).show();
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



