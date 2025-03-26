package com.friendsbook.service;

import java.io.ByteArrayOutputStream;
import java.util.Base64;
import javax.imageio.ImageIO;

import org.springframework.stereotype.Service;

import com.friendsbook.entity.Captchaa;

import cn.apiclub.captcha.Captcha;
import cn.apiclub.captcha.backgrounds.GradiatedBackgroundProducer;
import cn.apiclub.captcha.noise.CurvedLineNoiseProducer;
import cn.apiclub.captcha.text.producer.DefaultTextProducer;
import cn.apiclub.captcha.text.renderer.DefaultWordRenderer;

@Service
public class CaptchaGenerator {

    public static Captcha generateCaptcha(Integer height, Integer width) {
        return new Captcha.Builder(width, height).addBackground(new GradiatedBackgroundProducer())
                .addText(new DefaultTextProducer(), new DefaultWordRenderer()).addNoise(new CurvedLineNoiseProducer())
                .build();
    }

    public static String encodeCaptchaToBinary(Captcha captcha) {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(captcha.getImage(), "jpg", bos);
            return Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (Exception exc) {
            exc.printStackTrace();
            return null;
        }
    }

    public static Captchaa getCaptcha() {
        Captchaa captchaSettings = new Captchaa();
        Captcha captcha = generateCaptcha(80, 150);
        captchaSettings.setHiddenCaptcha(captcha.getAnswer());
        captchaSettings.setCaptcha("");
        captchaSettings.setRealCaptcha(encodeCaptchaToBinary(captcha));
        return captchaSettings;
    }
}
