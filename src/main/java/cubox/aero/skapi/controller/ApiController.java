package cubox.aero.skapi.controller;


import cubox.aero.skapi.service.ApiService;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ApiService apiService;

    /**
     * 동행인이 동행을 시작할시 Delivery 서비스를 시작할 세션을 생성하는데 사용될 id를 발급합니다.
     *
     * @return
     */
    @GetMapping("/create")
    public Map<String, Object> create() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String obsId = sdf.format(date) + "-" + UUID.randomUUID();
        Map<String, Object> obs = new HashMap<>();
        obs.put("id", obsId);
        return obs;
    }

    @PostMapping("/postTest")
    public JSONObject create(@RequestParam("param") String param) {
        logger.info("param : {}", param);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = new Date();
        String obsId = sdf.format(date) + "-" + UUID.randomUUID();
        JSONObject obs = new JSONObject();
        obs.put("id", obsId);
        return obs;
    }


    @PostMapping("/image-form")
    public JSONObject imageForm(@RequestParam("imageUrl") String url,
                                @RequestPart("image") List<MultipartFile> iList) {

        logger.info("imageUrl : {}", url);
        MultipartFile image = iList.get(0);


        try {

            URL imageUrl = new URL(url);
            BufferedImage urlImage = ImageIO.read(imageUrl);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(urlImage, "jpg", baos);
            byte[] bytes = baos.toByteArray();

            apiService.match(bytes, image);

        } catch (Exception e) {
            e.printStackTrace();
        }


        JSONObject json = new JSONObject();
        return json;
    }

}



