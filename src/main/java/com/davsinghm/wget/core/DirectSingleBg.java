package com.davsinghm.wget.core;

import android.content.Context;
import android.net.Uri;

import com.davsinghm.wget.Constants;
import com.davsinghm.wget.core.info.ex.DownloadInterruptedException;
import com.davsinghm.wget.core.io.RandomAccessUri;
import com.davsinghm.wget.core.io.Utils;
import com.davsinghm.wget.core.util.HttpUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

public class DirectSingleBg extends Direct {

    private URL url;

    public DirectSingleBg(Context context, String url, Uri targetUri) throws MalformedURLException {
        super(context, null, targetUri);
        this.url = new URL(url);
    }

    public void downloadPart(AtomicBoolean stop) throws IOException {

        RandomAccessUri randomAccessUri = null;
        BufferedInputStream bufferedInputStream = null;

        try {

            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(Constants.WGET_CONNECT_TIMEOUT);
            urlConnection.setReadTimeout(Constants.WGET_READ_TIMEOUT);

            urlConnection.setRequestProperty("User-Agent", Constants.USER_AGENT); //NON-NLS

            HttpUtils.checkResponse(urlConnection);

            bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());

            randomAccessUri = Utils.openUriFile(getContext(), getTargetUri(), "rw");

            byte[] bytes = new byte[Constants.BUF_SIZE];
            int read;
            while ((read = bufferedInputStream.read(bytes)) > 0) {
                randomAccessUri.write(bytes, 0, read);

                if (stop.get())
                    throw new DownloadInterruptedException("Stopped");
                if (Thread.interrupted())
                    throw new DownloadInterruptedException("Interrupted");
            }

        } finally {
            if (randomAccessUri != null)
                randomAccessUri.close();
            if (bufferedInputStream != null)
                bufferedInputStream.close();
        }
    }

    @Override
    public void download(AtomicBoolean stop, Runnable notify) {

    }
}
