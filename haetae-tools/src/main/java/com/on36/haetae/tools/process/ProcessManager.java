package com.on36.haetae.tools.process;

import java.util.List;
import java.util.Map;

/**
 * @author zhanghr
 * @date 2016年3月19日
 */
public interface ProcessManager {

	Map<String, Object> process(String...args);

	int killProcess(int port);

	int killProcess(String port);
	
	int killProcess(ProcessTO process);
	
	List<ProcessTO> listProcess();
}
