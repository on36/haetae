package com.on36.haetae.http.route;

import static com.on36.haetae.http.route.RouteHelper.CUSTOM_REGEX_PATTERN;
import static com.on36.haetae.http.route.RouteHelper.PARAM_PREFIX;
import static com.on36.haetae.http.route.RouteHelper.PATH_ELEMENT_DOC;
import static com.on36.haetae.http.route.RouteHelper.PATH_ELEMENT_ROOT;
import static com.on36.haetae.http.route.RouteHelper.PATH_ELEMENT_SEPARATOR;
import static com.on36.haetae.http.route.RouteHelper.WILDCARD;
import static com.on36.haetae.http.route.RouteHelper.urlDecodeForPathParams;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

public class Route {

	public static final Route PATH_ROOT = new Route();
	public static final Route PATH_DOC = new Route(PATH_ELEMENT_DOC);

	private final String resourcePath;
	private final List<PathElement> allPathElements;
	private final List<NamedParameterElement> namedParamElements;
	private final List<SplatParameterElement> splatParamElements;
	private final List<StaticPathElement> staticPathElements;

	private Route() {
		this.resourcePath = PATH_ELEMENT_ROOT;

		this.allPathElements = new ArrayList<PathElement>();
		this.splatParamElements = new ArrayList<SplatParameterElement>();
		this.staticPathElements = new ArrayList<StaticPathElement>();
		this.namedParamElements = new ArrayList<NamedParameterElement>();

		extractPathElements();
	}

	public Route(String paramPath) {

		if (PATH_ELEMENT_SEPARATOR.equals(paramPath)) {
			throw new IllegalArgumentException(
					"path cannot be " + PATH_ELEMENT_SEPARATOR);
		}
		if (PATH_ELEMENT_ROOT.equals(paramPath)) {
			throw new IllegalArgumentException(
					"path cannot be " + PATH_ELEMENT_ROOT);
		}

		this.resourcePath = paramPath;

		this.allPathElements = new ArrayList<PathElement>();
		this.splatParamElements = new ArrayList<SplatParameterElement>();
		this.staticPathElements = new ArrayList<StaticPathElement>();
		this.namedParamElements = new ArrayList<NamedParameterElement>();

		extractPathElements();
	}

	private void extractPathElements() {

		Matcher m = CUSTOM_REGEX_PATTERN.matcher(resourcePath);
		Map<String, String> regexMap = getRegexMap(m);
		String path = m.replaceAll("");

		String[] pathElements = RouteHelper.getPathElements(path);
		for (int i = 0; i < pathElements.length; i++) {

			String currentElement = pathElements[i];

			if (currentElement.startsWith(PARAM_PREFIX)) {

				currentElement = currentElement.substring(1);
				NamedParameterElement named = new NamedParameterElement(
						currentElement, i, regexMap.get(currentElement));
				namedParamElements.add(named);
				allPathElements.add(named);

			} else if (currentElement.equals(WILDCARD)) {

				SplatParameterElement splat = new SplatParameterElement(i);
				splatParamElements.add(splat);
				allPathElements.add(splat);

			} else {

				if (currentElement.trim().length() < 1)
					continue;
				StaticPathElement staticElem = new StaticPathElement(
						currentElement, i);
				staticPathElements.add(staticElem);
				allPathElements.add(staticElem);
			}
		}
	}

	/*
	 * Returns a map of named param names to their regex, for named params that
	 * have a regex. e.g. {"name" -> "[a-z]+"}
	 */
	private Map<String, String> getRegexMap(Matcher m) {

		Map<String, String> regexMap = new HashMap<String, String>();
		while (m.find()) {
			String regex = resourcePath.substring(m.start() + 1, m.end() - 1);
			int namedParamStart = m.start() - 1;
			int namedParamEnd = m.start();
			String namedParamName = resourcePath.substring(namedParamStart,
					namedParamEnd);
			while (!namedParamName.startsWith(PARAM_PREFIX)) {
				namedParamStart--;
				namedParamName = resourcePath.substring(namedParamStart,
						namedParamEnd);
			}
			namedParamName = resourcePath.substring(namedParamStart + 1,
					namedParamEnd);
			regexMap.put(namedParamName, regex);
		}
		return regexMap;
	}

	public String getResourcePath() {

		return resourcePath.startsWith(PATH_ELEMENT_ROOT)
				? resourcePath.replace(PATH_ELEMENT_ROOT, "")
				: resourcePath;
	}

	public List<PathElement> getPathElements() {

		return new ArrayList<PathElement>(allPathElements);
	}

	public List<NamedParameterElement> getNamedParameterElements() {

		return new ArrayList<NamedParameterElement>(namedParamElements);
	}

	public List<SplatParameterElement> getSplatParameterElements() {

		return new ArrayList<SplatParameterElement>(splatParamElements);
	}

	public List<StaticPathElement> getStaticPathElements() {

		return new ArrayList<StaticPathElement>(staticPathElements);
	}

	/**
	 * Use of this method assumes the path given matches this Route.
	 * 
	 * @return the value of the named parameter in the path, or null if no named
	 *         parameter exists with the given name
	 */
	public String getNamedParameter(String paramName, String path) {

		List<NamedParameterElement> pathParams = getNamedParameterElements();
		String[] pathTokens = RouteHelper.getPathElements(path);

		for (NamedParameterElement pathParam : pathParams) {

			if (pathParam.name().equals(paramName)) {
				return urlDecodeForPathParams(pathTokens[pathParam.index()]);
			}
		}

		return null;
	}

	/**
	 * Use of this method assumes the path given matches this Route.
	 * 
	 * @return the value of the splat parameter at the given index, or null if
	 *         the splat parameter index does not exist
	 */
	public String getSplatParameter(int index, String path) {

		String[] splat = splat(path);
		if (index > splat.length - 1) {
			return null;
		}
		return splat[index];
	}

	/**
	 * Use of this method assumes the path given matches this Route.
	 */
	public String[] splat(String path) {

		List<SplatParameterElement> splatParams = getSplatParameterElements();
		String[] pathTokens = RouteHelper.getPathElements(path, false);
		String[] splat = new String[splatParams.size()];

		for (int i = 0; i < splatParams.size(); i++) {

			SplatParameterElement splatParam = splatParams.get(i);
			splat[i] = urlDecodeForPathParams(pathTokens[splatParam.index()]);

			if (i + 1 == splatParams.size() && endsWithSplat()) {
				/* this is the last splat param and the route ends with splat */
				for (int j = splatParam.index()
						+ 1; j < pathTokens.length; j++) {
					splat[i] = splat[i] + PATH_ELEMENT_SEPARATOR
							+ urlDecodeForPathParams(pathTokens[j]);
				}
			}
		}

		return splat;
	}

	private boolean endsWithSplat() {
		return resourcePath.endsWith(WILDCARD);
	}

	public boolean endsWithPathSeparator() {
		return resourcePath.endsWith(PATH_ELEMENT_SEPARATOR);
	}

	public boolean hasPathElements() {
		return !allPathElements.isEmpty();
	}

	public String toString() {

		return resourcePath;
	}

	public int hashCode() {

		int hash = 1;
		hash = hash * 13 + (resourcePath == null ? 0 : resourcePath.hashCode());
		return hash;
	}

	public boolean equals(Object o) {

		if (o == null)
			return false;
		if (o == this)
			return true;
		if (!(o instanceof Route))
			return false;
		Route that = (Route) o;
		return (this.resourcePath == null ? that.resourcePath == null
				: this.resourcePath.equals(that.resourcePath));
	}
}
