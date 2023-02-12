/*
 *    Copyright 2006-2023 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.mybatis.generator.codegen.mybatis3.javamapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;

public class SelectByColumnsMethodGenerator extends AbstractJavaMapperMethodGenerator {

    public SelectByColumnsMethodGenerator() {
        super();
    }

    @Override
    public void addInterfaceElements(Interface interfaze) {
        List<String> queryColumns = introspectedTable.getQueryColumns();
        if (queryColumns == null || queryColumns.size() == 0) {
            return;
        }

        FullyQualifiedJavaType javaType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        String name = "list" + javaType.getShortName() + "s";
        Method method = new Method(name);
        method.setVisibility(JavaVisibility.PUBLIC);
        method.setAbstract(true);

        FullyQualifiedJavaType returnType = FullyQualifiedJavaType.getNewListInstance();
        FullyQualifiedJavaType listType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());

        // import 内容
        Set<FullyQualifiedJavaType> importedTypes = new TreeSet<>();
        importedTypes.add(returnType);
        importedTypes.add(listType);

        returnType.addTypeArgument(listType);
        method.setReturnType(returnType);

        for (String queryColumn : queryColumns) {
            Optional<IntrospectedColumn> optional = introspectedTable.getColumn(queryColumn);
            if (!optional.isPresent()) {
                throw new RuntimeException("The column " + queryColumn + "does not found, which is configurated in queryColumns.");
            }

            IntrospectedColumn introspectedColumn = optional.get();
            Parameter parameter = buildParameter(introspectedColumn);
            method.addParameter(parameter); //$NON-NLS-1$
        }

        context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);
        if (context.getPlugins().clientInsertMethodGenerated(method, interfaze, introspectedTable)) {
            interfaze.addImportedTypes(importedTypes);
            interfaze.addMethod(method);
        }
    }
}
