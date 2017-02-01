package uk.gov.ons.ctp.sdx.service.impl;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.distributed.DistributedLockManager;

@Slf4j
public class SftpAdviceInterceptor implements MethodInterceptor {

    @Inject
    private DistributedLockManager sDXLockManager;
    
    /**
     * Clean up scheduler on bean destruction
     *
     */
    @PreDestroy
    public void cleanUp() {
      sDXLockManager.unlockInstanceLocks();
    }
    
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
      try {
        if (!sDXLockManager.isLocked("sftpScheduler")) {
          if (sDXLockManager.lock("sftpScheduler")) {
            return invocation.proceed();
          } 
        }
      log.debug("No advice given");
      return new Boolean(false); 
      } finally { 
        sDXLockManager.unlock("sftpScheduler");
      }
    }
}
