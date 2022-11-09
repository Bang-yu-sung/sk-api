package cubox.aero.skapi.service;

import cubox.aero.skapi.util.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiService {

    private final EncryptionUtil encryptionUtil;
    private final FaceApiService faceApiService;


    public JSONObject addUser(String faceId, MultipartFile file) {
        String resMsg;
        boolean result = true;


        try {

            log.info("fileName : {}", file.getName());
            log.info("faceId : {}", faceId);

            String base64data = Base64.getEncoder().encodeToString(file.getBytes());
            resMsg = faceApiService.addFace(faceId, encryptionUtil.encryptAES256(base64data));

            if (faceId.length() != 8) {
                throw new Exception("올바른 faceId가 아닙니다.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            result = false;
            resMsg = e.getMessage();
        }

        return setResponse(resMsg, result);
    }

    public JSONObject delUser(String faceId) {
        String resMsg;
        boolean result = true;
        try {
            log.info("faceId : {}", faceId);
            resMsg = faceApiService.delFace(faceId);
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
            resMsg = e.getMessage();
        }

        return setResponse(resMsg, result);
    }

    public JSONObject matchUserByFaceId(String faceId, MultipartFile file) {
        String resMsg;
        boolean result = true;
        try {

            log.info("fileName : {}", file.getName());
            log.info("faceId : {}", faceId);

            String base64data = Base64.getEncoder().encodeToString(file.getBytes());
            resMsg = faceApiService.matchByFaceId(faceId, encryptionUtil.encryptAES256(base64data));
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
            resMsg = e.getMessage();
        }

        return setResponse(resMsg, result);
    }

    public JSONObject match(MultipartFile file1, MultipartFile file2) throws Exception {

        String resMsg;
        boolean result = true;

        try {

            log.info("fileName1 : {}", file1.getName());
            log.info("fileName2 : {}", file2.getName());

            String base64data1 = Base64.getEncoder().encodeToString(file1.getBytes());
            String base64data2 = Base64.getEncoder().encodeToString(file2.getBytes());
            resMsg = faceApiService.getScore(
                    encryptionUtil.encryptAES256(base64data1),
                    encryptionUtil.encryptAES256(base64data2));
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
            resMsg = e.getMessage();
        }

        return setResponse(resMsg, result);
    }

    public JSONObject match(byte[] file1, MultipartFile file2) throws Exception {

        String resMsg;
        boolean result = true;

        try {

            String base64data1 = Base64.getEncoder().encodeToString(file1);
            String base64data2 = Base64.getEncoder().encodeToString(file2.getBytes());
            resMsg = faceApiService.getScore(
                    encryptionUtil.encryptAES256(base64data1),
                    encryptionUtil.encryptAES256(base64data2));
        } catch (Exception e) {
            e.printStackTrace();
            result = false;
            resMsg = e.getMessage();
        }

        return setResponse(resMsg, result);
    }

    /**
     * Response 셋팅
     */
    private JSONObject setResponse(String msg, boolean result) {

        if (!result && msg.equals("noMatch")) {
            // noMatch 일 경우는 ok, result: false, msg: 얼굴일치하지않음
            msg = "얼굴이 일치하지 않습니다.";
        }

        JSONObject response = new JSONObject();
        response.put("msg", msg);
        response.put("result", result);

        return response;
    }
}
