package com.placehub.boundedContext.post.form;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class CreatingForm extends BasePostFormFromFrontToBack {
    @NotBlank
    public String isOpenToPublic;
}
