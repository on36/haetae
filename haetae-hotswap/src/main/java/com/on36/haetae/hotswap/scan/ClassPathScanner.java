package com.on36.haetae.hotswap.scan;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

class ClassPathScanner implements Scanner {
	// scan for files inside JAR file - e.g.
	public static final String JAR_URL_SEPARATOR = "!/";
	public static final String JAR_URL_PREFIX = "jar:";
	public static final String FILE_URL_PREFIX = "file:";

	@Override
	public void scan(ClassLoader classLoader, String path,
			ScannerVisitor visitor) throws IOException {
		// find all directories - classpath directory or JAR
		path = path.replace(".", "/");
		Enumeration<URL> en = classLoader == null ? ClassLoader
				.getSystemResources(path) : classLoader.getResources(path);
		while (en.hasMoreElements()) {
			URL pluginDirURL = en.nextElement();
			File pluginDir = new File(pluginDirURL.getFile());
			if (pluginDir.isDirectory()) {
				scanDirectory(pluginDir, visitor);
			} else {
				// JAR file
				String uri;
				try {
					uri = pluginDirURL.toURI().toString();
				} catch (URISyntaxException e) {
					throw new IOException("Illegal directory URI "
							+ pluginDirURL, e);
				}

				if (uri.startsWith(JAR_URL_PREFIX)) {
					String jarFile = uri.substring(JAR_URL_PREFIX.length());
					scanJar(jarFile, visitor);
				} else {
				}
			}
		}
	}

	/**
	 * Recursively scan the directory.
	 *
	 * @param fileDir
	 *            directory.
	 * @param visitor
	 *            callback
	 * @throws IOException
	 *             exception from a visitor
	 */
	protected void scanDirectory(File fileDir, ScannerVisitor visitor)
			throws IOException {
		if (fileDir == null)
			throw new IllegalArgumentException("fileDir shound be not null");
		File[] fileList = fileDir.listFiles();
		if (fileList != null && fileList.length > 0) {
			for (File file : fileList) {
				if (file.isDirectory()) {
					scanDirectory(file, visitor);
				} else if (file.isFile() && file.getName().endsWith(".class")) {
					visitor.visit(file.getAbsolutePath());
				}
			}
		}
	}

	/**
	 * Scan JAR file for all entries. Resolve the JAR file itself and than
	 * iterate all entries and call visitor.
	 *
	 * @param urlFile 
	 *           fileName
	 * @param visitor
	 *            callback
	 * @throws IOException
	 *             exception from a visitor
	 */
	private void scanJar(String urlFile, ScannerVisitor visitor)
			throws IOException {

		int separatorIndex = urlFile.indexOf(JAR_URL_SEPARATOR);
		JarFile jarFile;
		String rootEntryPath;

		if (separatorIndex != -1) {
			String jarFileUrl = urlFile.substring(0, separatorIndex);
			rootEntryPath = urlFile.substring(separatorIndex
					+ JAR_URL_SEPARATOR.length());
			jarFile = getJarFile(jarFileUrl);
		} else {
			rootEntryPath = "";
			jarFile = new JarFile(urlFile);
		}

		if (!"".equals(rootEntryPath) && !rootEntryPath.endsWith("/")) {
			rootEntryPath = rootEntryPath + "/";
		}

		for (Enumeration<JarEntry> entries = jarFile.entries(); entries
				.hasMoreElements();) {
			JarEntry entry = entries.nextElement();
			String entryPath = entry.getName();

			// class files inside entry
			if (entryPath.startsWith(rootEntryPath)
					&& entryPath.endsWith(".class")) {
				visitor.visit(entryPath);
			}
		}
	}

	/**
	 * Resolve the given jar file URL into a JarFile object.
	 */
	protected JarFile getJarFile(String jarFileUrl) throws IOException {
		if (jarFileUrl.startsWith(FILE_URL_PREFIX)) {
			try {
				return new JarFile(toURI(jarFileUrl).getSchemeSpecificPart());
			} catch (URISyntaxException ex) {
				// Fallback for URLs that are not valid URIs (should hardly ever
				// happen).
				return new JarFile(jarFileUrl.substring(FILE_URL_PREFIX
						.length()));
			}
		} else {
			return new JarFile(jarFileUrl);
		}
	}

	/**
	 * Create a URI instance for the given location String, replacing spaces
	 * with "%20" quotes first.
	 *
	 * @param location
	 *            the location String to convert into a URI instance
	 * @return the URI instance
	 * @throws URISyntaxException
	 *             if the location wasn't a valid URI
	 */
	public static URI toURI(String location) throws URISyntaxException {
		return new URI(location.replace(" ", "%20"));
	}
}
