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
package org.mybatis.generator.internal;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.comments.Comment;
import org.mybatis.generator.api.ShellCallback;
import org.mybatis.generator.exception.ShellException;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;

import static org.mybatis.generator.internal.util.messages.Messages.getString;

public class DefaultShellCallback implements ShellCallback {

    private final boolean overwrite;

    public DefaultShellCallback(boolean overwrite) {
        super();
        this.overwrite = overwrite;
    }

    @Override
    public File getDirectory(String targetProject, String targetPackage) throws ShellException {
        // targetProject is interpreted as a directory that must already exist
        //
        // targetPackage is interpreted as a subdirectory, but in package
        // format (with dots instead of slashes). The subdirectory will be
        // created if it does not already exist

        File targetProjectDirectory = new File(targetProject);
        if (!targetProjectDirectory.isDirectory()) {
            throw new ShellException(getString("Warning.9", //$NON-NLS-1$
                    targetProject));
        }

        StringBuilder sb = new StringBuilder();
        StringTokenizer st = new StringTokenizer(targetPackage, "."); //$NON-NLS-1$
        while (st.hasMoreTokens()) {
            sb.append(st.nextToken());
            sb.append(File.separatorChar);
        }

        File directory = new File(targetProjectDirectory, sb.toString());
        if (!directory.isDirectory()) {
            boolean rc = directory.mkdirs();
            if (!rc) {
                throw new ShellException(getString("Warning.10", //$NON-NLS-1$
                        directory.getAbsolutePath()));
            }
        }

        return directory;
    }

    @Override
    public boolean isOverwriteEnabled() {
        return overwrite;
    }


    @Override
    public String mergeJavaFile(String newFileSource, File existingFile, String[] javadocTags, String fileEncoding) throws ShellException {
        try {
            CompilationUnit existCU = StaticJavaParser.parse(existingFile);
            CompilationUnit newCU = StaticJavaParser.parse(newFileSource);

            ClassOrInterfaceDeclaration existClass = getClassOrInterfaceDeclaration(existCU);
            ClassOrInterfaceDeclaration newClass = getClassOrInterfaceDeclaration(newCU);

            if (existClass != null && newClass != null) {
                // remove generated method
                removeGeneratedMethod(existClass);

                // add new method
                for (MethodDeclaration method : newClass.getMethods()) {
                    existClass.getMembers().add(method);
                }

                return existCU.toString();
            } else {
                return newFileSource;
            }
        } catch (Exception e) {
            throw new ShellException(e);
        }
    }

    private void removeGeneratedMethod(ClassOrInterfaceDeclaration existClass) {
        List<MethodDeclaration> methods = existClass.getMethods();
        for (MethodDeclaration method : methods) {
            Optional<Comment> optional = method.getComment();
            if (optional.isPresent()) {
                Comment comment = optional.get();
                String commentContent = comment.asString();
                if (commentContent.contains("@mbg.generated")) {
                    // delete method which is auto generated
                    existClass.remove(method);
                }
            }
        }
    }

    // get class
    private ClassOrInterfaceDeclaration getClassOrInterfaceDeclaration(CompilationUnit compilationUnit) {
        for (TypeDeclaration<?> type : compilationUnit.getTypes()) {
            if (type instanceof ClassOrInterfaceDeclaration) {
                return (ClassOrInterfaceDeclaration) type;
            }
        }
        return null;
    }

    @Override
    public boolean isMergeSupported() {
        return true;
    }
}
