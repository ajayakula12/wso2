package com.uol.utils;

import org.apache.synapse.MessageContext;
import org.apache.synapse.core.axis2.Axis2SynapseEnvironment;
import org.apache.synapse.mediators.AbstractMediator;
import org.apache.synapse.mediators.base.SequenceMediator;

public   class Test extends AbstractMediator{

	@Override
	public boolean mediate(MessageContext synCtx) {
		log.info("ajay akula");
		org.apache.synapse.core.SynapseEnvironment synapseEnv = synCtx.getEnvironment();
		   for (int i = 0; i < 5; i++) {
	            // Create a new message context for each iteration
	             Axis2SynapseEnvironment synEnv = (Axis2SynapseEnvironment) synCtx.getEnvironment();

	            // Create a new Synapse message context for sequence invocation
	            MessageContext sequenceCtx = synEnv.createMessageContext();

	            // Set properties, headers, or payload if needed
	            // sequenceCtx.setProperty(...);
	            // sequenceCtx.setEnvelope(...);

	            // Invoke the sequence
	            SequenceMediator sqm=new SequenceMediator();
	           sqm.setName("testSeq");
	           sqm.mediate(synCtx);
	            //synCtx.getEnvelope().detach();
	        }
	 
		return false;
	}
	
	

	

}
