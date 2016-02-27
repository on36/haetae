package com.on36.haetae.http.route;

import java.util.ArrayList;
import java.util.List;

import static com.on36.haetae.http.route.RouteHelper.*;

public class TreeRouter implements Router {

    private TreeNode root; 
    
    public synchronized void add(Route route) {
        
        List<PathElement> pathElements = route.getPathElements();
        if (!pathElements.isEmpty() && route.endsWithPathSeparator()) {
            pathElements.add(
                    new StaticPathElement(PATH_ELEMENT_SEPARATOR, pathElements.size() - 1));
        }
        
        if (root == null) {
            root = new TreeNode(new StaticPathElement(PATH_ELEMENT_SEPARATOR, 0));
        }
        
        TreeNode currentNode = root;
        for (PathElement elem : pathElements) {
            
            TreeNode matchingNode = currentNode.getMatchingChild(elem);
            if (matchingNode == null) {
                TreeNode newChild = new TreeNode(elem);
                currentNode.addChild(newChild);
                currentNode = newChild;
            } else {
                currentNode = matchingNode;
            }
        }
        currentNode.setRoute(route);
    }
    
    /**
     * Returns a Route that matches the given URL path.
     * Note that the path is expected to be an undecoded URL path.
     * The router will handle any decoding that might be required.
     * 
     *  @param path an undecoded URL path
     *  @return the matching route, or null if none is found
     */
    public Route route(String path) {
        
        List<String> searchTokens = getPathAsSearchTokens(path);
        
        /* handle the case where path is '/' and route '/*' exists */
        if (searchTokens.isEmpty() && root.containsSplatChild() && !root.hasRoute()) {
            return root.getSplatChild().getRoute();
        }
        
        TreeNode currentMatchingNode = root;
        for (String token : searchTokens) {
            
            TreeNode matchingNode = currentMatchingNode.getMatchingChild(token);
            if (matchingNode == null) return null;
            currentMatchingNode = matchingNode;
            
            if (currentMatchingNode.isSplat() && 
                    !currentMatchingNode.hasChildren()) {
                return currentMatchingNode.getRoute();
            }
        }
        
        return currentMatchingNode.getRoute();
    }
    
    private List<String> getPathAsSearchTokens(String path) {
        
        List<String> tokens = new ArrayList<String>();
        path = urlDecodeForRouting(path);
        String[] pathElements = getPathElements(path);
        for (int i = 0; i < pathElements.length; i++) {
            String token = pathElements[i];
            if (token != null && token.trim().length() > 0) {
                tokens.add(token);
            }
        }        
        if (!tokens.isEmpty() && 
                path.trim().endsWith(PATH_ELEMENT_SEPARATOR)) {
            tokens.add(PATH_ELEMENT_SEPARATOR);
        }
        return tokens;
    }
    
    public TreeNode getRoot() {
        
        return root;
    }
}
