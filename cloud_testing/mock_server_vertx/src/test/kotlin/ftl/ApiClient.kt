package ftl;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer
import io.vertx.core.streams.ReadStream
import io.vertx.core.json.JsonObject
import io.vertx.ext.web.client.HttpRequest;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.client.WebClientOptions;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Base64;

public class ApiClient {
    private var client: WebClient



    private var cookieParams: MultiMap

    internal constructor(vertx: Vertx, host: String, port: Int) {
        client = WebClient.create(vertx, WebClientOptions().setDefaultHost(host).setDefaultPort(port))
        cookieParams = MultiMap.caseInsensitiveMultiMap()
    }

    internal constructor(client: WebClient) {
        this.client = client
        cookieParams = MultiMap.caseInsensitiveMultiMap()
    }

    /**
     * Call GetV1TestEnvironmentCatalog with empty body. 
     * 
     * @param environmentType Parameter environmentType inside path
     * @param uploadProtocol Parameter upload_protocol inside query
     * @param prettyPrint Parameter prettyPrint inside query
     * @param uploadType Parameter uploadType inside query
     * @param fields Parameter fields inside query
     * @param $Xgafv Parameter $.xgafv inside query
     * @param callback Parameter callback inside query
     * @param alt Parameter alt inside query
     * @param accessToken Parameter access_token inside query
     * @param key Parameter key inside query
     * @param quotaUser Parameter quotaUser inside query
     * @param pp Parameter pp inside query
     * @param oauthToken Parameter oauth_token inside query
     * @param bearerToken Parameter bearer_token inside query
     * @param projectId Parameter projectId inside query
     * @param handler The handler for the asynchronous request
     */
    fun GetV1TestEnvironmentCatalog(
        environmentType: String?,
        uploadProtocol: String? ,
        prettyPrint: Boolean? ,
        uploadType: String? ,
        fields: String? ,
        $Xgafv: String? ,
        callback: String? ,
        alt: String? ,
        accessToken: String? ,
        key: String? ,
        quotaUser: String? ,
        pp: Boolean? ,
        oauthToken: String? ,
        bearerToken: String? ,
        projectId: String? ,
        handler: Handler<AsyncResult<HttpResponse<Buffer>>>) {
        // Check required params
        if (environmentType == null) throw RuntimeException("Missing parameter environmentType in path");
        

        // Generate the uri
        var uri: String = "/v1/testEnvironmentCatalog/{environmentType}"
        uri = uri.replaceAll("\\{{1}([.;?*+]*([^\\{\\}.;?*+]+)[^\\}]*)\\}{1}", "{$2}") //Remove * . ; ? from url template
        uri = uri.replace("{environmentType}", this.renderPathParam("environmentType", environmentType));


        var request = client.get(uri)

        var requestCookies = MultiMap.caseInsensitiveMultiMap()
        if (uploadProtocol != null) this.addQueryParam("upload_protocol", uploadProtocol, request);
        if (prettyPrint != null) this.addQueryParam("prettyPrint", prettyPrint, request);
        if (uploadType != null) this.addQueryParam("uploadType", uploadType, request);
        if (fields != null) this.addQueryParam("fields", fields, request);
        if ($Xgafv != null) this.addQueryParam("$.xgafv", $Xgafv, request);
        if (callback != null) this.addQueryParam("callback", callback, request);
        if (alt != null) this.addQueryParam("alt", alt, request);
        if (accessToken != null) this.addQueryParam("access_token", accessToken, request);
        if (key != null) this.addQueryParam("key", key, request);
        if (quotaUser != null) this.addQueryParam("quotaUser", quotaUser, request);
        if (pp != null) this.addQueryParam("pp", pp, request);
        if (oauthToken != null) this.addQueryParam("oauth_token", oauthToken, request);
        if (bearerToken != null) this.addQueryParam("bearer_token", bearerToken, request);
        if (projectId != null) this.addQueryParam("projectId", projectId, request);
        

        this.renderAndAttachCookieHeader(request, requestCookies);
        request.send(handler);
    }

    /**
     * Call CancelV1ProjectsTestMatricesCancelByProjectId with empty body. 
     * 
     * @param projectId Parameter projectId inside path
     * @param testMatrixId Parameter testMatrixId inside path
     * @param uploadProtocol Parameter upload_protocol inside query
     * @param prettyPrint Parameter prettyPrint inside query
     * @param uploadType Parameter uploadType inside query
     * @param fields Parameter fields inside query
     * @param $Xgafv Parameter $.xgafv inside query
     * @param callback Parameter callback inside query
     * @param alt Parameter alt inside query
     * @param accessToken Parameter access_token inside query
     * @param key Parameter key inside query
     * @param quotaUser Parameter quotaUser inside query
     * @param pp Parameter pp inside query
     * @param oauthToken Parameter oauth_token inside query
     * @param bearerToken Parameter bearer_token inside query
     * @param handler The handler for the asynchronous request
     */
    fun CancelV1ProjectsTestMatricesCancelByProjectId(
        projectId: String?,
        testMatrixId: String?,
        uploadProtocol: String? ,
        prettyPrint: Boolean? ,
        uploadType: String? ,
        fields: String? ,
        $Xgafv: String? ,
        callback: String? ,
        alt: String? ,
        accessToken: String? ,
        key: String? ,
        quotaUser: String? ,
        pp: Boolean? ,
        oauthToken: String? ,
        bearerToken: String? ,
        handler: Handler<AsyncResult<HttpResponse<Buffer>>>) {
        // Check required params
        if (projectId == null) throw RuntimeException("Missing parameter projectId in path");
        if (testMatrixId == null) throw RuntimeException("Missing parameter testMatrixId in path");
        

        // Generate the uri
        var uri: String = "/v1/projects/{projectId}/testMatrices/{testMatrixId}:cancel"
        uri = uri.replaceAll("\\{{1}([.;?*+]*([^\\{\\}.;?*+]+)[^\\}]*)\\}{1}", "{$2}") //Remove * . ; ? from url template
        uri = uri.replace("{projectId}", this.renderPathParam("projectId", projectId));
        uri = uri.replace("{testMatrixId}", this.renderPathParam("testMatrixId", testMatrixId));


        var request = client.post(uri)

        var requestCookies = MultiMap.caseInsensitiveMultiMap()
        if (uploadProtocol != null) this.addQueryParam("upload_protocol", uploadProtocol, request);
        if (prettyPrint != null) this.addQueryParam("prettyPrint", prettyPrint, request);
        if (uploadType != null) this.addQueryParam("uploadType", uploadType, request);
        if (fields != null) this.addQueryParam("fields", fields, request);
        if ($Xgafv != null) this.addQueryParam("$.xgafv", $Xgafv, request);
        if (callback != null) this.addQueryParam("callback", callback, request);
        if (alt != null) this.addQueryParam("alt", alt, request);
        if (accessToken != null) this.addQueryParam("access_token", accessToken, request);
        if (key != null) this.addQueryParam("key", key, request);
        if (quotaUser != null) this.addQueryParam("quotaUser", quotaUser, request);
        if (pp != null) this.addQueryParam("pp", pp, request);
        if (oauthToken != null) this.addQueryParam("oauth_token", oauthToken, request);
        if (bearerToken != null) this.addQueryParam("bearer_token", bearerToken, request);
        

        this.renderAndAttachCookieHeader(request, requestCookies);
        request.send(handler);
    }

    /**
     * Call GetV1ProjectsTestMatricesByProjectId with empty body. 
     * 
     * @param projectId Parameter projectId inside path
     * @param testMatrixId Parameter testMatrixId inside path
     * @param uploadProtocol Parameter upload_protocol inside query
     * @param prettyPrint Parameter prettyPrint inside query
     * @param uploadType Parameter uploadType inside query
     * @param fields Parameter fields inside query
     * @param $Xgafv Parameter $.xgafv inside query
     * @param callback Parameter callback inside query
     * @param alt Parameter alt inside query
     * @param accessToken Parameter access_token inside query
     * @param key Parameter key inside query
     * @param quotaUser Parameter quotaUser inside query
     * @param pp Parameter pp inside query
     * @param oauthToken Parameter oauth_token inside query
     * @param bearerToken Parameter bearer_token inside query
     * @param handler The handler for the asynchronous request
     */
    fun GetV1ProjectsTestMatricesByProjectId(
        projectId: String?,
        testMatrixId: String?,
        uploadProtocol: String? ,
        prettyPrint: Boolean? ,
        uploadType: String? ,
        fields: String? ,
        $Xgafv: String? ,
        callback: String? ,
        alt: String? ,
        accessToken: String? ,
        key: String? ,
        quotaUser: String? ,
        pp: Boolean? ,
        oauthToken: String? ,
        bearerToken: String? ,
        handler: Handler<AsyncResult<HttpResponse<Buffer>>>) {
        // Check required params
        if (projectId == null) throw RuntimeException("Missing parameter projectId in path");
        if (testMatrixId == null) throw RuntimeException("Missing parameter testMatrixId in path");
        

        // Generate the uri
        var uri: String = "/v1/projects/{projectId}/testMatrices/{testMatrixId}"
        uri = uri.replaceAll("\\{{1}([.;?*+]*([^\\{\\}.;?*+]+)[^\\}]*)\\}{1}", "{$2}") //Remove * . ; ? from url template
        uri = uri.replace("{projectId}", this.renderPathParam("projectId", projectId));
        uri = uri.replace("{testMatrixId}", this.renderPathParam("testMatrixId", testMatrixId));


        var request = client.get(uri)

        var requestCookies = MultiMap.caseInsensitiveMultiMap()
        if (uploadProtocol != null) this.addQueryParam("upload_protocol", uploadProtocol, request);
        if (prettyPrint != null) this.addQueryParam("prettyPrint", prettyPrint, request);
        if (uploadType != null) this.addQueryParam("uploadType", uploadType, request);
        if (fields != null) this.addQueryParam("fields", fields, request);
        if ($Xgafv != null) this.addQueryParam("$.xgafv", $Xgafv, request);
        if (callback != null) this.addQueryParam("callback", callback, request);
        if (alt != null) this.addQueryParam("alt", alt, request);
        if (accessToken != null) this.addQueryParam("access_token", accessToken, request);
        if (key != null) this.addQueryParam("key", key, request);
        if (quotaUser != null) this.addQueryParam("quotaUser", quotaUser, request);
        if (pp != null) this.addQueryParam("pp", pp, request);
        if (oauthToken != null) this.addQueryParam("oauth_token", oauthToken, request);
        if (bearerToken != null) this.addQueryParam("bearer_token", bearerToken, request);
        

        this.renderAndAttachCookieHeader(request, requestCookies);
        request.send(handler);
    }

    /**
     * Call CreateV1ProjectsTestMatricesByProjectId with empty body. 
     * 
     * @param projectId Parameter projectId inside path
     * @param uploadProtocol Parameter upload_protocol inside query
     * @param prettyPrint Parameter prettyPrint inside query
     * @param uploadType Parameter uploadType inside query
     * @param fields Parameter fields inside query
     * @param $Xgafv Parameter $.xgafv inside query
     * @param callback Parameter callback inside query
     * @param alt Parameter alt inside query
     * @param accessToken Parameter access_token inside query
     * @param key Parameter key inside query
     * @param quotaUser Parameter quotaUser inside query
     * @param pp Parameter pp inside query
     * @param oauthToken Parameter oauth_token inside query
     * @param bearerToken Parameter bearer_token inside query
     * @param requestId Parameter requestId inside query
     * @param handler The handler for the asynchronous request
     */
    fun CreateV1ProjectsTestMatricesByProjectIdWithEmptyBody(
        projectId: String?,
        uploadProtocol: String? ,
        prettyPrint: Boolean? ,
        uploadType: String? ,
        fields: String? ,
        $Xgafv: String? ,
        callback: String? ,
        alt: String? ,
        accessToken: String? ,
        key: String? ,
        quotaUser: String? ,
        pp: Boolean? ,
        oauthToken: String? ,
        bearerToken: String? ,
        requestId: String? ,
        handler: Handler<AsyncResult<HttpResponse<Buffer>>>) {
        // Check required params
        if (projectId == null) throw RuntimeException("Missing parameter projectId in path");
        

        // Generate the uri
        var uri: String = "/v1/projects/{projectId}/testMatrices"
        uri = uri.replaceAll("\\{{1}([.;?*+]*([^\\{\\}.;?*+]+)[^\\}]*)\\}{1}", "{$2}") //Remove * . ; ? from url template
        uri = uri.replace("{projectId}", this.renderPathParam("projectId", projectId));


        var request = client.post(uri)

        var requestCookies = MultiMap.caseInsensitiveMultiMap()
        if (uploadProtocol != null) this.addQueryParam("upload_protocol", uploadProtocol, request);
        if (prettyPrint != null) this.addQueryParam("prettyPrint", prettyPrint, request);
        if (uploadType != null) this.addQueryParam("uploadType", uploadType, request);
        if (fields != null) this.addQueryParam("fields", fields, request);
        if ($Xgafv != null) this.addQueryParam("$.xgafv", $Xgafv, request);
        if (callback != null) this.addQueryParam("callback", callback, request);
        if (alt != null) this.addQueryParam("alt", alt, request);
        if (accessToken != null) this.addQueryParam("access_token", accessToken, request);
        if (key != null) this.addQueryParam("key", key, request);
        if (quotaUser != null) this.addQueryParam("quotaUser", quotaUser, request);
        if (pp != null) this.addQueryParam("pp", pp, request);
        if (oauthToken != null) this.addQueryParam("oauth_token", oauthToken, request);
        if (bearerToken != null) this.addQueryParam("bearer_token", bearerToken, request);
        if (requestId != null) this.addQueryParam("requestId", requestId, request);
        

        this.renderAndAttachCookieHeader(request, requestCookies);
        request.send(handler);
    }

    /**
     * Call CreateV1ProjectsTestMatricesByProjectId with Json body. 
     * 
     * @param projectId Parameter projectId inside path
     * @param uploadProtocol Parameter upload_protocol inside query
     * @param prettyPrint Parameter prettyPrint inside query
     * @param uploadType Parameter uploadType inside query
     * @param fields Parameter fields inside query
     * @param $Xgafv Parameter $.xgafv inside query
     * @param callback Parameter callback inside query
     * @param alt Parameter alt inside query
     * @param accessToken Parameter access_token inside query
     * @param key Parameter key inside query
     * @param quotaUser Parameter quotaUser inside query
     * @param pp Parameter pp inside query
     * @param oauthToken Parameter oauth_token inside query
     * @param bearerToken Parameter bearer_token inside query
     * @param requestId Parameter requestId inside query
     * @param body Json object or bean that represents the body of the request
     * @param handler The handler for the asynchronous request
     */
    fun CreateV1ProjectsTestMatricesByProjectIdWithJson(
        projectId: String?,
        uploadProtocol: String? ,
        prettyPrint: Boolean? ,
        uploadType: String? ,
        fields: String? ,
        $Xgafv: String? ,
        callback: String? ,
        alt: String? ,
        accessToken: String? ,
        key: String? ,
        quotaUser: String? ,
        pp: Boolean? ,
        oauthToken: String? ,
        bearerToken: String? ,
        requestId: String? ,
        body: Any?, handler: Handler<AsyncResult<HttpResponse<Buffer>>>) {
        // Check required params
        if (projectId == null) throw RuntimeException("Missing parameter projectId in path");
        

        // Generate the uri
        var uri: String = "/v1/projects/{projectId}/testMatrices"
        uri = uri.replaceAll("\\{{1}([.;?*+]*([^\\{\\}.;?*+]+)[^\\}]*)\\}{1}", "{$2}") //Remove * . ; ? from url template
        uri = uri.replace("{projectId}", this.renderPathParam("projectId", projectId));


        var request = client.post(uri)

        var requestCookies = MultiMap.caseInsensitiveMultiMap()
        if (uploadProtocol != null) this.addQueryParam("upload_protocol", uploadProtocol, request);
        if (prettyPrint != null) this.addQueryParam("prettyPrint", prettyPrint, request);
        if (uploadType != null) this.addQueryParam("uploadType", uploadType, request);
        if (fields != null) this.addQueryParam("fields", fields, request);
        if ($Xgafv != null) this.addQueryParam("$.xgafv", $Xgafv, request);
        if (callback != null) this.addQueryParam("callback", callback, request);
        if (alt != null) this.addQueryParam("alt", alt, request);
        if (accessToken != null) this.addQueryParam("access_token", accessToken, request);
        if (key != null) this.addQueryParam("key", key, request);
        if (quotaUser != null) this.addQueryParam("quotaUser", quotaUser, request);
        if (pp != null) this.addQueryParam("pp", pp, request);
        if (oauthToken != null) this.addQueryParam("oauth_token", oauthToken, request);
        if (bearerToken != null) this.addQueryParam("bearer_token", bearerToken, request);
        if (requestId != null) this.addQueryParam("requestId", requestId, request);
        this.addHeaderParam("Content-Type", "application/json", request);
        

        this.renderAndAttachCookieHeader(request, requestCookies);
        request.sendJson(body, handler);
    }


    

    // Parameters functions

    /**
     * Remove a cookie parameter from the cookie cache

     * @param paramName name of cookie parameter
     */
    fun removeCookie(paramName: String) {
        cookieParams.remove(paramName)
    }

    private fun addQueryParam(paramName: String, value: Any, request: HttpRequest<*>) {
        request.addQueryParam(paramName, value.toString())
    }

    /**
     * Add a cookie param in cookie cache

     * @param paramName name of cookie parameter
     * *
     * @param value value of cookie parameter
     */
    fun addCookieParam(paramName: String, value: Any) {
        renderCookieParam(paramName, value, cookieParams)
    }

    private fun addHeaderParam(headerName: String, value: Any, request: HttpRequest<*>) {
        request.putHeader(headerName, value.toString())
    }

    private fun renderPathParam(paramName: String, value: Any): String {
        return value.toString()
    }

    private fun renderCookieParam(paramName: String, value: Any, map: MultiMap) {
        map.remove(paramName)
        map.add(paramName, value.toString())
    }

    /**
     * Following this table to implement parameters serialization

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | style          | explode | in            | array                               | object                                 |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | matrix         | false   | path          | ;color=blue,black,brown             | ;color=R,100,G,200,B,150               |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | matrix         | true    | path          | ;color=blue;color=black;color=brown | ;R=100;G=200;B=150                     |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | label          | false   | path          | .blue.black.brown                   | .R.100.G.200.B.150                     |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | label          | true    | path          | .blue.black.brown                   | .R=100.G=200.B=150                     |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | form           | false   | query, cookie | color=blue,black,brown              | color=R,100,G,200,B,150                |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | form           | true    | query, cookie | color=blue&color=black&color=brown  | R=100&G=200&B=150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | simple         | false   | path, header  | blue,black,brown                    | R,100,G,200,B,150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | simple         | true    | path, header  | blue,black,brown                    | R=100,G=200,B=150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | spaceDelimited | false   | query         | blue%20black%20brown                | R%20100%20G%20200%20B%20150            |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | pipeDelimited  | false   | query         | blue|black|brown                    | R|100|G|200                            |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | deepObject     | true    | query         | n/a                                 | color[R]=100&color[G]=200&color[B]=150 |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     */

    /**
     * Render path value with matrix style exploded/not exploded

     * @param paramName
     * *
     * @param value
     * *
     * @return
     */
    private fun renderPathMatrix(paramName: String, value: Any): String {
        return ";" + paramName + "=" + value.toString()
    }

    /**
     * Render path array with matrix style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | matrix         | false   | path          | ;color=blue,black,brown             | ;color=R,100,G,200,B,150               |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @return
     */
    private fun renderPathArrayMatrix(paramName: String, values: List<Any>): String {
        val serialized = values.stream().map<String> { `object` -> encode(`object`.toString()) }.collect(Collectors.toList<String>()).joinToString(",")
        return ";$paramName=$serialized"
    }

    /**
     * Render path object with matrix style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | matrix         | false   | path          | ;color=blue,black,brown             | ;color=R,100,G,200,B,150               |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @return
     */
    private fun renderPathObjectMatrix(paramName: String, values: Map<String, Any>): String {
        val listToSerialize = ArrayList<String>()
        for ((key, value) in values.entrySet()) {
            listToSerialize.add(key)
            listToSerialize.add(encode(value.toString()).toString())
        }
        val serialized = listToSerialize.joinToString(",")
        return ";$paramName=$serialized"
    }

    /**
     * Render path array with matrix style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | matrix         | true    | path          | ;color=blue;color=black;color=brown | ;R=100;G=200;B=150                     |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @return
     */
    private fun renderPathArrayMatrixExplode(paramName: String, values: List<Any>): String {
        return values.stream().map { `object` -> ";" + paramName + "=" + encode(`object`.toString()) }.collect(Collectors.toList<String>()).joinToString("")
    }

    /**
     * Render path object with matrix style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | matrix         | true    | path          | ;color=blue;color=black;color=brown | ;R=100;G=200;B=150                     |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @return
     */
    private fun renderPathObjectMatrixExplode(paramName: String, values: Map<String, Any>): String {
        return values.entrySet().stream().map { entry -> ";" + entry.key + "=" + encode(entry.value.toString()) }.collect(Collectors.toList<String>()).joinToString("")
    }

    /**
     * Render path value with label style exploded/not exploded

     * @param paramName
     * *
     * @param value
     * *
     * @return
     */
    private fun renderPathLabel(paramName: String, value: Any): String {
        return "." + value.toString()
    }

    /**
     * Render path array with label style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | label          | false   | path          | .blue.black.brown                   | .R.100.G.200.B.150                     |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @return
     */
    private fun renderPathArrayLabel(paramName: String, values: List<Any>): String {
        return "." + values.stream().map<String> { `object` -> encode(`object`.toString()) }.collect(Collectors.toList<String>()).joinToString(".")
    }

    /**
     * Render path object with label style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | label          | false   | path          | .blue.black.brown                   | .R.100.G.200.B.150                     |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @return
     */
    private fun renderPathObjectLabel(paramName: String, values: Map<String, Any>): String {
        val listToSerialize = ArrayList<String>()
        for ((key, value) in values.entrySet()) {
            listToSerialize.add(key)
            listToSerialize.add(encode(value.toString()).toString())
        }
        return "." + listToSerialize.joinToString(".")
    }

    /**
     * Render path array with label style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | label          | true    | path          | .blue.black.brown                   | .R=100.G=200.B=150                     |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @return
     */
    private fun renderPathArrayLabelExplode(paramName: String, values: List<Any>): String {
        return renderPathArrayLabel(paramName, values)
    }

    /**
     * Render path object with label style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | label          | true    | path          | .blue.black.brown                   | .R=100.G=200.B=150                     |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @return
     */
    private fun renderPathObjectLabelExplode(paramName: String, values: Map<String, Any>): String {
        var result = ""
        for ((key, value) in values.entrySet())
            result = result + ("." + key + "=" + encode(value.toString()))
        return result
    }

    /**
     * Render path array with simple style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | simple         | false   | path, header  | blue,black,brown                    | R,100,G,200,B,150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @return
     */
    private fun renderPathArraySimple(paramName: String, values: List<Any>): String {
        return values.stream().map<String> { `object` -> encode(`object`.toString()) }.collect(Collectors.toList<String>()).joinToString(",")
    }

    /**
     * Render path object with simple style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | simple         | false   | path, header  | blue,black,brown                    | R,100,G,200,B,150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @return
     */
    private fun renderPathObjectSimple(paramName: String, values: Map<String, Any>): String {
        val listToSerialize = ArrayList<String>()
        for ((key, value) in values.entrySet()) {
            listToSerialize.add(key)
            listToSerialize.add(encode(value.toString()).toString())
        }
        return listToSerialize.joinToString(",")
    }

    /**
     * Render path array with simple style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | simple         | true    | path, header  | blue,black,brown                    | R=100,G=200,B=150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @return
     */
    private fun renderPathArraySimpleExplode(paramName: String, values: List<Any>): String {
        return renderPathArraySimple(paramName, values)
    }

    /**
     * Render path object with simple style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | simple         | true    | path, header  | blue,black,brown                    | R=100,G=200,B=150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @return
     */
    private fun renderPathObjectSimpleExplode(paramName: String, values: Map<String, Any>): String {
        return values.entrySet().stream().map { entry -> entry.key + "=" + encode(entry.value.toString()) }.collect(Collectors.toList<String>()).joinToString(",")
    }

    /**
     * Add query array with form style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | form           | false   | query, cookie | color=blue,black,brown              | color=R,100,G,200,B,150                |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addQueryArrayForm(paramName: String, values: List<Any>, request: HttpRequest<*>) {
        val serialized = values.stream().map { `object` -> `object`.toString() }.collect(Collectors.toList<String>()).joinToString(",")
        this.addQueryParam(paramName, serialized, request) // Encoding is done by WebClient
    }

    /**
     * Add query object with form style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | form           | false   | query, cookie | color=blue,black,brown              | color=R,100,G,200,B,150                |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addQueryObjectForm(paramName: String, values: Map<String, Any>, request: HttpRequest<*>) {
        val listToSerialize = ArrayList<String>()
        for ((key, value) in values.entrySet()) {
            listToSerialize.add(key)
            listToSerialize.add(value.toString())
        }
        val serialized = listToSerialize.joinToString(",")
        this.addQueryParam(paramName, serialized, request) // Encoding is done by WebClient
    }

    /**
     * Add cookie array with form style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | form           | false   | query, cookie | color=blue,black,brown              | color=R,100,G,200,B,150                |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     */
    private fun renderCookieArrayForm(paramName: String, values: List<Any>, map: MultiMap) {
        val value = values.stream().map { `object` -> `object`.toString() }.collect(Collectors.toList<String>()).joinToString(",")
        map.remove(paramName)
        map.add(paramName, value)
    }

    /**
     * Add a cookie array parameter in cookie cache

     * @param paramName name of cookie parameter
     * *
     * @param values list of values of cookie parameter
     */
    fun addCookieArrayForm(paramName: String, values: List<Any>) {
        renderCookieArrayForm(paramName, values, cookieParams)
    }

    /**
     * Add cookie object with form style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | form           | false   | query, cookie | color=blue,black,brown              | color=R,100,G,200,B,150                |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     */
    private fun renderCookieObjectForm(paramName: String, values: Map<String, Any>, map: MultiMap) {
        val listToSerialize = ArrayList<String>()
        for ((key, value) in values.entrySet()) {
            listToSerialize.add(key)
            listToSerialize.add(value.toString())
        }
        val value = listToSerialize.joinToString(",")
        map.remove(paramName)
        map.add(paramName, value)
    }

    /**
     * Add a cookie object parameter in cookie cache

     * @param paramName name of cookie parameter
     * *
     * @param values map of values of cookie parameter
     */
    fun addCookieObjectForm(paramName: String, values: Map<String, Any>) {
        renderCookieObjectForm(paramName, values, cookieParams)
    }

    /**
     * Add query array with form style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | form           | true    | query, cookie | color=blue&color=black&color=brown  | R=100&G=200&B=150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addQueryArrayFormExplode(paramName: String, values: List<Any>, request: HttpRequest<*>) {
        for (value in values)
            this.addQueryParam(paramName, value.toString(), request)
    }

    /**
     * Add query object with form style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | form           | true    | query, cookie | color=blue&color=black&color=brown  | R=100&G=200&B=150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addQueryObjectFormExplode(paramName: String, values: Map<String, Any>, request: HttpRequest<*>) {
        for ((key, value) in values.entrySet())
            this.addQueryParam(key, value.toString(), request)
    }

    /**
     * Add cookie array with form style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | form           | true    | query, cookie | color=blue&color=black&color=brown  | R=100&G=200&B=150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     */
    private fun renderCookieArrayFormExplode(paramName: String, values: List<Any>, map: MultiMap) {
        map.remove(paramName)
        for (value in values)
            map.add(paramName, value.toString())
    }

    fun addCookieArrayFormExplode(paramName: String, values: List<Any>) {
        renderCookieArrayFormExplode(paramName, values, cookieParams)
    }

    /**
     * Add cookie object with form style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | form           | true    | query, cookie | color=blue&color=black&color=brown  | R=100&G=200&B=150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     */
    private fun renderCookieObjectFormExplode(paramName: String, values: Map<String, Any>, map: MultiMap) {
        for ((key, value) in values.entrySet()) {
            map.remove(key)
            map.add(key, value.toString())
        }
    }

    fun addCookieObjectFormExplode(paramName: String, values: Map<String, Any>) {
        renderCookieObjectFormExplode(paramName, values, cookieParams)
    }

    /**
     * Add header array with simple style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | simple         | false   | path, header  | blue,black,brown                    | R,100,G,200,B,150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param headerName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addHeaderArraySimple(headerName: String, values: List<Any>, request: HttpRequest<*>) {
        val serialized = values.stream().map { `object` -> `object`.toString() }.collect(Collectors.toList<String>()).joinToString(",")
        this.addHeaderParam(headerName, serialized, request)
    }

    /**
     * Add header object with simple style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | simple         | false   | path, header  | blue,black,brown                    | R,100,G,200,B,150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param headerName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addHeaderObjectSimple(headerName: String, values: Map<String, Any>, request: HttpRequest<*>) {
        val listToSerialize = ArrayList<String>()
        for ((key, value) in values.entrySet()) {
            listToSerialize.add(key)
            listToSerialize.add(value.toString())
        }
        val serialized = listToSerialize.joinToString(",")
        this.addHeaderParam(headerName, serialized, request)
    }

    /**
     * Add header array with simple style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | simple         | true    | path, header  | blue,black,brown                    | R=100,G=200,B=150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param headerName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addHeaderArraySimpleExplode(headerName: String, values: List<Any>, request: HttpRequest<*>) {
        this.addHeaderArraySimple(headerName, values, request)
    }

    /**
     * Add header object with simple style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | simple         | true    | path, header  | blue,black,brown                    | R=100,G=200,B=150                      |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param headerName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addHeaderObjectSimpleExplode(headerName: String, values: Map<String, Any>, request: HttpRequest<*>) {
        val listToSerialize = ArrayList<String>()
        for ((key, value) in values.entrySet()) {
            listToSerialize.add(key + "=" + value.toString())
        }
        val serialized = listToSerialize.joinToString(",")
        this.addHeaderParam(headerName, serialized, request)
    }

    /**
     * Add query array with spaceDelimited style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | spaceDelimited | false   | query         | blue%20black%20brown                | R%20100%20G%20200%20B%20150            |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addQueryArraySpaceDelimited(paramName: String, values: List<Any>, request: HttpRequest<*>) {
        val serialized = values.stream().map { `object` -> `object`.toString() }.collect(Collectors.toList<String>()).joinToString(" ")
        this.addQueryParam(paramName, serialized, request) // Encoding is done by WebClient
    }

    /**
     * Add query object with spaceDelimited style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | spaceDelimited | false   | query         | blue%20black%20brown                | R%20100%20G%20200%20B%20150            |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addQueryObjectSpaceDelimited(paramName: String, values: Map<String, Any>, request: HttpRequest<*>) {
        val listToSerialize = ArrayList<String>()
        for ((key, value) in values.entrySet()) {
            listToSerialize.add(key)
            listToSerialize.add(value.toString())
        }
        val serialized = listToSerialize.joinToString(" ")
        this.addQueryParam(paramName, serialized, request) // Encoding is done by WebClient
    }

    /**
     * Add query array with pipeDelimited style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | pipeDelimited  | false   | query         | blue|black|brown                    | R|100|G|200                            |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addQueryArrayPipeDelimited(paramName: String, values: List<Any>, request: HttpRequest<*>) {
        val serialized = values.stream().map { `object` -> `object`.toString() }.collect(Collectors.toList<String>()).joinToString("|")
        this.addQueryParam(paramName, serialized, request) // Encoding is done by WebClient
    }

    /**
     * Add query object with pipeDelimited style and not exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | pipeDelimited  | false   | query         | blue|black|brown                    | R|100|G|200                            |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addQueryObjectPipeDelimited(paramName: String, values: Map<String, Any>, request: HttpRequest<*>) {
        val listToSerialize = ArrayList<String>()
        for ((key, value) in values.entrySet()) {
            listToSerialize.add(key)
            listToSerialize.add(value.toString())
        }
        val serialized = listToSerialize.joinToString("|")
        this.addQueryParam(paramName, serialized, request) // Encoding is done by WebClient
    }

    /**
     * Add query object with deepObject style and exploded

     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+
     * | deepObject     | true    | query         | n/a                                 | color[R]=100&color[G]=200&color[B]=150 |
     * +----------------+---------+---------------+-------------------------------------+----------------------------------------+

     * @param paramName
     * *
     * @param values
     * *
     * @param request
     */
    private fun addQueryObjectDeepObjectExplode(paramName: String, values: Map<String, Any>, request: HttpRequest<*>) {
        for ((key, value) in values.entrySet()) {
            this.addQueryParam("$paramName[$key]", value.toString(), request)
        }
    }


    private fun renderAndAttachCookieHeader(request: HttpRequest<*>, otherCookies: MultiMap?) {
        if ((otherCookies == null || otherCookies.isEmpty()) && cookieParams.isEmpty())
            return;
        val listToSerialize = ArrayList<String>()
        for ((key, value) in cookieParams.entries()) {
            if (otherCookies != null && !otherCookies.contains(key)) {
                try {
                    listToSerialize.add(encode(key).toString() + "=" + encode(value).toString())
                } catch (e1: UnsupportedEncodingException) {}
            }
        }
        if (otherCookies != null) {
            for ((key, value) in otherCookies.entries()) {
                try {
                    listToSerialize.add(encode(key).toString() + "=" + encode(value).toString())
                } catch (e1: UnsupportedEncodingException) {}
            }
        }
        request.putHeader("Cookie", listToSerialize.joinToString("; "))
    }

    // Other functions

    private fun encode(s: String): String? {
        try {
            return URLEncoder.encode(s, "UTF-8")
        } catch (e: Exception) {
            return null
        }

    }

    /**
     * Close the connection with server

     */
    fun close() {
        client!!.close()
    }

}
