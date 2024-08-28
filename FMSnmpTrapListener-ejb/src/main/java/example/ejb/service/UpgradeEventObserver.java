package example.ejb.service;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.slf4j.Logger;

import com.ericsson.oss.itpf.sdk.upgrade.UpgradeEvent;
import com.ericsson.oss.itpf.sdk.upgrade.UpgradePhase;
import com.ericsson.oss.itpf.sdk.upgrade.UpgradeController;

@ApplicationScoped
public class UpgradeEventObserver { 
	private Logger logger;

public void upgradeNotificationObserver(@Observes final UpgradeEvent event) {
         logger.info("upgradeNotificationObserver triggered..");
        final UpgradePhase phase = event.getPhase();
                switch (phase) {
                        case SERVICE_INSTANCE_UPGRADE_PREPARE:
                        	 logger.info("SERVICE_INSTANCE_UPGRADE_PREPARE..");   
                         // service performs some actions here 
                                  
                         // Now accept/reject the UpgradeEvent
                         event.accept("This service is happy to upgrade.");
                         break;
                        
                        // Service must accept all UpgradePhases that
                        // it will perform no specific action for.
                        case SERVICE_CLUSTER_UPGRADE_PREPARE:
                        case SERVICE_CLUSTER_UPGRADE_FAILED:
                        case SERVICE_CLUSTER_UPGRADE_FINISHED_SUCCESSFULLY:
                        case SERVICE_INSTANCE_UPGRADE_FAILED:
                        case SERVICE_INSTANCE_UPGRADE_FINISHED_SUCCESSFULLY:
                        	logger.info("SERVICE_CLUSTER_UPGRADE_PREPARE OR SERVICE_CLUSTER_UPGRADE_FAILED OR SERVICE_CLUSTER_UPGRADE_FINISHED_SUCCESSFULLY OR" +
                        			"SERVICE_INSTANCE_UPGRADE_FAILED OR SERVICE_INSTANCE_UPGRADE_FINISHED_SUCCESSFULLY CALLED..");  
                         event.accept("OK");
                         break;
                        
                        default:
                        	logger.info("upgradeNotificationObserver default clled..");  
                         event.reject("Unexpected UpgradePhase");
                         break;
                }
                
        }
}