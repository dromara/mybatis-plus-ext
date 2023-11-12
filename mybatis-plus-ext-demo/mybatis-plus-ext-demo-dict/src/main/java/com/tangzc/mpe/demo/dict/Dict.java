package com.tangzc.mpe.demo.dict;

import com.tangzc.mpe.bind.metadata.annotation.BindField;
import com.tangzc.mpe.bind.metadata.annotation.JoinCondition;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@BindField(entity = SysDict.class, field = "dictVal", conditions = @JoinCondition(selfField = ""))
public @interface Dict {

    @AliasFor(annotation = BindField.class, attribute = "conditions")
    JoinCondition value();
}
