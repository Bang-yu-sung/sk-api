package cubox.aero.skapi.service;

import cubox.aero.skapi.type.HttpType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static cubox.aero.skapi.type.FaceApiType.*;
import static cubox.aero.skapi.type.HttpType.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class FaceApiService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${frs.server.url}")
    private String frsServerUrl;


    public String addFace(String faceId, String jsonImage) throws Exception {
        HashMap<String, String> faceInfo = new HashMap<>();
        faceInfo.put("faceId", faceId);
        faceInfo.put("image", jsonImage);
        return getFaceApiResult(ADD_FACE_URL.getValue(), faceInfo, POST);
    }

    public String delFace(String faceId) throws Exception {
        HashMap<String, String> faceInfo = new HashMap<>();
        faceInfo.put("faceId", faceId);
        return getFaceApiResult(DEL_FACE_URL.getValue(), faceInfo, DELETE);
    }

    public String matchByFaceId(String faceId, String jsonImage) throws Exception {
        HashMap<String, String> faceInfo = new HashMap<>();
        faceInfo.put("faceId", faceId);
        faceInfo.put("image", jsonImage);
        String url = IDENTIFICATION_URL.getValue() + "/" + faceId + "/image-json";
        return getFaceApiResult(url, faceInfo, POST);
    }

    public String getScore(String image1, String image2) throws Exception {
        HashMap<String, String> faceInfo = new HashMap<>();
        faceInfo.put("image1", image1);
        faceInfo.put("image2", image2);
        return getFaceApiResult(SCORE_URL.getValue(), faceInfo, POST);
    }

//    public String getFeature(String image) throws Exception {
//        HashMap<String, String> faceInfo = new HashMap<>();
//        faceInfo.put("image", image);
//        return getFaceApiResult(FEATURE_URL.getValue(), faceInfo, POST);
//    }

//    public String getFeatureScore(String feature1, String feature2) throws Exception {
//        HashMap<String, String> faceInfo = new HashMap<>();
//        faceInfo.put("feature1", feature1);
//        faceInfo.put("feature2", feature2);
//        return getFaceApiResult(FEATURE_SCORE_URL.getValue(), faceInfo, POST);
//    }

    public String reDirect(String param, String token, String frsApiUrl) throws Exception {

        String reqUrl = frsServerUrl + "/v1/" + frsApiUrl;

        logger.info("reqUrl : {}", reqUrl);

        return sendPost(reqUrl, token, param);
    }

    private String getFaceApiResult(String funcUrl, HashMap<String, String> param, HttpType type) throws Exception {
        String reqUrl = FACE_API_URL.getValue() + funcUrl;
        String result;
        switch (type) {
            case GET:
                result = sendGet(reqUrl);
                break;
            case POST:
                // result = sendPost(reqUrl, new JSONObject(param).toJSONString());
                result = "";
                break;
            case DELETE:
                result = sendDelete(reqUrl + "/" + param.get("faceId"));
                break;
            default:
                result = "Invalid Request Method Type";
                break;
        }
        return result;
    }

    private String sendPost(String url, String token, String params) throws Exception {
        log.info("\nSending 'POST' request to URL : " + url);
        log.info("\nSending 'POST' request params : " + params);
        HttpURLConnection con = getConnectionSetDefaultHeader(url, POST);
        con.setRequestProperty("Accept", "application/json");
        con.setRequestProperty("Content-Type", "application/json");
        con.setRequestProperty("X-Api-Key", "CUFRSDEV-A01B-C23D-45EF-6G7H89I0123J");
        if (token != null) {
            con.setRequestProperty("Authorization", token);
        }

        con.setDoOutput(true); // Send post request, 항상 갱신된내용을 가져옴.
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(params);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        log.info("Response Code : " + responseCode);
        String responseMsg = con.getResponseMessage();
        log.info("Response Message : " + responseMsg);

        if (responseCode == 412) throw new Exception("noMatch");
        return responseToString(con);
    }

    private String sendGet(String url) throws Exception {
        log.info("\nSending 'GET' request to URL : " + url);
        HttpURLConnection con = getConnectionSetDefaultHeader(url, GET);
        int responseCode = con.getResponseCode();
        log.info("Response Code : " + responseCode);
        return responseToString(con);
    }

    private String sendDelete(String url) throws Exception {
        log.info("\nSending 'DELETE' request to URL : " + url);
        HttpURLConnection con = getConnectionSetDefaultHeader(url, DELETE);
        con.setRequestProperty("x-api-key", X_API_KEY.getValue());
        int responseCode = con.getResponseCode();
        log.info("Response Code : " + responseCode);
        return responseToString(con);
    }

    private HttpURLConnection getConnectionSetDefaultHeader(String url, HttpType type) throws IOException {
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod(String.valueOf(type));
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json");
        con.setConnectTimeout(10000); //컨텍션타임아웃 10초
        con.setReadTimeout(5000); //컨텐츠조회 타임아웃 5총
        return con;
    }

    private String responseToString(HttpURLConnection con) throws IOException {
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
        String inputLine;
        StringBuilder response = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        return response.toString();
    }
}