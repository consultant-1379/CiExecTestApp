/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2012
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package example.ejb.service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.*;
import javax.inject.Inject;

import org.slf4j.Logger;

import example.ejb.ServiceRemote;

@Singleton
@Startup
public class ServiceStartupBean {

	@Inject
	private Logger logger;
	
/*	
	@EJB
	private ServiceRemote remote;*/

	@PostConstruct
	void onServiceStart() {
		this.logger.info("Starting service");
		try
		{
		//remote.tstSnmpCompnent();
		}
		catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
	}

	@PreDestroy
	void onServiceStop() {
		this.logger.info("Stopping service");
		
	}

}
