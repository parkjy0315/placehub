package com.placehub.boundedContext.post.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreSignedUrlResponseForm {
    public int idx;
    public long fileName;
    public String preSignedUrl;
}
