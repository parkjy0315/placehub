package com.placehub.boundedContext.post.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreSignedUrlRequestForm {
    public int id;
    public FileData fileData;

    @Getter
    @Setter
    public static class FileData {
        public String fileName;
        public String contentType;
    }
}
