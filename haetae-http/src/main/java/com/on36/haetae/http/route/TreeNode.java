package com.on36.haetae.http.route;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

import static com.on36.haetae.http.route.RouteHelper.*;

public class TreeNode {
    
    private final List<TreeNode> children = new ArrayList<TreeNode>();
    
    /*
     * From the Java API documentation for the Pattern class:
     * Instances of this (Pattern) class are immutable and are safe for use by 
     * multiple concurrent threads. Instances of the Matcher class are not 
     * safe for such use.
     */
    private final Pattern pattern;
    
    private final PathElement pathElement;
    
    private Route route;
    
    private final TreeNodeComparator treeNodeComparator = new TreeNodeComparator();
    
    public TreeNode(PathElement elem) {
        
        this.pattern = compilePattern(elem);
        this.pathElement = elem;
    }
    
    private Pattern compilePattern(PathElement elem) {
        
        StringBuilder routeRegex = new StringBuilder("^");
        
        if (elem instanceof NamedParameterElement) {
            
            NamedParameterElement namedElem = (NamedParameterElement)elem;
            if (namedElem.hasRegex()) {
                routeRegex.append("(").append(namedElem.regex()).append(")");
            } else {
                routeRegex.append("([^").append(PATH_ELEMENT_SEPARATOR).append("]+)");
            }
            
        } else if (elem instanceof SplatParameterElement) {
            
            routeRegex.append("(.*)");
            
        } else {
            
            routeRegex.append(escapeNonCustomRegex(elem.name()));
        }
        
        routeRegex.append("$");
        return Pattern.compile(routeRegex.toString());
    }
    
    public boolean matches(String token) {
        
        return pattern().matcher(token).find();
    }
    
    public boolean matches(PathElement elem) {
        
        if (pathElement != null) {
            return pathElement.equals(elem);
        }
        
        return false;
    }
    
    public void addChild(TreeNode node) {
        
        children.add(node);
        Collections.sort(children, treeNodeComparator);
    }
    
    public List<TreeNode> getChildren() {
        
        return new ArrayList<TreeNode>(children);
    }
    
    public TreeNode getMatchingChild(PathElement elem) {
        
        for (TreeNode node : children) {
            if (node.matches(elem)) return node;
        }
        return null;
    }
    
    public TreeNode getMatchingChild(String token) {
        
        for (TreeNode node : children) {
            if (node.matches(token)) return node;
        }
        return null;
    }
    
    public boolean hasChildren() {
        return !children.isEmpty();
    }
    
    public boolean containsSplatChild() {
        return getSplatChild() != null;
    }
    
    public TreeNode getSplatChild() {
        
        for (TreeNode child : children) {
            if (child.pathElement instanceof SplatParameterElement) {
                return child;
            }
        }
        return null;
    }
    
    public Pattern pattern() {
        
        return pattern;
    }
    
    public boolean isSplat() {
        return pathElement instanceof SplatParameterElement;
    }
    
    public Route getRoute() {
        
        return route;
    }
    
    public void setRoute(Route route) {
        
        this.route = route;
    }
    
    public boolean hasRoute() {
        return this.route != null;
    }
    
    public String toString() {
        
        return pattern.toString();
    }
    
    private static class TreeNodeComparator implements Comparator<TreeNode> {
        
        public int compare(TreeNode node1, TreeNode node2) {
        
            String r1Elem = getElem(node1.pathElement);
            String r2Elem = getElem(node2.pathElement);
            
            return new PathElementComparator().compare(r1Elem, r2Elem);
        }
        
        private String getElem(PathElement element) {
            String elem = element.name();
            if (element instanceof NamedParameterElement) {
                elem = PARAM_PREFIX + elem;
            }
            return elem;
        }
    }
}
