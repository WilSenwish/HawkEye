package com.littleyes.storage.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * <p> <b> 验证码 </b> </p>
 *
 * @author Junbing.Chen
 * @date 2021-03-16
 */
public class ValidateCode {

    /**
     * 图片的宽度。
     */
    private int width = 100;
    /**
     * 图片的高度。
     */
    private int height = 40;
    /**
     * 验证码干扰线数量
     */
    private int lineCount = 50;
    /**
     * 验证码
     */
    private String code;


    private ValidateCode(String code) {
        this.code = code;
    }

    private ValidateCode(int width, String code) {
        this(code);
        this.width = width;
        this.height = (int) (width * 0.4D);
    }

    private ValidateCode(int width, String code, int lineCount) {
        this(width, code);
        this.lineCount = lineCount;
    }


    public static String generate(String code) {
        return new ValidateCode(code).generate();
    }

    public static String generate(int width, String code) {
        return new ValidateCode(width, code).generate();
    }

    public static String generate(int width, String code, int lineCount) {
        return new ValidateCode(width, code, lineCount).generate();
    }


    private String generate() {
        int fontHeight = height - 2;

        // 图像buffer
        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = buffImg.createGraphics();
        // 将图像填充为白色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, width, height);
        // 创建字体,可以修改为其它的
        Font font = new Font("Fixedsys", Font.PLAIN, fontHeight);
        g.setFont(font);

        // 生成验证码图像
        drawLines(g);
        drawCodeChars(g, fontHeight);

        try (ByteArrayOutputStream bs = new ByteArrayOutputStream()) {
            ImageIO.write(buffImg, "PNG", bs);
            return Base64.getEncoder().encodeToString(bs.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("验证码生成异常", e);
        }
    }

    private void drawLines(Graphics2D graphics) {
        int r;
        int g;
        int b;
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < lineCount; i++) {
            // 设置随机开始和结束坐标
            //x坐标开始
            int xs = random.nextInt(width);
            //y坐标开始
            int ys = random.nextInt(height);
            //x坐标结束
            int xe = xs + random.nextInt(width / 8);
            //y坐标结束
            int ye = ys + random.nextInt(height / 8);

            // 产生随机的颜色值，让输出的每个干扰线的颜色值都将不同。
            r = random.nextInt(255);
            g = random.nextInt(255);
            b = random.nextInt(255);

            graphics.setColor(new Color(r, g, b));
            graphics.drawLine(xs, ys, xe, ye);
        }
    }

    private void drawCodeChars(Graphics2D graphics, int fontHeight) {
        int r;
        int g;
        int b;

        int x = width / (code.length() + 1);
        int y = height - (int) (fontHeight * 0.186D) + 1;
        int base = x / 2;
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < code.length(); i++) {
            // 产生随机的颜色值，让输出的每个字符的颜色值都将不同。
            r = random.nextInt(255);
            g = random.nextInt(255);
            b = random.nextInt(255);

            graphics.setColor(new Color(r, g, b));
            graphics.drawString(String.valueOf(code.charAt(i)), i * x + base, y);
        }
    }

}
