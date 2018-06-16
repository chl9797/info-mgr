package com.jw.infomgr.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "kira")
public class UploadConfig {
    private String uploadPath;  // 上传路径
}
