package com.friendsbook.config;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

@Configuration
public class KaptchaConfig {

    @Bean
    public Producer kaptchaProducer() {
        Properties properties = new Properties();
        properties.put("kaptcha.image.width", "200");
        properties.put("kaptcha.image.height", "50");
        properties.put("kaptcha.textproducer.font.size", "40");
        properties.put("kaptcha.textproducer.char.length", "6");
        properties.put("kaptcha.textproducer.char.string", "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890");
        properties.put("kaptcha.textproducer.font.color", "black");
        properties.put("kaptcha.background.clear.from", "white");
        properties.put("kaptcha.background.clear.to", "lightgray");
        
        Config config = new Config(properties);
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(config);
        
        return kaptcha;
    }
}
