package com.peachtree.config;

import com.wechat.pay.java.core.Config;
import com.wechat.pay.java.core.RSAAutoCertificateConfig;
import com.wechat.pay.java.core.notification.NotificationConfig;
import com.wechat.pay.java.core.notification.NotificationParser;
import com.wechat.pay.java.service.payments.jsapi.JsapiService;
import com.wechat.pay.java.service.payments.nativepay.NativePayService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@Data
@Configuration
@ConfigurationProperties(prefix = "wechat.pay")
public class WeChatPayConfig {
    
    private String appId;
    private String mchId;
    private String apiV3Key;
    private String privateKeyPath;
    private String merchantSerialNumber;
    private String notifyUrl;
    private boolean enabled = false; // 默认禁用
    
    /**
     * 获取微信支付配置
     */
    @Bean
    @ConditionalOnProperty(prefix = "wechat.pay", name = "enabled", havingValue = "true")
    public Config wechatPayConfig() {
        try {
            log.info("初始化微信支付配置...");
            // 读取商户私钥
            String privateKey = loadPrivateKey();
            
            // 使用自动更新证书的配置
            return new RSAAutoCertificateConfig.Builder()
                    .merchantId(mchId)
                    .privateKey(privateKey)
                    .merchantSerialNumber(merchantSerialNumber)
                    .apiV3Key(apiV3Key)
                    .build();
        } catch (Exception e) {
            log.error("微信支付配置初始化失败", e);
            throw new RuntimeException("微信支付配置初始化失败", e);
        }
    }
    
    /**
     * 通知解析器（用于解析支付回调）
     */
    @Bean
    @ConditionalOnProperty(prefix = "wechat.pay", name = "enabled", havingValue = "true")
    public NotificationParser notificationParser(Config config) {
        return new NotificationParser((NotificationConfig) config);
    }
    
    /**
     * JSAPI支付服务（公众号/小程序支付）
     */
    @Bean
    @ConditionalOnProperty(prefix = "wechat.pay", name = "enabled", havingValue = "true")
    public JsapiService jsapiService(Config config) {
        return new JsapiService.Builder().config(config).build();
    }
    
    /**
     * Native支付服务（扫码支付）
     */
    @Bean
    @ConditionalOnProperty(prefix = "wechat.pay", name = "enabled", havingValue = "true")
    public NativePayService nativePayService(Config config) {
        return new NativePayService.Builder().config(config).build();
    }
    
    /**
     * 加载商户私钥
     */
    private String loadPrivateKey() throws IOException {
        if (privateKeyPath.startsWith("classpath:")) {
            String path = privateKeyPath.substring("classpath:".length());
            ClassPathResource resource = new ClassPathResource(path);
            return new String(resource.getInputStream().readAllBytes());
        } else {
            return Files.readString(java.nio.file.Path.of(privateKeyPath));
        }
    }
}
