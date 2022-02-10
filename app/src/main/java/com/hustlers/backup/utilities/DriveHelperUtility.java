package com.hustlers.backup.utilities;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.api.client.http.InputStreamContent;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.File;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * A utility for performing read/write operations on Drive files
 *
 * @author niranj1997
 */
public class DriveHelperUtility {
    private final Executor mExecutor = Executors.newSingleThreadExecutor ();
    private final Drive mDriveService;

    public DriveHelperUtility ( Drive driveService ) {
        mDriveService = driveService;
    }

    /**
     * Uploads backup file to Google Drive.
     *
     * @param data byte array to be backed up
     * @return file ID of the backup file
     */
    public Task<String> backup ( byte[] data ) {
        return Tasks.call ( mExecutor, () -> {
            String fileId = null;
            List<File> fileList = mDriveService.files ().list ().setSpaces ( "appDataFolder" )
                    .setFields ( "files(id)" ).execute ().getFiles ();
            if (fileList != null && fileList.size () > 0) {
                Log.d ( "DRIVE", "Count of files in Drive is " + fileList.size () );
                fileId = fileList.get ( 0 ).getId ();
            }
            if (fileId != null) {
                Log.d ( "DRIVE", "File already present in Google Drive with ID " + fileId );
                File body = new File ().setName ( "eventBackup" ).setMimeType ( "text/plain" );
                File file = mDriveService.files ().update ( fileId, body, new InputStreamContent ( body.getMimeType (),
                        new ByteArrayInputStream ( data ) ) ).setFields ( "id, name, size" ).execute ();
                Log.d ( "DRIVE", "File updated in Google Drive" );
                Log.d ( "DRIVE", "File ID is " + file.getId () );
                Log.d ( "DRIVE", "File name is " + file.getName () );
                Log.d ( "DRIVE", "File size is " + file.getSize () + " bytes" );
                return file.getId ();
            } else {
                Log.d ( "DRIVE", "File not present in Google Drive" );
                File body = new File ().setName ( "eventBackup" ).setMimeType ( "text/plain" );
                body.setParents ( Collections.singletonList ( "appDataFolder" ) );
                File file = mDriveService.files ().create ( body, new InputStreamContent ( body.getMimeType (),
                        new ByteArrayInputStream ( data ) ) ).setFields ( "id, name, size" ).execute ();
                Log.d ( "DRIVE", "File created in Google Drive" );
                Log.d ( "DRIVE", "File ID is " + file.getId () );
                Log.d ( "DRIVE", "File name is " + file.getName () );
                Log.d ( "DRIVE", "File size is " + file.getSize () + " bytes" );
                return file.getId ();
            }
        } );
    }

    /**
     * Checks if any backup file is already present in Google Drive
     *
     * @return modified timestamp of latest backup file
     */
    public Task<String> getLatestBackupTimestamp () {
        return Tasks.call ( mExecutor, () -> {
            String timestamp = null;
            List<File> fileList = mDriveService.files ().list ().setSpaces ( "appDataFolder" )
                    .setFields ( "files(modifiedTime)" ).execute ().getFiles ();
            if (fileList != null && fileList.size () > 0) {
                Log.d ( "DRIVE", "Count of files in Drive is " + fileList.size () );
                timestamp = fileList.get ( 0 ).getModifiedTime ().toString ();
            }
            Log.d ( "DRIVE", "Last modified timestamp is " + timestamp );
            return timestamp;
        } );
    }

    /**
     * Restores backup file in the form of byte array which is present in Google Drive
     *
     * @return byte array of latest backup file
     */
    public Task<byte[]> restore () {
        return Tasks.call ( mExecutor, () -> {
            byte[] data = null;
            String fileId;
            List<File> fileList = mDriveService.files ().list ().setSpaces ( "appDataFolder" )
                    .setFields ( "files(id)" ).execute ().getFiles ();
            if (fileList != null && fileList.size () > 0) {
                Log.d ( "DRIVE", "Count of files in Drive is " + fileList.size () );
                fileId = fileList.get ( 0 ).getId ();
                Log.d ( "DRIVE", "File to be restored has ID " + fileId );
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream ();
                mDriveService.files ().get ( fileId ).executeMediaAndDownloadTo ( outputStream );
                data = outputStream.toByteArray ();
                Log.d ( "DRIVE", "File retrieved of size " + data.length + " bytes" );
            }
            return data;
        } );
    }
}