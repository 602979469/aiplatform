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
        BufferedImage image = scale(source, maxWidth, Integer.MAX_VALUE);
        return ObjectUtil.isNull(image) ? null : encodeJpeg(image, JPEG_QUALITY);
    }

    /**
     * 上传落库前的压缩：按最大宽高（只缩不放）等比缩放后编码为 JPEG，
     * 结果不比原图小就返回 null（调用方保留原图）。
     *
     * <p>注意只按"宽度上限 + 高度上限"缩放，不按长边缩放：手机截图是又高又窄的图，
     * 按长边缩会把宽度压小、字就看不清了；截图宽度一般在 1600 内，于是只走重编码。
     *
     * @param source 原图字节
     * @param maxWidth 最大宽度（像素）
     * @param maxHeight 最大高度（像素）
     * @param quality JPEG 质量（0~1）
     * @return 压缩后的 JPEG 字节；不需要压缩或压缩无收益返回 null
     */
    public static byte[] compressForStorage(byte[] source, int maxWidth, int maxHeight, float quality) {
        BufferedImage image = scale(source, maxWidth, maxHeight);
        if (ObjectUtil.isNull(image)) {
            return null;
        }
        byte[] encoded = encodeJpeg(image, quality);
        return ObjectUtil.isNotNull(encoded) && encoded.length < source.length ? encoded : null;
    }

    /**
     * 等比缩放（只缩不放，宽高都不超上限）；不是图片返回 null。
     */
    private static BufferedImage scale(byte[] source, int maxWidth, int maxHeight) {
        if (ArrayUtil.isEmpty(source) || maxWidth <= 0) {
            return null;
        }
        try (ByteArrayInputStream in = new ByteArrayInputStream(source)) {
            BufferedImage src = ImageIO.read(in);
            if (ObjectUtil.isNull(src) || src.getWidth() <= 0 || src.getHeight() <= 0) {
                return null;
            }
            double scale = Math.min(1D, Math.min(maxWidth / (double) src.getWidth(),
                    Math.max(maxHeight, 1) / (double) src.getHeight()));
            int width = Math.max(1, (int) Math.round(src.getWidth() * scale));
            int height = Math.max(1, (int) Math.round(src.getHeight() * scale));
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
            return target;
        } catch (Exception e) {
            LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "图片缩放失败：{}", e.getMessage());
            return null;
        }
    }

    /**
     * 编码为 JPEG 字节。
     */
    private static byte[] encodeJpeg(BufferedImage image, float quality) {
        try {
            return doEncodeJpeg(image, quality);
        } catch (Exception e) {
            LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "图片编码失败：{}", e.getMessage());
            return null;
        }
    }

    private static byte[] doEncodeJpeg(BufferedImage image, float quality) throws Exception {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            return null;
        }
        ImageWriter writer = writers.next();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ImageOutputStream imageOut = ImageIO.createImageOutputStream(out)) {
            ImageWriteParam param = writer.getDefaultWriteParam();
            param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            param.setCompressionQuality(quality);
            writer.setOutput(imageOut);
            writer.write(null, new IIOImage(image, null, null), param);
        } finally {
            writer.dispose();
        }
        return out.toByteArray();
    }
}
