/**
 * Copyright 2016-2017 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.reaktivity.nukleus.maven.plugin.internal.ast;

public class AstNodeLocator
{

    private final AstScopeNode root;

    public AstNodeLocator(AstScopeNode root)
    {
        this.root = root;
    }

    public AstNode locateNode(
        AstScopeNode parent,
        String searchName,
        String currentPrefix)
    {
        if (parent == null)
        {
            parent = root;
        }

        String currentName = parent.name();
        if (currentPrefix != null)
        {
            currentName = currentPrefix + currentName;
        }

        if (searchName.equals(currentName))
        {
            return parent;
        }

        for (AstScopeNode child : parent.scopes())
        {
            AstNode foundNode = locateNode(child, searchName, currentName + "::");
            if (foundNode != null)
            {
                return foundNode;
            }
        }

        for (AstStructNode child : parent.structs())
        {
            AstNode foundNode = locateNode(child, searchName, currentName + "::");
            if (foundNode != null)
            {
                return foundNode;
            }
        }

        for (AstUnionNode child : parent.unions())
        {
            AstNode foundNode = locateNode(child, searchName, currentName + "::");
            if (foundNode != null)
            {
                return foundNode;
            }
        }

        for (AstEnumNode child : parent.enums())
        {
            AstNode foundNode = locateNode(child, searchName, currentName + "::");
            if (foundNode != null)
            {
                return foundNode;
            }
        }

        return null;
    }

    private AstNode locateNode(
        AstStructNode node,
        String searchName,
        String currentPrefix)
    {
        String currentName = node.name();
        if (currentPrefix != null)
        {
            currentName = currentPrefix + currentName;
        }
        if (searchName.equals(currentName))
        {
            return node;
        }
        return null;
    }

    private AstNode locateNode(
        AstUnionNode node,
        String searchName,
        String currentPrefix)
    {
        String currentName = node.name();
        if (currentPrefix != null)
        {
            currentName = currentPrefix + currentName;
        }
        if (searchName.equals(currentName))
        {
            return node;
        }
        return null;
    }

    private AstNode locateNode(
        AstEnumNode node,
        String searchName,
        String currentPrefix)
    {
        String currentName = node.name();
        if (currentPrefix != null)
        {
            currentName = currentPrefix + currentName;
        }
        if (searchName.equals(currentName))
        {
            return node;
        }
        return null;
    }
}
