package org.haetae.common.conf;

import org.junit.Assert;
import org.junit.Test;

import com.on36.haetae.common.conf.Configuration;

/**
 * @author zhanghr
 * @date 2016年1月30日 
 */
public class ConfigrationTest {

	@Test
	public void testNULL() {
		String value = Configuration.create().getString("nihao");
		Assert.assertEquals(null,value);
	}
}
