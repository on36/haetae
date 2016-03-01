package com.on36.haetae.hotswap.scan;

import java.io.IOException;

interface Scanner {

	public void scan(ClassLoader classLoader, String path,
			ScannerVisitor visitor) throws IOException;
}