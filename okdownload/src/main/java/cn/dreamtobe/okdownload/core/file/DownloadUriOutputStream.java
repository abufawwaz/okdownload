/*
 * Copyright (C) 2017 Jacksgong(jacksgong.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package cn.dreamtobe.okdownload.core.file;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.ParcelFileDescriptor;
import android.system.ErrnoException;
import android.system.Os;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

import cn.dreamtobe.okdownload.OkDownload;
import cn.dreamtobe.okdownload.core.util.LogUtil;

/**
 * The output stream for uri.
 */

public class DownloadUriOutputStream implements DownloadOutputStream {

    private FileChannel channel;
    private ParcelFileDescriptor pdf;
    private BufferedOutputStream out;

    public DownloadUriOutputStream(Context context, Uri uri) throws FileNotFoundException {
        pdf = context.getContentResolver().openFileDescriptor(uri, "rw");
        if (pdf == null) throw new IllegalArgumentException();

        final FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
        channel = fos.getChannel();
        out = new BufferedOutputStream(fos, OkDownload.with().processFileStrategy.buffSize());
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }

    @Override
    public void flushAndSync() throws IOException {
        out.flush();
        pdf.getFileDescriptor().sync();
    }

    @Override
    public boolean supportSeek() {
        return true;
    }

    @Override
    public void seek(long offset) throws IOException {
        channel.position(offset);
    }

    @Override
    public void setLength(long newLength) throws IOException {
        final String tag = "DownloadUriOutputStream";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            try {
                Os.ftruncate(pdf.getFileDescriptor(), newLength);
            } catch (ErrnoException e) {
                LogUtil.w(tag, "It can't pre-allocate length(" + newLength + ") on the sdk" +
                        " version(" + Build.VERSION.SDK_INT + "), because of " + e);
                e.printStackTrace();
            }
        } else {
            LogUtil.w(tag,
                    "it can't pre-allocate length(" + newLength + ") on the sdk " +
                            "version(" + Build.VERSION.SDK_INT + ")");
        }
    }

    public static class Factory implements DownloadOutputStream.Factory {

        @Override
        public DownloadOutputStream create(Context context, File file) throws FileNotFoundException {
            return new DownloadUriOutputStream(context, Uri.fromFile(file));
        }

        @Override
        public DownloadOutputStream create(Context context, Uri uri) throws FileNotFoundException {
            return new DownloadUriOutputStream(context, uri);
        }
    }
}
