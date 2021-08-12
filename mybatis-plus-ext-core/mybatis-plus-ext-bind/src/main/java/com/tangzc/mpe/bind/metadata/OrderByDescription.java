package com.tangzc.mpe.bind.metadata;

import com.tangzc.mpe.bind.metadata.annotation.JoinOrderBy;
import lombok.AllArgsConstructor;
import lombok.Getter;

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
    private final String field;
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
        return field.equals(orderBy.field) && isAsc == orderBy.isAsc;
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, isAsc);
    }

    @Override
    public String toString() {
        return field + "|" + isAsc;
    }
}
