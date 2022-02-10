package com.hustlers.backup.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.hustlers.backup.R;
import com.hustlers.backup.models.Record;
import com.hustlers.backup.models.Records;
import com.hustlers.backup.repositories.RecordDatabase;
import com.hustlers.backup.utilities.ActivityResultUtility;
import com.hustlers.backup.utilities.DriveHelperUtility;
import com.hustlers.backup.utilities.ParcelableUtility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {

    private ActivityResultUtility<Intent, ActivityResult> activityLauncher;
    private RecordDatabase recordDatabase;
    private GoogleSignInClient client;
    private DriveHelperUtility driveHelperUtility;
    private Button countRecordsButton;
    private Button addRecordsButton;
    private Button deleteRecordsButton;
    private Button signInButton;
    private Button backupButton;
    private Button restoreButton;
    private Button signOutButton;

    @Override
    protected void onCreate ( Bundle savedInstanceState ) {
        super.onCreate ( savedInstanceState );
        setContentView ( R.layout.activity_main );
        initialize ();
        setListeners ();
    }

    /**
     * Shows a toast message to user
     *
     * @param message toast message to be displayed to user
     */
    private void showToastMessage ( String message ) {
        try {
            Toast.makeText ( this, message, Toast.LENGTH_SHORT ).show ();
        } catch (Exception ignored) {

        }
    }

    /**
     * Initializes views, databases, activity launchers and any other items
     */
    private void initialize () {
        activityLauncher = ActivityResultUtility.registerActivityForResult ( this );
        recordDatabase = Room.databaseBuilder ( this, RecordDatabase.class, "Record" ).allowMainThreadQueries ().build ();
        countRecordsButton = findViewById ( R.id.countRecordsButton );
        addRecordsButton = findViewById ( R.id.addRecordsButton );
        deleteRecordsButton = findViewById ( R.id.deleteRecordsButton );
        signInButton = findViewById ( R.id.signInButton );
        backupButton = findViewById ( R.id.backupButton );
        restoreButton = findViewById ( R.id.restoreButton );
        signOutButton = findViewById ( R.id.signOutButton );
    }

    /**
     * Sets listeners to all the views
     */
    private void setListeners () {
        countRecordsButton.setOnClickListener ( v -> countRecords () );
        addRecordsButton.setOnClickListener ( v -> addRecords () );
        deleteRecordsButton.setOnClickListener ( v -> deleteRecords () );
        signInButton.setOnClickListener ( v -> requestSignIn () );
        backupButton.setOnClickListener ( v -> backupRecords () );
        restoreButton.setOnClickListener ( v -> restoreRecords () );
        signOutButton.setOnClickListener ( v -> requestSignOut () );
    }

    /**
     * Starts a sign-in activity.
     */
    private void requestSignIn () {
        showToastMessage ( "Requesting sign-in" );
        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder ( GoogleSignInOptions.DEFAULT_SIGN_IN )
                        .requestEmail ()
                        .requestScopes ( new Scope ( DriveScopes.DRIVE_APPDATA ) )
                        .build ();
        client = GoogleSignIn.getClient ( this, signInOptions );
        activityLauncher.launch ( client.getSignInIntent (), this::handleSignInResult );
    }

    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
     */
    private void handleSignInResult ( ActivityResult activityResult ) {
        Intent result = activityResult.getData ();
        GoogleSignIn.getSignedInAccountFromIntent ( result )
                .addOnSuccessListener ( googleAccount -> {
                    showToastMessage ( "Signed in as " + googleAccount.getEmail () );
                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2 (
                                    this, Collections.singleton ( DriveScopes.DRIVE_APPDATA ) );
                    credential.setSelectedAccount ( googleAccount.getAccount () );
                    Drive drive = new Drive.Builder (
                            AndroidHttp.newCompatibleTransport (),
                            new GsonFactory (),
                            credential )
                            .setApplicationName ( "Drive Backup Sample Application" )
                            .build ();
                    driveHelperUtility = new DriveHelperUtility ( drive );
                    checkDrive ();
                } )
                .addOnFailureListener ( exception -> showToastMessage ( "Unable to sign in" ) );
    }

    /**
     * Requests for a sign out event
     */
    private void requestSignOut () {
        if (client == null || driveHelperUtility == null) {
            showToastMessage ( "Please try after signing in" );
            return;
        }
        client.signOut ().addOnCompleteListener ( this, task -> showToastMessage ( "Sign out successful" ) );
        driveHelperUtility = null;
        client = null;

    }

    /**
     * Count records from RECORD table
     */
    private void countRecords () {
        List<Record> records = recordDatabase.daoAccess ().retrieveAll ();
        for (Record record : records) {
            Log.d ( "DEBUG", record.toString () );
        }
        int count = recordDatabase.daoAccess ().retrieveCount ();
        showToastMessage ( count + " records present" );
    }

    /**
     * Adds few sample records to RECORD table
     */
    private void addRecords () {
        List<Record> records = new ArrayList<> ();
        int count = 1000;
        for (int i = 0; i < count; i++) {
            String uuid = UUID.randomUUID ().toString ();
            records.add ( new Record ( uuid, "Record-" + uuid ) );
        }
        recordDatabase.daoAccess ().insertAll ( records );
        showToastMessage ( count + " new records added" );
    }

    /**
     * Deletes old records from RECORD table
     */
    private void deleteRecords () {
        recordDatabase.daoAccess ().deleteAll ();
        showToastMessage ( "Old records deleted" );
    }

    /**
     * Creates a backup of records in Google Drive
     */
    private void backupRecords () {
        if (client == null || driveHelperUtility == null) {
            showToastMessage ( "Please try after signing in" );
            return;
        }
        Records records = new Records ( recordDatabase.daoAccess ().retrieveAll () );
        byte[] data = ParcelableUtility.marshall ( records );
        driveHelperUtility.backup ( data ).addOnCompleteListener ( task -> {
            if (task.isSuccessful ()) {
                showToastMessage ( "File uploaded to drive" );
            } else {
                Log.e ( "DRIVE", "EXCEPTION", task.getException () );
                showToastMessage ( "File not uploaded to drive" );
            }
        } );

    }

    /**
     * Checks if any backup file is already present in Google Drive
     */
    private void checkDrive () {
        if (client == null || driveHelperUtility == null) {
            showToastMessage ( "Please try after signing in" );
            return;
        }
        driveHelperUtility.getLatestBackupTimestamp ().addOnCompleteListener ( task -> {
            if (task.isSuccessful () && task.getResult () != null) {
                showToastMessage ( "Existing backup file found" );
            } else if (task.isSuccessful () && task.getResult () == null) {
                showToastMessage ( "No existing backup file found" );
            } else {
                Log.e ( "DRIVE", "EXCEPTION", task.getException () );
                showToastMessage ( "Could not check drive" );
            }
        } );
    }

    /**
     * Restores records from Google Drive
     */
    private void restoreRecords () {
        if (client == null || driveHelperUtility == null) {
            showToastMessage ( "Please try after signing in" );
            return;
        }
        driveHelperUtility.restore ().addOnCompleteListener ( task -> {
            if (task.isSuccessful () && task.getResult () != null) {
                List<Record> records = ParcelableUtility.unMarshall ( task.getResult (), Records.CREATOR ).getRecords ();
                Log.d ( "DRIVE", "Existing backup file found with " + records.size () + " records" );
                Log.d ( "DRIVE", "Old records will be deleted and records from backup file will be loaded" );
                recordDatabase.daoAccess ().deleteAll ();
                Log.d ( "DRIVE", "Old records deleted" );
                recordDatabase.daoAccess ().insertAll ( records );
                Log.d ( "DRIVE", "Records from backup file loaded successfully" );
                showToastMessage ( "Restore complete" );
            } else if (task.isSuccessful () && task.getResult () == null) {
                showToastMessage ( "No existing backup file found" );
            } else {
                Log.e ( "DRIVE", "EXCEPTION", task.getException () );
                showToastMessage ( "Could not restore" );
            }
        } );
    }
}