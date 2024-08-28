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

import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.ObjectMessage;

import org.slf4j.Logger;

import com.adventnet.snmp.beans.*;
import com.adventnet.snmp.snmp2.SnmpException;
import com.adventnet.snmp.snmp2.SnmpPDU;
import com.ericsson.oss.TrapReceiverService.ejb.registration.TrapReceiverClusterListener;
import com.ericsson.oss.itpf.sdk.eventbus.Channel;
import com.ericsson.oss.itpf.sdk.eventbus.annotation.Endpoint;
import com.ericsson.oss.itpf.sdk.instrument.annotation.InstrumentedBean;
import com.ericsson.oss.itpf.sdk.instrument.annotation.Profiled;

@Singleton
@Startup
public class TrapListenerStartupBean implements TrapListener {

	public static final int port = 162;
	public static  String address[]={"127.0.0.1"}; 

	@Inject
	TrapReceiverClusterListener trapReceiverClusterListener;
	@Inject
	@Endpoint("jms:/topic/SnmpTrapListenerTopic")
	private Channel channel;
	@Inject
	private Logger logger;

	private SnmpPDU snmpPDU=null;
	private static int trapReceived;
	private static int trapSent;
	
	
	@Profiled
	@PostConstruct
	void onServiceStart() throws Exception {
		logger.info("Starting  Trap receiver service");
		logger.info("is trap receiver master: "+trapReceiverClusterListener.getMasterState());
		String jbossaddress=getBindingAddress();
		address[0]=jbossaddress.toString();
		logger.info("binding address is"+address[0]);

		if (trapReceiverClusterListener.getMasterState()) 
		{		
			try{
				logger.info("trap listener is master strating the receiver");
				SnmpTrapReceiver receiver = new SnmpTrapReceiver();
				TrapListener listener = new TrapListener() {
					public void receivedTrap(TrapEvent trap) {

						logger.info("Got a trap from: "+trap.getRemoteHost());
						logger.info("trap is"+trap.getTrapPDU().printVarBinds());
						snmpPDU=trap.getTrapPDU();
						trapReceived++;
						logger.info("total Received pdu count is :"+trapReceived);
						sendMessage(snmpPDU);
						return;
					}
				};
				receiver.addTrapListener(listener);

				try {
					receiver.setPortWithExceptionMsg(port);
					receiver.setLocalAddresses(address);
				}
				catch(SnmpException e) {
					e.printStackTrace();
					System.err.println(e.getMessage());
				}


			}catch (Exception e) {
				e.printStackTrace();
				this.logger.error("ahh error is"+e);
			}
		}

		else
		{			
			logger.info("Sorry tapreceiver is not master So we can not start the Listener service");
			logger.info("Trap receiver not running at the moment..");
		}

	}

	@Profiled
	public void sendMessage(final SnmpPDU snmpPdu) {
		logger.info("sending pdu to channel:"+this.channel+"message paylod is:"+snmpPdu.toString());
		try
		{
			
			this.channel.send((Serializable) snmpPdu);
			logger.info("messagePayload sent on channel");
			trapSent++;
			logger.info("total number of pdu sent is :"+trapSent);
			logger.info("is trapreceived==trapsent"+(trapSent==trapReceived));
			

		}
		catch(Exception e)
		{
			logger.error("Exception while sending to topic"+e.getMessage());
		}


	}

	/**
	 * @return Binding IP Address of the JBOSS server
	 */

	public String getBindingAddress() throws UnknownHostException {
		String finalListeningIP = System.getProperty("jboss.bind.address");

		if (finalListeningIP != null) {
			if (finalListeningIP.equalsIgnoreCase("0.0.0.0")
					|| finalListeningIP.equalsIgnoreCase("127.0.0.1")) {
				finalListeningIP = InetAddress.getLocalHost().getHostAddress()
						.toString();
			}
		} else {
			finalListeningIP = InetAddress.getLocalHost().getHostAddress()
					.toString();
		}
		logger.info("finalListeningIP is:"+finalListeningIP);
		return finalListeningIP;
	}

	@Override
	public void receivedTrap(TrapEvent arg0) {
		// TODO Auto-generated method stub
		logger.info("it is nothing");
	}

}
