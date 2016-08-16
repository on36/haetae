package com.on36.haetae.manager.process;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.on36.haetae.tools.utils.ProcessUtil;

/**
 * @author zhanghr
 * @date 2016年3月19日
 */
public abstract class BaseProcessManager implements ProcessManager {

	protected Map<Integer, ProcessTO> processMap = new HashMap<Integer, ProcessTO>();

	@Override
	public Map<String, Object> process(String... args) {
		Map<String, Object> result = ProcessUtil.execHaetaeServer(false, args);
		if (true == (Boolean) result.get("success")) {
			int port = (int) result.get("port");
			int pid = (int) result.get("pid");
			processMap.put(port, new ProcessTO(pid, port, null, null, null));
		}
		return result;
	}

	@Override
	public int killProcess(int port) {
		int pid = getPid(port);
		if (pid > 0) {
			ProcessUtil.execAndAutoCloseble(killPid(pid));
			return 1;
		}
		return -1;
	}

	@Override
	public int killProcess(String port) {
		return killProcess(Integer.parseInt(port));
	}

	@Override
	public int killProcess(ProcessTO process) {
		return killProcess(process.getPort());
	}

	@Override
	public List<ProcessTO> listProcess() {

		return null;
	}

	protected abstract List<String> killPid(int pid);

	protected abstract int findPid(int port);

	protected int getPid(int port) {
		if (getProcess(port) != null)
			return getProcess(port).getPid();
		return findPid(port);
	}

	protected ProcessTO getProcess(int port) {
		return processMap.get(port);
	}

	protected List<ProcessTO> getProcesses() {
		return null;
	}
}