package com.jakt.aiplatform.common.util.tools;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

/**
 * 图片工具：把原图缩成列表用的小图，避免列表页直接拉几 MB 的原图。
 */
public final class ImageUtil {

    /** 缩略图 JPEG 质量。 */
    private static final float JPEG_QUALITY = 0.82F;

    private ImageUtil() {
    }

    /**
     * 按宽度等比缩放（只缩不放），统一输出 JPEG 字节；不是图片或处理失败返回 null。
     *
     * @param source 原图字节
     * @param maxWidth 目标最大宽度（像素）
     * @return JPEG 字节；失败返回 null（调用方回退到原图）
     */
    public static byte[] resizeToWidth(byte[] source, int maxWidth) {
        if (ArrayUtil.isEmpty(source) || maxWidth <= 0) {
            return null;
        }
        try (ByteArrayInputStream in = new ByteArrayInputStream(source)) {
            BufferedImage src = ImageIO.read(in);
            if (ObjectUtil.isNull(src) || src.getWidth() <= 0 || src.getHeight() <= 0) {
                return null;
            }
            int width = Math.min(maxWidth, src.getWidth());
            int height = Math.max(1, (int) Math.round(src.getHeight() * (width / (double) src.getWidth())));
            BufferedImage target = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D graphics = target.createGraphics();
            graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            // 透明底铺白，JPEG 不支持透明
            graphics.setColor(Color.WHITE);
            graphics.fillRect(0, 0, width, height);
            graphics.drawImage(src, 0, 0, width, height, null);
            graphics.dispose();
            return writeJpeg(target);
        } catch (Exception e) {
            LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "图片缩放失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 编码为 JPEG 字节。
     */
    private static byte[] writeJpeg(BufferedImage image) throws Exception {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            return null;
        }
        ImageWriter writer = writers.next();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ImageOutputStream imageOut = ImageIO.createImageOutputStream(out)) {
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(JPEG_QUALITY);
            writer.setOutput(imageOut);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
        return out.toByteArray();
    }
}
