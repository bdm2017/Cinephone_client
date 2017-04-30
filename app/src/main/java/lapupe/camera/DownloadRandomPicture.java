/*
 * Copyright (c) 2010-11 Dropbox, Inc.
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without
 * restriction, including without limitation the rights to use,
 * copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following
 * conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 * OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */


package lapupe.camera;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.exception.DropboxException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Here we show getting metadata for a directory and downloading a file in a
 * background thread, trying to show typical exception handling and flow of
 * control for an app that downloads a file from Dropbox.
 */

public class DownloadRandomPicture extends AsyncTask<Void, Long, Boolean> {


    private Context mContext;
    private final ProgressDialog mDialog;
    private DropboxAPI<?> mApi;
    private String mPath;
    private ImageView mView;

    private FileOutputStream mFos;

    private boolean mCanceled;
    private boolean mMatch;
    private Long mFileLen;
    private String mErrorMsg;

    private final String PHOTO_DIR = "/Photos/";

    // Note that, since we use a single file name here for simplicity, you
    // won't be able to use this code for two simultaneous downloads.
    private final static String IMAGE_FILE_NAME = "data.csv";

    public DownloadRandomPicture(Context context, DropboxAPI<?> api,
                                 String dropboxPath) {
        // We set the context this way so we don't accidentally leak activities
        mContext = context.getApplicationContext();

        mApi = api;
        mPath = PHOTO_DIR;

        mDialog = new ProgressDialog(context);
        mDialog.setMessage("Downloading Image");
        mDialog.setButton(ProgressDialog.BUTTON_POSITIVE, "Cancel", new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                mCanceled = true;
                mErrorMsg = "Canceled";

                // This will cancel the getThumbnail operation by closing
                // its stream
                if (mFos != null) {
                    try {
                        mFos.close();
                    } catch (IOException e) {
                    }
                }
            }
        });

        mDialog.show();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        try {
            // Get the metadata for a directory
            Entry dirent = mApi.metadata("/Photos/dataMovie.csv", 1000, null, true, null);
            String revOld = ReadSettings();
            revOld = revOld.substring(0, dirent.rev.length());
            if(revOld.equals(dirent.rev) == false) {
                mFileLen = dirent.bytes;

                File dir = new File(mContext.getCacheDir().getAbsolutePath());
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                File file = new File(mContext.getCacheDir().getAbsolutePath() + "/" + IMAGE_FILE_NAME);
                FileOutputStream outputStream = new FileOutputStream(file);
                DropboxAPI.DropboxFileInfo info = mApi.getFile("/Photos/dataMovie.csv", null, outputStream, null);
                WriteSettings(info.getMetadata().rev);
                Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
                mMatch = true;
            }else {
                mMatch = false;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        } catch (DropboxException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    protected void onProgressUpdate(Long... progress) {
        int percent = (int)(100.0*(double)progress[0]/mFileLen + 0.5);
        mDialog.setProgress(percent);
    }

    @Override
    protected void onPostExecute(Boolean result) {
        mDialog.dismiss();
    }

    private void showToast(String msg) {
        Toast error = Toast.makeText(mContext, msg, Toast.LENGTH_LONG);
        error.show();
    }


    public void WriteSettings( String data){
        FileOutputStream fOut = null;
        OutputStreamWriter osw = null;

        try{
            File file = new File(mContext.getCacheDir().getAbsolutePath() + "/rev");
            fOut = new FileOutputStream(file);
            osw = new OutputStreamWriter(fOut);
            osw.write(data);
            osw.flush();
            //popup surgissant pour le résultat
        }
        catch (Exception e) {
        }
        finally {
            try {
                osw.close();
                fOut.close();
            } catch (IOException e) {
            }
        }
    }

    public String ReadSettings(){
        FileInputStream fIn = null;
        InputStreamReader isr = null;

        char[] inputBuffer = new char[50];
        String data = null;

        try{
            File file = new File(mContext.getCacheDir().getAbsolutePath() + "/rev");
            fIn = new FileInputStream(file);
            isr = new InputStreamReader(fIn);
            isr.read(inputBuffer);
            data = new String(inputBuffer);
        }
        catch (Exception e) {
        }
        return data;
    }

    public boolean getMatch() {
        return mMatch;
    }


}
