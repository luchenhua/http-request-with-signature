import org.apache.commons.lang3.RandomStringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class RestUtil {

    public String simplePost(String url, String param) throws Exception {

        // 测试用Key
        String encryptKey = "";
        String userKey = "";
        String secretKey = "";

        String nonce = generateNonce();
        String callTime = generateCallTime();
        String sig = generateSignature(userKey, secretKey, nonce, callTime);

        Map<String, Object> params = new HashMap<>();
        params.put("user_key", userKey);
        params.put("param", DesUtil.encode(encryptKey, param));
        params.put("nonce", nonce);
        params.put("call_time", callTime);
        params.put("sig", sig);

        return new HttpUtil().postSimple(url, params);
    }

    public boolean isValidRequest(String faUserKey, String nonce, String callTime, String sig) {

        // 测试用Key
        String secretKey = "";

        return sig.equals(generateSignature(faUserKey, secretKey, nonce, callTime));
    }

    private String generateNonce() {

        return RandomStringUtils.randomAlphanumeric(20);
    }

    private String generateCallTime() {

        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS"));
    }

    private String generateSignature(String userKey, String secretKey, String nonce, String callTime) {

        return PasswordUtil.md5Encode(userKey + callTime + nonce + secretKey);
    }
}
