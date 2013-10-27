// Nou5 Mauritius Contacts Update is a free and open source application 
//that enables you to effortlessly update your contacts with regards to the
//forthcoming 7 to 8 digits Numbering Migration planned for all mobile 
//networks in Mauritius as from 01 September 2013.

//Please note that this is the core code of Nou5 which is used to 
//update local mobile contacts. Please adapt this code to your own 
//program so that you can play with it. 

// Copyright (c) 2013  
// Author: Azagen Mootoo 
// Contact: hostintruder [at] gmail.com
// Website: www.expertforward.com

// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 3 of the License, or
// (at your option) any later version.

// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software Foundation,
// Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301  USA


package com.azagen.Nou5;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentProviderOperation;
import android.content.Context;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class UpdaterActivity extends Activity {

    //Global data declarations.
    private TextView textView_ProgressTextMessage;
    private TextView textView_titleUpdate;
    private Button buttonAddNumber5;
    private Button buttonRemoveNumber5;
    private ProgressBar progress;
    private Boolean buttonAdd5 = false;
    private Boolean buttonRemove5 = false;
    private Integer contactsCounter;
    private Integer batchCounter;
    private Boolean batchMode = false;
    private Boolean running = false;
    private Boolean updatingDB = false;
    private Toast toast;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.updater_layout);

        //Variables assignment.
        textView_ProgressTextMessage = (TextView) findViewById(R.id.progressTextMessage);
        progress = (ProgressBar) findViewById(R.id.progressBar1);
        buttonAddNumber5 = (Button) findViewById(R.id.btnAddNumber5);
        buttonRemoveNumber5 = (Button) findViewById(R.id.btnRemoveNumber5);
        textView_titleUpdate = (TextView) findViewById(R.id.titleUpdate);

        //Declaring of custom font to objects.
        Typeface font_Kavoon = Typeface.createFromAsset(getAssets(), "fonts/Kavoon-Regular.otf");
        Typeface font_Pacifico = Typeface.createFromAsset(getAssets(), "fonts/Pacifico.ttf");
        Typeface font_Lobster = Typeface.createFromAsset(getAssets(), "fonts/Lobster.otf");

        //Applying custom font to objects.
        buttonAddNumber5.setTypeface(font_Pacifico);
        buttonRemoveNumber5.setTypeface(font_Pacifico);
        textView_titleUpdate.setTypeface(font_Lobster);
        textView_ProgressTextMessage.setTypeface(font_Kavoon);

        //set title text
        textView_titleUpdate.setText(" Manage Contacts");

    }

    //When the button Add number 5 is clicked, the following
    //code logic will be executed.
    public void onClickAdd5Button(View view) {


        //display a dialog for backup purposes...
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Information");
        builder.setMessage("All local mobile numbers will be updated. Do you want to proceed?");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();

                        //the code logic below will execute the update process,
                        //if the button yes is clicked.
                        buttonAdd5 = true;
                        buttonRemove5 = false;
                        contactsCounter = 0;
                        batchCounter = 0;
                        UpdateAllContactsTask task = new UpdateAllContactsTask();
                        task.execute(new String[]{" "});

                        //clearing the result text box
                        textView_ProgressTextMessage.setText("");
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();


    }

    //When the button Remove number 5 is clicked, the following
    //code logic will be executed.
    public void onClickRemove5Button(View view) {


        //display a dialog for backup purposes...
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle("Information");
        builder.setMessage("All updated local mobile numbers will be rolled back. Do you want to proceed?");
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        dialog.dismiss();

                        //the code logic below will execute the update process,
                        //if the button yes is clicked.
                        buttonRemove5 = true;
                        buttonAdd5 = false;

                        contactsCounter = 0;
                        batchCounter = 0;

                        UpdateAllContactsTask task = new UpdateAllContactsTask();
                        task.execute(new String[]{" "});

                        //clearing the result text box
                        textView_ProgressTextMessage.setText("");
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {

                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();

    }

    //sub routines to check if the phone number is a
    //mobile phone number in the republic of mauritius numbering plan.
    public boolean isMobileNumber(String mobnumber) {

        if (mobnumber.matches("^[2][5].*")                 //Cellplus Mobile numbers
                || mobnumber.matches("^[2][9].*")          //ICTA
                || mobnumber.matches("^[4][2][1].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][2][2].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][2][3].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][2][8].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][2][9].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][4].*")          //ICTA
                || mobnumber.matches("^[4][7][1].*")       //ICTA
                || mobnumber.matches("^[4][7][2].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][7][3].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][7][4].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][7][5].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][7][6].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][7][7].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][7][8].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][7][9].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[4][9].*")          //Emtel Mobile numbers
                || mobnumber.matches("^[7].*")             //Mobile numbers Cellplus + Emtel
                || mobnumber.matches("^[8][2].*")          //Cellplus Mobile numbers
                || mobnumber.matches("^[8][5].*")          //MTML Mobile number
                || mobnumber.matches("^[8][6].*")          //MTML Mobile numbers
                || mobnumber.matches("^[8][7][1].*")       //MTML Mobile numbers
                || mobnumber.matches("^[8][7][5].*")       //Cellplus Mobile numbers
                || mobnumber.matches("^[8][7][6].*")       //Cellplus Mobile numbers
                || mobnumber.matches("^[8][7][7].*")       //Cellplus Mobile numbers
                || mobnumber.matches("^[8][7][8].*")       //Cellplus Mobile numbers
                || mobnumber.matches("^[9][0].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[9][1].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[9][2].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[9][3].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[9][4].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[9][5].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[9][6].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[9][7].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[9][8].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                ) {
            return true;
        } else {
            return false;
        }

    }

    //checking if the number processed is a phone number where the
    //number 5 has already been included.
    public boolean isNewMobileNumber(String mobnumber) {
        //if the number starts by the number 5, go on dude, make it fast.
        //if the phone number starts by 5, proceed with changes.
        if (mobnumber.matches("^[5][2][5].*")                 //Cellplus Mobile numbers
                || mobnumber.matches("^[5][2][9].*")          //ICTA
                || mobnumber.matches("^[5][4][2][1].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][2][2].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][2][3].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][2][8].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][2][9].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][4].*")          //ICTA
                || mobnumber.matches("^[5][4][7][1].*")       //ICTA
                || mobnumber.matches("^[5][4][7][2].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][7][3].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][7][4].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][7][5].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][7][6].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][7][7].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][7][8].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][7][9].*")       //Emtel Mobile numbers
                || mobnumber.matches("^[5][4][9].*")          //Emtel Mobile numbers
                || mobnumber.matches("^[5][7].*")             //Mobile numbers Cellplus + Emtel
                || mobnumber.matches("^[5][8][2].*")          //Cellplus Mobile numbers
                || mobnumber.matches("^[5][8][5].*")          //MTML Mobile numbers
                || mobnumber.matches("^[5][8][6].*")          //MTML Mobile numbers
                || mobnumber.matches("^[5][8][7][1].*")       //MTML Mobile numbers
                || mobnumber.matches("^[5][8][7][5].*")       //Cellplus Mobile numbers
                || mobnumber.matches("^[5][8][7][6].*")       //Cellplus Mobile numbers
                || mobnumber.matches("^[5][8][7][7].*")       //Cellplus Mobile numbers
                || mobnumber.matches("^[5][8][7][8].*")       //Cellplus Mobile numbers
                || mobnumber.matches("^[5][9][0].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[5][9][1].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[5][9][2].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[5][9][3].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[5][9][4].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[5][9][5].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[5][9][6].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[5][9][7].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                || mobnumber.matches("^[5][9][8].*")          //Mobile numbers Cellplus + Emtel + MTML > ICTA Updated
                ) {
            return true;
        } else {
            return false;
        }
    }

    //when the back button is pressed, the following code will get executed.
    @Override
    public void onBackPressed() {

        //if update is in progress in the aSync task, warn user.
        if (running) {

            //checking if the program has not yet start the update
            if (updatingDB == false) {

                //display a dialog for backup purposes...
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setCancelable(true);

                if (buttonAdd5) {
                    builder.setTitle("Update in progress...");
                    builder.setMessage("Are you sure you want to cancel the update?");
                } else {
                    builder.setTitle("Rollback in progress… ");
                    builder.setMessage("Are you sure you want to cancel the rollback?");
                }

                builder.setInverseBackgroundForced(true);
                builder.setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();

                                //checking again if the program has not yet start the update
                                if (updatingDB == false) {
                                    //set running to false, so that aSync task is cancel
                                    running = false;
                                    //okay,go back
                                    finish();
                                    overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
                                } else {
                                    //display another type of message
                                    Context context = getApplicationContext();
                                    CharSequence text = "Please wait, saving already in progress...";
                                    int duration = Toast.LENGTH_SHORT;

                                    if (toast == null) {
                                        toast = Toast.makeText(context, text, duration);
                                        toast.show();
                                    }

                                    if (!toast.getView().isShown()) {
                                        toast.show();
                                    }
                                }
                            }
                        });
                builder.setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                dialog.dismiss();

                                //do not finish the activity
                                return;

                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            } else {
                //display another type of message
                Context context = getApplicationContext();
                CharSequence text;

                if (batchMode == false)
                {
                    text = "Please wait ...";
                }
                else
                {
                    text = "Some contacts have already been updated! Please wait ... ";
                }

                int duration = Toast.LENGTH_SHORT;

                if (toast == null) {
                    toast = Toast.makeText(context, text, duration);
                    toast.show();
                }

                if (!toast.getView().isShown()) {
                    //toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
            }


        } else {

            //no aSync task is running, do back to main page.
            super.onBackPressed();
            overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        }

    }

    private class UpdateAllContactsTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... urls) {

            String response = "";
            ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            //flags
            Boolean flag_230 = false;
            Boolean flag_plus230 = false;
            Boolean flag_00230 = false;

            //getting all the data from tables without any condition (4 parameters null) queries for the
            //time being and bring everything to the cursor.
            Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    null,
                    null,
                    null,
                    null);

            int progCounter = 0;
            int maxContacts = cursor.getCount();
            int maxContactsPer = 0;


            //checking if the cursor is not empty before proceeding.
            if (cursor.getCount() > 0) {

                maxContactsPer = 47000 / maxContacts;

                //iteration through the cursor.
                while (cursor.moveToNext()) {

                    //if task has been cancelled
                    if (running == false) {
                        break;
                    }

                    publishProgress(progCounter);
                    progCounter += maxContactsPer;
                    // Pick out the ID, and the Display name of the
                    // contact from the current row of the cursor
                    String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    if (Integer.parseInt(cursor.getString(
                            cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {

                        Cursor pCur = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                                null,
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                                new String[]{id},
                                null);

                        while (pCur.moveToNext()) {

                            //resetting the flag
                            flag_230 = false;
                            flag_plus230 = false;
                            flag_00230 = false;

                            // Do something with phones
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            String phoneNoStr = String.valueOf(phoneNo);
                            String newPhoneNoStr = "00000000";

                            //replace anything that is a space character (including space, tab characters etc)
                            phoneNoStr = phoneNoStr.replaceAll("\\s", "");


                            //if the safe mode button has been checked, proceed with this kind
                            //of processing, else go for a coffee or do something else.
                            //if safe mode has been enabled, then update only mobile number type set in
                            //android, else if bigmode, then use republic of mauritius phone numbering plan.
                            if (buttonAdd5) {

                                //dealing with 230 and +230 issue.
                                if (phoneNoStr.matches("^[+][2][3][0].*")) {
                                    //remove the +230 from the string
                                    phoneNoStr = phoneNoStr.substring(4, phoneNoStr.length());

                                    //set a flag for later use
                                    flag_plus230 = true;

                                } else if (phoneNoStr.matches("^[2][3][0].*")) {
                                    //remove the 230 from the string
                                    phoneNoStr = phoneNoStr.substring(3, phoneNoStr.length());

                                    //set a flag for later use
                                    flag_230 = true;

                                } else if (phoneNoStr.matches("^[0][0][2][3][0].*")) {
                                    //remove the 00230 from the string
                                    phoneNoStr = phoneNoStr.substring(5, phoneNoStr.length());

                                    //set a flag for later use
                                    flag_00230 = true;

                                }

                                //remove all non number characters from the phone number
                                phoneNoStr = phoneNoStr.replaceAll("[^\\d]", "");


                                if (phoneNoStr.length() == 7) {
                                    if (isMobileNumber(phoneNoStr)) {
                                        if (flag_plus230) {
                                            //add the new famous number '5' to the phone number.
                                            newPhoneNoStr = "+2305" + phoneNoStr;
                                        } else if (flag_230) {
                                            //add the new famous number '5' to the phone number.
                                            newPhoneNoStr = "2305" + phoneNoStr;
                                        } else if (flag_00230) {
                                            //add the new famous number '5' to the phone number.
                                            newPhoneNoStr = "002305" + phoneNoStr;
                                        } else {
                                            //add the new famous number '5' to the phone number.
                                            newPhoneNoStr = "5" + phoneNoStr;
                                        }

                                        contactsCounter = contactsCounter + 1;

                                        //begin of addition bulk
                                        batchCounter = batchCounter + 1;
                                        //end of addition bulk

                                        try {

                                            String rawContactID = pCur.getString(pCur.getColumnIndex(ContactsContract.Data._ID));

                                            String where = ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                                                    ContactsContract.Data._ID + " = ? AND " +
                                                    ContactsContract.Data.MIMETYPE + " = ? ";

                                            String[] params = new String[]{name,
                                                    rawContactID,
                                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                                            };

                                            ops.add(ContentProviderOperation
                                                    .newUpdate(ContactsContract.Data.CONTENT_URI)
                                                    .withSelection(where, params)
                                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNoStr)
                                                    .build());

                                        } catch (Exception e) {
                                            //do something catchy :)
                                        }

                                    }
                                }


                                //begin of addition bulk
                                if (batchCounter == 400) {

                                    //do the batch update now
                                    try {

                                        updatingDB = true;
                                        batchMode = true;

                                        getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                        ops = new ArrayList<ContentProviderOperation>(400);


                                    } catch (RemoteException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    } catch (OperationApplicationException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }

                                    //reset the batch counter
                                    batchCounter = 0;

                                }
                                //end of addition bulk


                                if (contactsCounter > 0) {

                                    response = contactsCounter.toString() + " mobile numbers updated successfully.";
                                    //response = "Mobile numbers updated successfully.";
                                } else {
                                    response = "No applicable mobile number found for update.";
                                }

                            } else if (buttonRemove5) {

                                //dealing with 230 and +230 issue.
                                if (phoneNoStr.matches("^[+][2][3][0].*")) {
                                    //remove the +230 from the string
                                    phoneNoStr = phoneNoStr.substring(4, phoneNoStr.length());

                                    //set a flag for later use
                                    flag_plus230 = true;

                                } else if (phoneNoStr.matches("^[2][3][0].*")) {
                                    //remove the 230 from the string
                                    phoneNoStr = phoneNoStr.substring(3, phoneNoStr.length());

                                    //set a flag for later use
                                    flag_230 = true;

                                } else if (phoneNoStr.matches("^[0][0][2][3][0].*")) {
                                    //remove the 00230 from the string
                                    phoneNoStr = phoneNoStr.substring(5, phoneNoStr.length());

                                    //set a flag for later use
                                    flag_00230 = true;

                                }

                                //remove all non number characters from the phone number
                                phoneNoStr = phoneNoStr.replaceAll("[^\\d]", "");

                                if (phoneNoStr.length() == 8) {

                                    //Okay dude, here we are going to get back to the old systems, okay
                                    //all number '5' will get removed. Rolling back will be fast and smooth, but
                                    //please use at your own risk if you have other international number starting by
                                    //the number '5'.

                                    //if the number starts by the number 5, go on dude, make it fast.
                                    //if the phone number starts by 5, proceed with changes.
                                    if (isNewMobileNumber(phoneNoStr)) {
                                        //remove the first character from the string
                                        newPhoneNoStr = phoneNoStr.substring(1);


                                        if (flag_plus230) {
                                            //add the new famous number '5' to the phone number.
                                            newPhoneNoStr = "+230" + newPhoneNoStr;
                                        } else if (flag_230) {
                                            //add the new famous number '5' to the phone number.
                                            newPhoneNoStr = "230" + newPhoneNoStr;
                                        } else if (flag_00230) {
                                            //add the new famous number '5' to the phone number.
                                            newPhoneNoStr = "00230" + newPhoneNoStr;
                                        } else {
                                            //do nothing
                                        }

                                        contactsCounter = contactsCounter + 1;

                                        //begin of addition bulk
                                        batchCounter = batchCounter + 1;
                                        //end of addition bulk

                                        try {
                                            String rawContactID = pCur.getString(pCur.getColumnIndex(ContactsContract.Data._ID));

                                            String where = ContactsContract.Data.DISPLAY_NAME + " = ? AND " +
                                                    ContactsContract.Data._ID + " = ? AND " +
                                                    ContactsContract.Data.MIMETYPE + " = ? ";

                                            String[] params = new String[]{name,
                                                    rawContactID,
                                                    ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                                            };

                                            ops.add(ContentProviderOperation
                                                    .newUpdate(ContactsContract.Data.CONTENT_URI)
                                                    .withSelection(where, params)
                                                    .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, newPhoneNoStr)
                                                    .build());


                                        } catch (Exception e) {
                                            //do something catchy :)
                                        }
                                    }

                                }


                                //begin of addition bulk
                                if (batchCounter == 400) {

                                    //do the batch update now
                                    try {

                                        updatingDB = true;
                                        batchMode = true;

                                        getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                                        ops = new ArrayList<ContentProviderOperation>(400);


                                    } catch (RemoteException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    } catch (OperationApplicationException e) {
                                        e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                                    }

                                    //reset the batch counter
                                    batchCounter = 0;

                                }
                                //end of addition bulk

                                if (contactsCounter > 0) {

                                    response = contactsCounter.toString() + " mobile numbers rolled back successfully.";
                                    //response = "Contacts rollback successfully.";
                                } else {
                                    response = "No applicable mobile number found for rollback.";
                                }
                            }
                        }
                        pCur.close();

                    }
                }

                //if contacts is available and that no phone number is
                //found for all the contacts, then send this response
                if (contactsCounter == 0) {
                    if (buttonAdd5) {
                        response = "No applicable mobile number found for update.";
                    } else {
                        response = "No applicable mobile number found for rollback.";
                    }
                }

            } else {

                response = "Contact list may be empty";
            }

            cursor.close();


            //if task has not been cancelled
            if (running == true) {

                //do the batch update now
                try {

                    updatingDB = true;

                    getContentResolver().applyBatch(ContactsContract.AUTHORITY, ops);
                    //set progress bar to end
                    progress.setProgress(50000);


                } catch (RemoteException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (OperationApplicationException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }

            return response;
        }

        @Override
        protected void onPreExecute() {
            //settings for the progress bar
            progress.setMax(50000);
            progress.setVisibility(View.VISIBLE);

            //settings for the button clicked
            //disabling the button
            buttonAddNumber5.setEnabled(false);
            buttonAddNumber5.setTextColor(Color.GRAY);

            buttonRemoveNumber5.setEnabled(false);
            buttonRemoveNumber5.setTextColor(Color.GRAY);

            //setting flags
            running = true;
            updatingDB = false;
            batchMode = false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            //settings for the progressbar
            progress.setProgress(values[0]);

            if (contactsCounter == 1) {
                //set text
                textView_ProgressTextMessage.setText("Processing " + contactsCounter.toString() + " contact...");
            } else if (contactsCounter > 1) {
                //set text
                textView_ProgressTextMessage.setText("Processing " + contactsCounter.toString() + " contacts...");
            }
            else {
                //set text
                textView_ProgressTextMessage.setText("Processing...");
            }


            //begin of addition bulk
            if(batchCounter > 390)
            {
                textView_ProgressTextMessage.setText("Saving batch of contacts... Please wait!");
            }
            //end of addition bulk


            if (values[0] > 46000 && contactsCounter > 0 ) {

                textView_ProgressTextMessage.setText("Saving... Please wait!");

            }
        }

        @Override
        protected void onPostExecute(String result) {
            //settings for the progressbar
            textView_ProgressTextMessage.setText(result);
            progress.setVisibility(View.INVISIBLE);

            //settings for the button clicked
            //enabling the button
            buttonAddNumber5.setEnabled(true);
            buttonAddNumber5.setTextColor(Color.BLACK);

            buttonRemoveNumber5.setEnabled(true);
            buttonRemoveNumber5.setTextColor(Color.BLACK);

            //clearing all flags
            buttonAdd5 = false;
            buttonRemove5 = false;
            running = false;
            updatingDB = false;
            batchMode = false;
        }

    }
}

// Copyright (c) 2013  Azagen Mootoo
// Under GPL R3 (www.expertforward.com) 