package cubox.aero.skapi.controller;


import cubox.aero.skapi.axis.AxisSock;
import cubox.aero.skapi.axis.axFid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/test")
public class TestController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private AxisSock axisApi;

    @Value("${tr.server.ip}")
    private String trServerIp;

    @Value("${tr.server.port}")
    private int trServerPort;

    @PostMapping("/trTest")
    public String trTest() {

        // 테스트 데이터
        // "SIS15020" 0 0x005930, 7

        byte[] bytes = null;

        String resultStr = "success";

        String trCode = "SIS15020";
        byte[] acData = "005930".getBytes();

        Map<String, Object> trMap = new HashMap<>();

        try {
            trMap = doTrRecv(trCode, acData, 7);
        } catch (Exception e) {
            e.printStackTrace();
        }

        bytes = (byte[]) trMap.get("rcvDt");

        logger.info("tr transaction test : {}", bytes);

        if(bytes == null){
            resultStr = "fail";
        }

        return resultStr;
    }

    private Map<String, Object> doTrRecv(String trCode, byte[] acData, int trLength) {


        // fid 편집을 위한 class
        axFid axfid = null;
        // 서버 조회를 위한 class
        byte[] m_recvData = null;

        // 실제 tr 송/수신 부분
        // 접속 서버 ip, port 세팅
        // 테스트를 위한 테스트 서버 공인아이피, 포트
        // 전용선 작업이후 새로운 아이피,포트 제공 예정
        axisApi.setInfo(trServerIp, trServerPort);


        // fid 조회 인 경우 세팅 필요, 일반 tr 0
        // 응답전문 m_recvData에 있음
        m_recvData = axisApi.AxisCall(trCode, 0, acData, trLength);
        // 사용예, 서버시간 조회
        //m_recvData = m_axSock.AxisCall("10612", 0, " ", 1);

        // error code get
        byte[] errcod = axisApi.getErrCod();
        // error message get
        byte[] errmsg = axisApi.getErrMsg();

        logger.info("errcod : {}", errcod);
        logger.info("errmsg : {}", errmsg);

        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("errCd", errcod);
        resultMap.put("errMsg", errmsg);

        resultMap.put("rcvDt", m_recvData);

        return resultMap;
    }
}



