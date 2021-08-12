package com.tangzc.mpe.actable.constants;

public class Constants {

    public static final String ACTABLE_DATABASE_TYPE_KEY_VALUE = "${actable.database.type:mysql}";
    public static final String ACTABLE_MODEL_PACK_KEY_VALUE = "${actable.model.pack:}";
    public static final String ACTABLE_TABLE_AUTO_KEY_VALUE = "${actable.table.auto:update}";

    public static final String TABLE_INDEX_KEY_VALUE = "${actable.index.prefix:actable_idx_}";
    public static final String TABLE_UNIQUE_KEY_VALUE = "${actable.unique.prefix:actable_uni_}";

    public static final String NEW_TABLE_MAP = "newTableMap";
    public static final String MODIFY_TABLE_MAP = "modifyTableMap";
    public static final String ADD_TABLE_MAP = "addTableMap";
    public static final String REMOVE_TABLE_MAP = "removeTableMap";
    public static final String MODIFY_TABLE_PROPERTY_MAP = "modifyTablePropertyMap";
    public static final String DROPKEY_TABLE_MAP = "dropKeyTableMap";
    public static final String DROPINDEXANDUNIQUE_TABLE_MAP = "dropIndexAndUniqueTableMap";
    public static final String ADDINDEX_TABLE_MAP = "addIndexTableMap";
    public static final String ADDUNIQUE_TABLE_MAP = "addUniqueTableMap";

}
