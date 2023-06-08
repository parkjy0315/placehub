package com.placehub.boundedContext.post.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreatingForm extends BasePostFormFromFrontToBack {
    @NotBlank
    public String isOpenToPublic;
}
