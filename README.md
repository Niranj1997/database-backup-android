# About
This is a simple Android application to backup and restore records from local database to Google Drive and vice versa

## Functionality
Home page consists of the following 7 buttons

 - Count Records
 - Add Records
 - Delete Records
 - Sign In
 - Backup
 - Restore
 - Sign Out

### Count Records
This is used to retrieve and show the number of records stored in the local database.
### Add Records
This is used to add 1000 sample records to the local database.
### Delete Records
This is used to delete all the records from the local database.
### Sign In
To use your Google Drive for backup and restore, this application must be connected to your Google Drive using your Google credentials. For authorization and giving access, this button is used. This will get access only to this application's **`appDataFolder`** and not complete Google Drive files or folders. Once sign in is successful, it will check if any backup file is present in Google Drive and display the message to the user.
### Backup
This functionality works only after the user is signed in. This will backup the local database contents to Google Drive as a hidden file under **`appDataFolder`**. If backup already found, this will update the existing file. If backup not found, this will create a new file in Google Drive. The backup file cannot be directly viewed in Google Drive. But size can be checked in Google Drive using **`Settings -> Manage Apps`**
### Restore
This functionality works only after the user is signed in. This will restore the Google Drive contents to local database. If no backup file found, it will show the message to user. If backup file found, then, that file will be retreived from Google Drive. Next, all records present in local database will be deleted. Then, it will insert the records restored from Google Drive to local database.
### Sign Out
This is used to sign out.

## More Information
For more help, feel free to leave a mail to niranj997@gmail.com

