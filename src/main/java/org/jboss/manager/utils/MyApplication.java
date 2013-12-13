package org.jboss.manager.utils;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.jboss.manager.ManagementController;
import org.jboss.manager.ServersController;

public class MyApplication extends Application {
	HashSet<Object> singletons = new HashSet<Object>();

	public MyApplication() {
		singletons.add(new ManagementController());
		singletons.add(new ServersController());
	}

	@Override
	public Set<Class<?>> getClasses() {
		HashSet<Class<?>> set = new HashSet<Class<?>>();
		return set;
	}

	@Override
	public Set<Object> getSingletons() {
		return singletons;
	}
}
