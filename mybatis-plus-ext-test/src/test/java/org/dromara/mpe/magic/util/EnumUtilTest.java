package org.dromara.mpe.magic.util;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EnumUtilTest {

    // 普通枚举（非MP枚举）
    enum PlainEnum { A, B, C }

    // 使用 IEnum 接口的 MP 枚举
    enum MpIEnum implements IEnum<Integer> {
        ONE(1), TWO(2);
        private final int value;
        MpIEnum(int value) { this.value = value; }
        @Override
        public Integer getValue() { return value; }
    }

    // 使用 @EnumValue 的 MP 枚举
    enum MpAnnoEnum {
        X("x"), Y("y");
        @EnumValue
        private final String code;
        MpAnnoEnum(String code) { this.code = code; }
    }

    @Test
    void isMpEnums_plainEnum_shouldReturnFalse() {
        assertFalse(EnumUtil.isMpEnums(PlainEnum.class));
    }

    @Test
    void isMpEnums_iEnum_shouldReturnTrue() {
        assertTrue(EnumUtil.isMpEnums(MpIEnum.class));
    }

    @Test
    void isMpEnums_annoEnum_shouldReturnTrue() {
        assertTrue(EnumUtil.isMpEnums(MpAnnoEnum.class));
    }

    @Test
    void getEnumFieldSaveDbType_plainEnum_shouldReturnString() {
        assertEquals(String.class, EnumUtil.getEnumFieldSaveDbType(PlainEnum.class));
    }

    @Test
    void getEnumFieldSaveDbType_iEnum_shouldReturnInteger() {
        assertEquals(Integer.class, EnumUtil.getEnumFieldSaveDbType(MpIEnum.class));
    }

    @Test
    void getEnumFieldSaveDbType_annoEnum_shouldReturnString() {
        assertEquals(String.class, EnumUtil.getEnumFieldSaveDbType(MpAnnoEnum.class));
    }

    @Test
    void getEnumFieldSaveDbType_nonEnum_shouldThrow() {
        assertThrows(IllegalArgumentException.class, () -> EnumUtil.getEnumFieldSaveDbType(String.class));
    }
}
