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
package org.mybatis.generator.codegen.mybatis3.xmlmapper.elements;

import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;

public class SelectByColumnsElementGenerator extends AbstractXmlElementGenerator {

    public SelectByColumnsElementGenerator() {
        super();
    }

    @Override
    public void addElements(XmlElement parentElement) {
        List<String> queryColumns = introspectedTable.getQueryColumns();
        if (queryColumns == null || queryColumns.size() == 0) {
            return;
        }

        XmlElement answer = new XmlElement("select"); //$NON-NLS-1$
        FullyQualifiedJavaType javaType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        String name = "list" + javaType.getShortName() + "s";
        answer.addAttribute(new Attribute("id", name)); //$NON-NLS-1$
        answer.addAttribute(new Attribute("resultMap", //$NON-NLS-1$
                introspectedTable.getBaseResultMapId()));
        context.getCommentGenerator().addComment(answer);

        StringBuilder sb = new StringBuilder();
        sb.append("select "); //$NON-NLS-1$
        answer.addElement(new TextElement(sb.toString()));

        answer.addElement(getBaseColumnListElement());

        sb.setLength(0);
        sb.append("from "); //$NON-NLS-1$
        sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
        answer.addElement(new TextElement(sb.toString()));

        buildQueryWhereClause().forEach(answer::addElement);

        if (context.getPlugins().sqlMapSelectByPrimaryKeyElementGenerated(answer, introspectedTable)) {
            parentElement.addElement(answer);
        }
    }

    protected List<TextElement> buildQueryWhereClause() {
        List<String> queryColumns = introspectedTable.getQueryColumns();
        boolean first = true;
        List<TextElement> textElements = new ArrayList<>();
        for (String queryColumn : queryColumns) {
            String line;
            if (first) {
                line = "where "; //$NON-NLS-1$
                first = false;
            } else {
                line = "  and "; //$NON-NLS-1$
            }

            Optional<IntrospectedColumn> optional = introspectedTable.getColumn(queryColumn);
            IntrospectedColumn introspectedColumn = optional.get();

            line += MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn);
            line += " = "; //$NON-NLS-1$
            line += MyBatis3FormattingUtilities.getParameterClause(introspectedColumn);
            textElements.add(new TextElement(line));
        }
        return textElements;
    }
}
