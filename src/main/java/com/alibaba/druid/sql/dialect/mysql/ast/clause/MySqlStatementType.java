/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.sql.dialect.mysql.ast.clause;

import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlBlockStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlDeleteStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlInsertStatement;
import com.alibaba.druid.sql.dialect.mysql.ast.statement.MySqlUpdateStatement;

/**
 * MySql procedure statement type
 * @author zz
 *
 */
public enum MySqlStatementType {
	//select statement
	SELECT(SQLSelectStatement.class.getName()),
	//update statement
	UPDATE(MySqlUpdateStatement.class.getName()),
	//insert statement
	INSERT(MySqlInsertStatement.class.getName()),
	//delete statement
	DELETE(MySqlDeleteStatement.class.getName()),
	//while statement
	WHILE(MySqlWhileStatement.class.getName()),
	//if statement
	IF(MySqlIfStatement.class.getName()),
	//begin-end
	BLOCK(MySqlBlockStatement.class.getName()),
	//declare statement
	DECLARE(MySqlDeclareStatement.class.getName()),
	//select into
	SELECTINTO(MySqlSelectIntoStatement.class.getName()),
	//case
	CASE(MySqlCaseStatement.class.getName()),
	
	UNDEFINED,
	;
	
	
	
	public final String name;

	MySqlStatementType(){
        this(null);
    }

	MySqlStatementType(String name){
        this.name = name;
    }
	public static MySqlStatementType getType(SQLStatement stmt)
	{
		 for (MySqlStatementType type : MySqlStatementType.values()) {
             if (type.name == stmt.getClass().getName()) {
                 return type;
             }
         }
		 return UNDEFINED;
	}
}
