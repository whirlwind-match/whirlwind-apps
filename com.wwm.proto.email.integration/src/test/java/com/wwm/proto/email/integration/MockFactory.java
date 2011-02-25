package com.wwm.proto.email.integration;

import org.mockito.Mockito;
import org.springframework.beans.factory.FactoryBean;

/**
 * A Mockito mock factory to allow autowiring by type
 * @author Neale
 *
 */
public class MockFactory implements FactoryBean<Object> {

	private Object singletonInstance;
	
	private Class<?> type;
	
	@Override
	public synchronized Object getObject() throws Exception {
		
		if (singletonInstance == null){
			singletonInstance = Mockito.mock(type);
		}
		return singletonInstance;
	}

	@Override
	public Class<?> getObjectType() {
		return type;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}
}
