package org.dromara.mpe.bind.metadata;

import org.dromara.mpe.bind.metadata.annotation.JoinOrderBy;
import org.dromara.mpe.magic.util.TableColumnNameUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.Objects;

/**
 * @author don
 */
@Getter
@AllArgsConstructor
public class OrderByDescription {

    /**
     * {@link JoinOrderBy#field()}
     */
    private final Field field;
    /**
     * {@link JoinOrderBy#isAsc()}
     */
    private final boolean isAsc;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        OrderByDescription orderBy = (OrderByDescription) o;
        return getFieldName().equals(orderBy.getFieldName()) && isAsc == orderBy.isAsc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getFieldName(), isAsc);
    }

    @Override
    public String toString() {
        return getFieldName() + "|" + isAsc;
    }

    public String getFieldName() {
        return field.getName();
    }

    public String getColumnName() {
        return TableColumnNameUtil.getColumnName(field);
    }
}
