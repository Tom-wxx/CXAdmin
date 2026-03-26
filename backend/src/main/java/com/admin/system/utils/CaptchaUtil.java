package com.admin.system.utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码生成工具类
 */
public class CaptchaUtil {

    private static final int WIDTH = 150;
    private static final int HEIGHT = 50;
    private static final int CODE_LENGTH = 4;
    // 验证码字符集：数字和字母（排除易混淆字符 0O1lI）
    private static final String CODE_CHARS = "23456789ABCDEFGHJKMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz";
    private static final Random RANDOM = new Random();

    /**
     * 生成验证码图片
     * @return 包含验证码文本和base64图片的数组
     */
    public static String[] generateCaptcha() {
        // 创建图片缓冲区
        BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image.createGraphics();

        // 设置背景色
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, WIDTH, HEIGHT);

        // 设置字体 - 使用更大更清晰的字体
        g.setFont(new Font("Arial", Font.BOLD, 32));

        // 生成验证码文本
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            String ch = String.valueOf(CODE_CHARS.charAt(RANDOM.nextInt(CODE_CHARS.length())));
            code.append(ch);

            // 使用更深的随机颜色提高对比度
            g.setColor(new Color(RANDOM.nextInt(80), RANDOM.nextInt(80), RANDOM.nextInt(80)));

            // 随机位置和角度
            int x = 25 + i * 30;
            int y = 35 + RANDOM.nextInt(8);
            int angle = RANDOM.nextInt(20) - 10;  // 减小旋转角度，提高可读性

            // 旋转绘制
            g.rotate(Math.toRadians(angle), x, y);
            g.drawString(ch, x, y);
            g.rotate(-Math.toRadians(angle), x, y);
        }

        // 绘制干扰线 - 使用更浅的颜色，不影响验证码识别
        for (int i = 0; i < 3; i++) {
            g.setColor(new Color(150 + RANDOM.nextInt(80), 150 + RANDOM.nextInt(80), 150 + RANDOM.nextInt(80)));
            int x1 = RANDOM.nextInt(WIDTH);
            int y1 = RANDOM.nextInt(HEIGHT);
            int x2 = RANDOM.nextInt(WIDTH);
            int y2 = RANDOM.nextInt(HEIGHT);
            g.drawLine(x1, y1, x2, y2);
        }

        // 绘制干扰点 - 减少数量和使用更浅的颜色
        for (int i = 0; i < 20; i++) {
            g.setColor(new Color(180 + RANDOM.nextInt(75), 180 + RANDOM.nextInt(75), 180 + RANDOM.nextInt(75)));
            g.fillOval(RANDOM.nextInt(WIDTH), RANDOM.nextInt(HEIGHT), 2, 2);
        }

        g.dispose();

        // 转换为Base64
        String base64Image = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] bytes = baos.toByteArray();
            base64Image = "data:image/png;base64," + Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            throw new RuntimeException("生成验证码图片失败", e);
        }

        return new String[]{code.toString(), base64Image};
    }
}
