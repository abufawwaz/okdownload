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

package cn.dreamtobe.okdownload.core.breakpoint;


import android.support.annotation.Nullable;

import cn.dreamtobe.okdownload.task.DownloadTask;

/**
 * Created by Jacksgong on 19/09/2017.
 */

public interface BreakpointStore {

    @Nullable
    BreakpointInfo get(int id);

    BreakpointInfo createAndInsert(DownloadTask task);

    void onProcessCurrentOffset(BreakpointInfo info, int blockIndex, long fetchLength);

    boolean update(BreakpointInfo breakpointInfo);

    void completeDownload(int id);

    int createId(DownloadTask task);
}
