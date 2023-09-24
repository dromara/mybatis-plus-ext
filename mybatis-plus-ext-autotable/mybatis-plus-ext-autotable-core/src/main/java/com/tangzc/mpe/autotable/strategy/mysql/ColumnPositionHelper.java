package com.tangzc.mpe.autotable.strategy.mysql;

import com.tangzc.mpe.autotable.strategy.mysql.data.MysqlColumnMetadata;
import com.tangzc.mpe.autotable.strategy.mysql.data.dbdata.InformationSchemaColumn;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ColumnPositionHelper {

    public static void generateChangePosition(List<InformationSchemaColumn> dbColumns, List<MysqlColumnMetadata> expectPositions) {

        List<InformationSchemaColumnPosition> realPositions = dbColumns.stream()
                .map(col -> new InformationSchemaColumnPosition(col.getColumnName(), col.getOrdinalPosition()))
                .collect(Collectors.toList());

        // 删除数据库列集合中不存在于实体字段上的列
        Set<String> entityColumnsSet = expectPositions.stream().map(MysqlColumnMetadata::getName).collect(Collectors.toSet());
        List<InformationSchemaColumnPosition> removeColumns = new ArrayList<>(realPositions.size());
        for (InformationSchemaColumnPosition realPosition : realPositions) {
            if (!entityColumnsSet.contains(realPosition.getColumnName())) {
                removeColumns.add(realPosition);
                continue;
            }
            if (!removeColumns.isEmpty()) {
                realPosition.setOrdinalPosition(realPosition.getOrdinalPosition() - removeColumns.size());
            }
        }
        realPositions.removeAll(removeColumns);

        // 向数据库列集合中，添加数据库中不存在的列到最后
        Set<String> dbColumnsSet = realPositions.stream().map(InformationSchemaColumnPosition::getColumnName).collect(Collectors.toSet());
        for (MysqlColumnMetadata expectPosition : expectPositions) {
            if (!dbColumnsSet.contains(expectPosition.getName())) {
                realPositions.add(new InformationSchemaColumnPosition(expectPosition.getName(), realPositions.size() + 1));
            }
        }

        Map<String, InformationSchemaColumnPosition> dbColumnPositionMap = realPositions.stream()
                .collect(Collectors.toMap(InformationSchemaColumnPosition::getColumnName, Function.identity()));

        Map<String, MysqlColumnMetadata> columnMetadataMap = expectPositions.stream().collect(Collectors.toMap(MysqlColumnMetadata::getName, Function.identity()));
        for (int index = 0; index < expectPositions.size(); index++) {
            // 当前位置期望的列名
            String expectColumnName = expectPositions.get(index).getName();
            // 当前位置实际的列名
            String realColumnName = realPositions.get(index).getColumnName();
            if (Objects.equals(expectColumnName, realColumnName)) {
                continue;
            }
            // 获取期望列名的实际位置
            Integer expectColumnNameRealPosition = dbColumnPositionMap.get(expectColumnName).getOrdinalPosition();
            // 从列表中删除
            realPositions.remove(expectColumnNameRealPosition - 1);
            // 再次插入新的位置
            realPositions.add(index, new InformationSchemaColumnPosition(expectColumnName, index + 1));
            // 该位置后面的列（直到删除的位置为止）的位置均+1
            for (int i = index + 1; i < expectColumnNameRealPosition; i++) {
                InformationSchemaColumnPosition columnPosition = realPositions.get(i);
                columnPosition.setOrdinalPosition(columnPosition.getOrdinalPosition() + 1);
            }

            if (index == 0) {
                // 如果新位置在最前面
                columnMetadataMap.get(expectColumnName).setNewPreColumn("");
            } else {
                // 取前一个字段的名字，声明排在他后面
                columnMetadataMap.get(expectColumnName).setNewPreColumn(realPositions.get(index - 1).getColumnName());
            }
        }
    }

    @Data
    @AllArgsConstructor
    public static class InformationSchemaColumnPosition {
        private String columnName;
        private Integer ordinalPosition;
    }

    // public static void main(String[] args) {
    //
    //     List<InformationSchemaColumn> dbColumns = Arrays.asList(db(1, "A"), db(2, "B"), db(3, "C"), db(4, "D"), db(5, "E"), db(6, "H"));
    //     List<MysqlColumnMetadata> expectPositions = Arrays.asList(app(1, "B"), app(2, "G"), app(3, "F"), app(4, "C"), app(5, "A"), app(6, "E"), app(7, "D"));
    //
    //
    //     Map<String, String> stringStringMap = generateChangePositionSql(dbColumns, expectPositions);
    // }


    // private static InformationSchemaColumn db(int index, String name) {
    //     InformationSchemaColumn informationSchemaColumn = new InformationSchemaColumn();
    //     informationSchemaColumn.setColumnName(name);
    //     informationSchemaColumn.setOrdinalPosition(index);
    //     return informationSchemaColumn;
    // }
    //
    // private static MysqlColumnMetadata app(int index, String org) {
    //     MysqlColumnMetadata informationSchemaColumn = new MysqlColumnMetadata();
    //     informationSchemaColumn.setName(org);
    //     informationSchemaColumn.setPosition(index);
    //     return informationSchemaColumn;
    // }
}
