package utils.dismember;


/**
 * Created by luwj on 2015/11/30.
 */
public class TransactionUtil {

    /**
     * 开启一个新事务
     */
    public static void startTx() {
//        // Logger.debug("startTx for Thread: %s and waiting for new row insert for cds_conversion_queue ... ",
//        // Thread.currentThread());
//        if (JPA.local.get() != null) {
//            try {
//                JPA.local.get().entityManager.close();
//            } catch (Exception e) {
//            }
//            JPA.local.remove();
//        }
//        JPA jpa = new JPA();
//        jpa.entityManager = JPA.entityManagerFactory.createEntityManager();
//        jpa.entityManager.setFlushMode(FlushModeType.COMMIT);
//        jpa.entityManager.getTransaction().begin();
//        JPA.local.set(jpa);
    }
//
//    public static void closeTx(boolean rollback, boolean ignoreQueue) {
//        // Logger.debug("closeTx(%s) for Thread: %s", rollback, Thread.currentThread());
//        if (JPA.local.get() == null) {
//            return;
//        }
//        EntityManager manager = JPA.local.get().entityManager;
//        try {
//            DB.getConnection().setAutoCommit(false);
//        } catch (Exception e) {
//            play.Logger.error(e, "Why the driver complains here?", new Object[0]);
//        }
//
//        if(!ignoreQueue){
//            manager.createNativeQuery("select queue_end()").getResultList();
//        }
//
//        if (manager.getTransaction().isActive()) if ((rollback) || manager.getTransaction().getRollbackOnly()) manager.getTransaction().rollback();
//        else try {
//                manager.getTransaction().commit();
//            } catch (Throwable e) {
//                for (int i = 0; i < 10; ++i) {
//                    if ((e instanceof PersistenceException) && (e.getCause() != null)) {
//                        e = e.getCause();
//                        break;
//                    }
//                    e = e.getCause();
//                    if (e == null) {
//                        break;
//                    }
//                }
//                throw new JPAException("Cannot commit", e);
//            }
//    }
//
//    /**
//     * 结束事务
//     *
//     * @param rollback
//     */
//    public static void closeTx(boolean rollback) {
//        closeTx(rollback, true);
//    }
}
