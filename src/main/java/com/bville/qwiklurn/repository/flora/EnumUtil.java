package com.bville.qwiklurn.repository.flora;

import java.util.Arrays;
import java.util.Optional;

public class EnumUtil {
    public static CodeDescrEnum parse(String code, CodeDescrEnum[] values) {
        Optional<CodeDescrEnum> optional = Arrays.stream(values).filter(e -> e.getCode().equalsIgnoreCase(code)).findFirst();
        if (optional.isPresent()) {
            return optional.get();
        }

        throw new RuntimeException(String.format("Unsupported code for %s: %s",values[0].getClass().getName(),code));
    }
}
