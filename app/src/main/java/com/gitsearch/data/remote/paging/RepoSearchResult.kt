/*
 * Copyright (C) 2018 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitsearch.data.remote.paging

import android.arch.lifecycle.LiveData
import android.arch.paging.PagedList
import com.gitsearch.data.model.NetworkState
import com.gitsearch.data.model.Repo

data class RepoSearchResult(
        val data: LiveData<PagedList<Repo>>,
        val networkState: LiveData<NetworkState>,
        val initialState: LiveData<NetworkState>,
        val refresh: () -> Unit
)