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
package org.mybatis.generator.api.dom.xml.render;

import java.util.Comparator;
import java.util.stream.Stream;

import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.ElementVisitor;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.VisitableElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.internal.util.CustomCollectors;

public class ElementRenderer implements ElementVisitor<Stream<String>> {
    private static final String INDENT = "    ";

    private final AttributeRenderer attributeRenderer = new AttributeRenderer();

    @Override
    public Stream<String> visit(TextElement element) {
        return Stream.of(element.getContent());
    }

    @Override
    public Stream<String> visit(XmlElement element, boolean isRoot) {
        Stream<String> stream;
        if (element.hasChildren()) {
            stream = renderWithChildren(element, isRoot);
        } else {
            stream = renderWithoutChildren(element);
        }

        return stream;
    }

    private Stream<String> renderWithoutChildren(XmlElement element) {
        return Stream.of("<" //$NON-NLS-1$
                + element.getName()
                + renderAttributes(element)
                + " />"); //$NON-NLS-1$
    }

    public Stream<String> renderWithChildren(XmlElement element, boolean isRoot) {
        return Stream.of(renderOpen(element), renderChildren(element, isRoot), renderClose(element))
                .flatMap(s -> s);
    }

    private String renderAttributes(XmlElement element) {
        return element.getAttributes().stream()
                .sorted(Comparator.comparing(Attribute::getName))
                .map(attributeRenderer::render)
                .collect(CustomCollectors.joining(" ", " ", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    private Stream<String> renderOpen(XmlElement element) {
        return Stream.of("<" //$NON-NLS-1$
                + element.getName()
                + renderAttributes(element)
                + ">"); //$NON-NLS-1$
    }

    private Stream<String> renderChildren(XmlElement element, boolean isRoot) {
        if (isRoot) {
            // 每个元素后面增加换行
            return element.getElements().stream()
                    .flatMap(this::renderFirstLevelChild)
                    .map(this::indent);
        }

        return element.getElements().stream()
                .flatMap(this::renderChild)
                .map(this::indent);
    }

    private Stream<String> renderChild(VisitableElement child) {
        return child.accept(this, false);
    }

    /**
     * 底层的 element 设置换行， 增加可读性
     *
     * @param child
     * @return
     */
    private Stream<String> renderFirstLevelChild(VisitableElement child) {
        Stream<String> accept = child.accept(this, false);
        return Stream.of(accept, Stream.of(INDENT)).flatMap(s -> s);
    }

    private String indent(String s) {
        return INDENT + s; //$NON-NLS-1$
    }

    private Stream<String> renderClose(XmlElement element) {
        return Stream.of("</" //$NON-NLS-1$
                + element.getName()
                + ">"); //$NON-NLS-1$
    }
}
