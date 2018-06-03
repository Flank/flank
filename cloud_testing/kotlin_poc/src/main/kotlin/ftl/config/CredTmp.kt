package ftl.config

//
// Copyright (C) 2018-present Instructure, Inc.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

import com.google.api.client.auth.oauth2.Credential
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.http.HttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.client.util.store.FileDataStoreFactory
import java.io.File
import java.io.IOException

// Code from: https://developers.google.com/sheets/api/quickstart/java
object CredTmp {

    /**
     * Home directory for the current user
     */
    private val HOME = System.getProperty("user.home")

    /**
     * Global instance of the JSON factory.
     */
    private val JSON_FACTORY = JacksonFactory.getDefaultInstance()

    /**
     * Global instance of the HTTP transport.
     */
    private var HTTP_TRANSPORT: HttpTransport? = null

    /**
     * Global instance of the [FileDataStoreFactory].
     */
    private var DATA_STORE_FACTORY: FileDataStoreFactory? = null
    private val SAVE_CRED = ".credentials/flank_kotlin"

    init {
        try {
            HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport()
            DATA_STORE_FACTORY = FileDataStoreFactory(File(
                    HOME, SAVE_CRED))
        } catch (t: Throwable) {
            t.printStackTrace()
            System.exit(1)
        }

    }

    /**
     * Creates an authorized Credential object.
     *
     * @return an authorized Credential object.
     * @throws IOException
     */
    @Throws(IOException::class)
    fun authorize(): Credential {
        // https://github.com/bootstraponline/gcloud_cli/blob/e4b5e01610abad2e31d8a6edb20b17b2f84c5395/google-cloud-sdk/lib/googlecloudsdk/core/config.py#L167
        // TODO: delete scopes except for auth/cloud-platform
        val scopes = listOf(
                "https://www.googleapis.com/auth/userinfo.email",
                "https://www.googleapis.com/auth/cloud-platform",
                "https://www.googleapis.com/auth/appengine.admin",
                "https://www.googleapis.com/auth/compute",
                "https://www.googleapis.com/auth/accounts.reauth"
        )

        // https://github.com/bootstraponline/gcloud_cli/blob/40521a6e297830b9f652a9ab4d8002e309b4353a/google-cloud-sdk/platform/gsutil/gslib/utils/system_util.py#L177
        val clientId = "32555940559.apps.googleusercontent.com"
        val clientSecret = "ZmssLNjJy2998hD4CTg2ejr2"
        // Build flow and trigger user authorization request.
        val flow = GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientId, clientSecret, scopes)
                .setDataStoreFactory(DATA_STORE_FACTORY!!)
                .setAccessType("offline")
                .build()

        // Save credential to DATA_STORE_DIR
        return AuthorizationCodeInstalledApp(
                flow, LocalServerReceiver()).authorize("default")
    }
}
