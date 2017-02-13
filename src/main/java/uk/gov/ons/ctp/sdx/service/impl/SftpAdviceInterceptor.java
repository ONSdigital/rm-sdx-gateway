package uk.gov.ons.ctp.sdx.service.impl;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;

@Slf4j
public class SftpAdviceInterceptor implements MethodInterceptor {

    private static final String LOCK_ID = "sftpReceiptLock";
    
    @Inject
    private DistributedLockManager sdxLockManager;
    
    /**
     * Clean up scheduler on bean destruction
     *
     */
    @PreDestroy
    public void cleanUp() {
      sdxLockManager.unlockInstanceLocks();
    }
    
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
      try {
        if (!sdxLockManager.isLocked(LOCK_ID)) {
          if (sdxLockManager.lock(LOCK_ID)) {
            return invocation.proceed();
          } 
        }
      log.debug("No advice given");
      return new Boolean(false); 
      } finally { 
        sdxLockManager.unlock(LOCK_ID);
      }
    }
}
