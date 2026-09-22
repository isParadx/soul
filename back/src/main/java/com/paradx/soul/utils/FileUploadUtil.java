package com.paradx.soul.utils;

import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * 文件上传安全工具类
 * 用于校验上传文件的类型和安全性
 */
public final class FileUploadUtil {

    private FileUploadUtil() {}

    /**
     * 允许上传的图片类型（白名单）
     */
    private static final Set<String> ALLOWED_IMAGE_TYPES = new HashSet<>(Arrays.asList(
            "image/jpeg",
            "image/jpg",
            "image/png",
            "image/gif",
            "image/webp"
    ));

    /**
     * 允许的文件扩展名
     */
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            ".jpg", ".jpeg", ".png", ".gif", ".webp"
    ));

    /**
     * 最大文件大小（5MB）
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 校验文件是否安全
     * @param file 上传的文件
     * @return 错误信息，null表示安全
     */
    public static String validateImageFile(MultipartFile file) {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            return "上传文件不能为空";
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            return "文件大小不能超过5MB";
        }

        // 检查Content-Type
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_IMAGE_TYPES.contains(contentType.toLowerCase())) {
            return "仅支持 JPG、PNG、GIF、WebP 格式的图片";
        }

        // 检查文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            return "文件名不能为空";
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            return "文件扩展名不合法，仅支持: " + ALLOWED_EXTENSIONS;
        }

        // 验证文件头魔数（防止伪造扩展名）
        try {
            byte[] headerBytes = new byte[4];
            file.getInputStream().read(headerBytes);
            if (!isValidImageHeader(headerBytes, extension)) {
                return "文件内容与扩展名不匹配";
            }
        } catch (Exception e) {
            return "文件读取失败";
        }

        return null;  // 校验通过
    }

    /**
     * 生成安全的文件名
     * 使用UUID替换原始文件名，防止路径遍历攻击
     * @param originalFilename 原始文件名
     * @return 安全的文件名
     */
    public static String generateSafeFileName(String originalFilename) {
        String extension = getFileExtension(originalFilename);
        return UUID.randomUUID().toString().replace("-", "") + extension;
    }

    /**
     * 获取文件扩展名
     * @param filename 文件名
     * @return 扩展名（包含点号），如".jpg"
     */
    private static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }

    /**
     * 验证图片文件头魔数
     * @param header 文件头字节
     * @param extension 文件扩展名
     * @return 是否有效
     */
    private static boolean isValidImageHeader(byte[] header, String extension) {
        if (header == null || header.length < 4) {
            return false;
        }

        // JPEG: FF D8 FF
        if (extension.equals(".jpg") || extension.equals(".jpeg")) {
            return header[0] == (byte) 0xFF && header[1] == (byte) 0xD8 && header[2] == (byte) 0xFF;
        }

        // PNG: 89 50 4E 47
        if (extension.equals(".png")) {
            return header[0] == (byte) 0x89 && header[1] == (byte) 0x50 
                    && header[2] == (byte) 0x4E && header[3] == (byte) 0x47;
        }

        // GIF: 47 49 46 38
        if (extension.equals(".gif")) {
            return header[0] == (byte) 0x47 && header[1] == (byte) 0x49 
                    && header[2] == (byte) 0x46 && header[3] == (byte) 0x38;
        }

        // WebP: 52 49 46 46 (RIFF)
        if (extension.equals(".webp")) {
            return header[0] == (byte) 0x52 && header[1] == (byte) 0x49 
                    && header[2] == (byte) 0x46 && header[3] == (byte) 0x46;
        }

        return false;
    }
}
