package com.on36.haetae.manager.process;

import java.util.Map;

/**
 * @author zhanghr
 * @date 2016年3月19日
 */
public interface ProcessManager {

	Map<String, Object> process(String...args);

	int killProcess(int pid);

	int killProcess(ProcessTO process);
}
