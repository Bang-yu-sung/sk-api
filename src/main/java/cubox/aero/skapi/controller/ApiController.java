package cubox.aero.skapi.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import cubox.aero.skapi.service.ApiService;
import org.apache.commons.io.IOUtils;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1")
public class ApiController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final int readByteSize = 256;
    private static final String charset = "UTF-8";
    private static final String CRLF = "\r\n";

    private PrintWriter writer = null;
    private OutputStream output = null;

    @Value("${frs.server.url}")
    private String frsServerUrl;

    @Value("${user.image.location}")
    private String userImageLocation;


    @Value("${user.image.type}")
    private String userImageType;


    @Autowired
    private ApiService apiService;

    @PostMapping("/{level1}")
    public String frsRedirect(HttpServletRequest request, @RequestBody HashMap<String, Object> map, @PathVariable("level1") String level1) throws Exception {

        logger.info("requestbody : {}", map);

        String token = request.getHeader("Authorization");
        logger.info("token : {}", token);


        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);

        json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);

        String result = "";

        return apiService.frsReDirect(json, token, level1);
    }

    @PostMapping(value = "/{level1}/{level2}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public String frsRedirect2(HttpServletRequest request, @RequestBody HashMap<String, Object> map, @PathVariable("level1") String level1, @PathVariable("level2") String level2) throws Exception {

        logger.info("requestbody : {}", map);

        String token = request.getHeader("Authorization");
        logger.info("token : {}", token);


        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(map);

        json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);

        return apiService.frsReDirect(json, token, level1, level2);
    }

    @PostMapping(value = "/{level1}/{level2}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String frsRedirect2Multipart(HttpServletRequest request, @PathVariable("level1") String level1, @PathVariable("level2") String level2
            , @RequestPart(value = "image1", required = false) MultipartFile image1, @RequestPart("image2") MultipartFile image2) throws Exception {

        logger.info("level1 : {}", level1);
        logger.info("level2 : {}", level2);

        String result = "";

        Map<String, Object> map = new HashMap<String, Object>();

        String token = request.getHeader("Authorization");
        logger.info("token : {}", token);

        if (level2.equals("tamper-history")) {


            String boundary = this.setBoundary(); //boundary정의

            String frsUrl = frsServerUrl + "/v1/" + level1 + "/" + level2;

            URL url = new URL(frsUrl);

            HttpURLConnection connection;
            if (frsUrl.startsWith("https")) {
                connection = (HttpsURLConnection) url.openConnection();
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            try {
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setRequestProperty("Accept", "multipart/form-data; boundary=" + boundary);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("X-Api-Key", "CUFRSDEV-A01B-C23D-45EF-6G7H89I0123J");
                if (token != null) {
                    connection.setRequestProperty("Authorization", token);
                }
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setConnectTimeout(10000);

                output = connection.getOutputStream();
                writer = new PrintWriter(new OutputStreamWriter(output, charset), true);

                addString(boundary, "LivenessType", request.getParameter("LivenessType"));
                addString(boundary, "ErrorCode", request.getParameter("ErrorCode"));
                addString(boundary, "ErrorValue", request.getParameter("ErrorValue"));

                addString(boundary, "Score1", request.getParameter("Score1"));
                addString(boundary, "Score2", request.getParameter("Score2"));
                addString(boundary, "Score3", request.getParameter("Score3"));
                addString(boundary, "Score4", request.getParameter("Score4"));
                addString(boundary, "Threshold1", request.getParameter("Threshold1"));
                addString(boundary, "Threshold2", request.getParameter("Threshold2"));
                addString(boundary, "Threshold3", request.getParameter("Threshold3"));
                addString(boundary, "Threshold4", request.getParameter("Threshold4"));

                //전송
                addFile(boundary, "image1", image1);
                addFile(boundary, "image2", image2);
                addEnd(boundary);

                // Request is lazily fired whenever you need to obtain information about response.
                int httpStatus = connection.getResponseCode();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                System.out.println("Response: " + sb.toString());

                result = sb.toString();

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                writer.close();
                output.close();
                connection.disconnect();
            }


        } else if (level2.equals("image-form")) {

            String boundary = this.setBoundary(); //boundary정의

            String frsUrl = frsServerUrl + "/v1/" + level1 + "/" + level2;

            URL url = new URL(frsUrl);

            HttpURLConnection connection;
            if (frsUrl.startsWith("https")) {
                connection = (HttpsURLConnection) url.openConnection();
            } else {
                connection = (HttpURLConnection) url.openConnection();
            }

            try {
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
                connection.setRequestProperty("Accept", "multipart/form-data; boundary=" + boundary);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("X-Api-Key", "CUFRSDEV-A01B-C23D-45EF-6G7H89I0123J");
                if (token != null) {
                    connection.setRequestProperty("Authorization", token);
                }
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setConnectTimeout(10000);

                output = connection.getOutputStream();
                writer = new PrintWriter(new OutputStreamWriter(output, charset), true);


                logger.info("userId : {}", request.getParameter("userId"));

                // addString(boundary, "userId", request.getParameter("userId"));
                addString(boundary, "ThresholdType", request.getParameter("ThresholdType"));

                if (userImageType.equals("NAS")) {

                    //SSL 무시하기
                    TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() {
                            return null;
                        }

                        public void checkClientTrusted(X509Certificate[] certs, String authType) {
                        }

                        public void checkServerTrusted(X509Certificate[] certs, String authType) {
                        }
                    }};

                    SSLContext sc = SSLContext.getInstance("SSL");
                    sc.init(null, trustAllCerts, new java.security.SecureRandom());
                    HttpsURLConnection
                            .setDefaultSSLSocketFactory(sc.getSocketFactory());
                    //end

                    String userIdUrl = "https://demo.cubox.aero:6880/demoApps/" + request.getParameter("userId");

                    logger.info("userIdUrl : {}", userIdUrl);

                    URL imageUrl = new URL(userIdUrl);

                    byte[] bytes = IOUtils.toByteArray(imageUrl);

                    logger.info("byte length : {}", bytes.length);

                    // 암호화
                    String AES_Vec = "dev0frs123cubox4";
                    String AES_Key = "A?C(H+MbQe1324Yq3t6w9z&C&F)J@NcR";

                    SecretKeySpec keySpec = new SecretKeySpec(AES_Key.getBytes(), "AES");
                    IvParameterSpec ivParamSpec = new IvParameterSpec(AES_Vec.getBytes());
                    Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                    c.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
                    byte[] encVal = c.doFinal(bytes);

                    //전송
                    addFile(boundary, "image1", encVal);

                } else if (userImageType.equals("NFS")) {

                    // NFS 에 신분증을 마운트시켜놓았을경우
                    // 저장된 위치에서 사진을 긁어와 암호화 후 전송한다.

                    File file = new File(userImageLocation + request.getParameter("userId"));

                    logger.info("file exists : {}", file.exists());
                    logger.info("file length : {}", file.length());

                    // 암호화
                    String AES_Vec = "dev0frs123cubox4";
                    String AES_Key = "A?C(H+MbQe1324Yq3t6w9z&C&F)J@NcR";

                    SecretKeySpec keySpec = new SecretKeySpec(AES_Key.getBytes(), "AES");
                    IvParameterSpec ivParamSpec = new IvParameterSpec(AES_Vec.getBytes());
                    Cipher c = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                    c.init(Cipher.ENCRYPT_MODE, keySpec, ivParamSpec);
                    byte[] encVal = c.doFinal(Files.readAllBytes(file.toPath()));

                    //전송
                    addFile(boundary, "image1", encVal);


                }

                addFile(boundary, "image2", image2);
                addEnd(boundary);

                // Request is lazily fired whenever you need to obtain information about response.
                int httpStatus = connection.getResponseCode();
                logger.info("response httpStatus : {}", httpStatus);
                if (httpStatus == 200) {

                    BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), charset));
                    StringBuilder sb = new StringBuilder();
                    String line = "";
                    while ((line = br.readLine()) != null) {
                        sb.append(line);
                    }
                    System.out.println("Response: " + sb.toString());

                    result = sb.toString();

                } else {

                    JSONObject resultJson = new JSONObject();
                    resultJson.put("errorCode", httpStatus);

                    if (httpStatus == 400) {
                        resultJson.put("errorMsg", "Error occurred");
                    } else if (httpStatus == 401) {
                        resultJson.put("errorMsg", "Invalid API key");
                    } else if (httpStatus == 403) {
                        resultJson.put("errorMsg", "Invalid Device");
                    } else if (httpStatus == 412) {
                        resultJson.put("errorMsg", "No match");
                    } else if (httpStatus == 423) {
                        resultJson.put("errorMsg", "Not approved Device");
                    } else {
                        resultJson.put("errorMsg", "Unknown error");
                    }

                    result = resultJson.toJSONString();

                }


            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                writer.close();
                output.close();
                connection.disconnect();
            }
        }


        return result;

    }

    /**********************************************************************
     * 통신 관련
     **********************************************************************/
    //바운더리 셋팅
    private String setBoundary() {
        String boundaryTime = Long.toHexString(System.currentTimeMillis()); // Just generate some unique random value.
        String boundary = boundaryTime;
        return boundary;
    }

    //스트링 추가
    private void addString(String boundary, String _key, String _value) {// Send normal String
        StringBuilder sb = new StringBuilder();
        sb.append("--" + boundary).append(CRLF);
        sb.append("Content-Disposition: form-data; name=\"" + _key + "\"").append(CRLF);
        sb.append("Content-Type: text/plain; charset=" + charset).append(CRLF);
        sb.append(CRLF).append(_value).append(CRLF);

        writer.append(sb).flush();
    }

    //파일 추가
    private void addFile(String boundary, String _key, MultipartFile _file) throws IOException {// Send File
        StringBuilder sb = new StringBuilder();
        sb.append("--" + boundary).append(CRLF);
        sb.append("Content-Disposition: form-data; name=\"" + _key + "\"; filename=\"" + _file.getName() + "\"").append(CRLF);
        sb.append("Content-Type: " + URLConnection.guessContentTypeFromName(_file.getName())).append(CRLF); // Text file itself must be saved in this charset!
        sb.append("Content-Transfer-Encoding: binary").append(CRLF);
        sb.append(CRLF);
        writer.append(sb).flush();


        FileInputStream inputStream = (FileInputStream) _file.getInputStream();
        byte[] buffer = new byte[(int) _file.getSize()];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        inputStream.close();

        writer.append(CRLF).flush();
    }

    private void addFile(String boundary, String _key, File _file) throws IOException {// Send File
        StringBuilder sb = new StringBuilder();
        sb.append("--" + boundary).append(CRLF);
        sb.append("Content-Disposition: form-data; name=\"" + _key + "\"; filename=\"" + _file.getName() + "\"").append(CRLF);
        sb.append("Content-Type: " + URLConnection.guessContentTypeFromName(_file.getName())).append(CRLF); // Text file itself must be saved in this charset!
        sb.append("Content-Transfer-Encoding: binary").append(CRLF);
        sb.append(CRLF);
        writer.append(sb).flush();


        FileInputStream inputStream = new FileInputStream(_file);
        byte[] buffer = new byte[(int) _file.length()];
        int bytesRead = -1;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        output.flush();
        inputStream.close();

        writer.append(CRLF).flush();
    }

    private void addFile(String boundary, String _key, byte[] _file) throws IOException {// Send File
        StringBuilder sb = new StringBuilder();
        sb.append("--" + boundary).append(CRLF);
        sb.append("Content-Disposition: form-data; name=\"" + _key + "\"; filename=\"" + "image1" + "\"").append(CRLF);
        sb.append("Content-Type: " + URLConnection.guessContentTypeFromName("image1")).append(CRLF); // Text file itself must be saved in this charset!
        sb.append("Content-Transfer-Encoding: binary").append(CRLF);
        sb.append(CRLF);
        writer.append(sb).flush();


        output.write(_file);
        output.flush();

        writer.append(CRLF).flush();
    }

    //전송처리 끝
    private void addEnd(String boundary) {//End of multipart/form-data.
        StringBuilder sb = new StringBuilder();
        sb.append("--").append(boundary).append("--").append(CRLF);
        writer.append(sb).flush();
    }


//    @PostMapping("/image-form")
//    public JSONObject imageForm(@RequestParam("imageUrl") String url,
//                                @RequestPart("image") List<MultipartFile> iList) {
//
//        logger.info("imageUrl : {}", url);
//        MultipartFile image = iList.get(0);
//
//
//        try {
//
//            URL imageUrl = new URL(url);
//            BufferedImage urlImage = ImageIO.read(imageUrl);
//
//            ByteArrayOutputStream baos = new ByteArrayOutputStream();
//            ImageIO.write(urlImage, "jpg", baos);
//            byte[] bytes = baos.toByteArray();
//
//            apiService.match(bytes, image);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//
//        JSONObject json = new JSONObject();
//        return json;
//    }

}



