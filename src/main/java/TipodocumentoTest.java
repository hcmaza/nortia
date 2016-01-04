/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

//
//import ar.edu.undec.nortia.controller.TipodocumentoFacade;
//import javax.ejb.EJB;
//import org.jboss.arquillian.container.test.api.Deployment;
//import org.jboss.arquillian.testng.Arquillian;
//import org.jboss.shrinkwrap.api.ShrinkWrap;
//import org.jboss.shrinkwrap.api.spec.JavaArchive;
//import org.testng.Assert;
//import org.testng.annotations.Test;
//
///**
// *
// * @author Hugo
// */
//
//public class TipodocumentoTest extends Arquillian {
//    
//    public TipodocumentoTest() {
//    }
//
//    @Deployment
//        public static JavaArchive createDeployment() {
//        JavaArchive jar = ShrinkWrap.create(JavaArchive.class, "hgrussEjb.jar");
//        jar.addPackage(Tipodocumento.class.getPackage());
//        jar.addAsResource("test-persistence.xml", "META-INF/persistence.xml");
//        // jar.addClass(GrussGottEjbLocal.class).addClass(GrussGottEjb.class);
//        // jar.addClass(UserDao.class).addClass(UserDaoLocal.class);
//        // jar.addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml");
//  
//        System.out.println(jar.toString(true));
//        return jar;
//       }
//
//       @EJB
//       TipodocumentoFacade grusEjb;
//
//       // injecting em does not work for jb5 + arquillian, just use DAOs and
//       // business methods for inserting, deleting of entities
//       // @PersistenceContext
//       // EntityManager em;
//
//       @Test
//       public void sayHalloTxt() throws Exception {
//            Tipodocumento hehe = new Tipodocumento();
//            hehe.setTipodocumento("xxx");
//            grusEjb.create(hehe);
//            
//            Tipodocumento result = grusEjb.find(hehe);
//            Assert.assertEquals("xxx", result.getTipodocumento());
//
//            System.out.println("#### found db entry text= " + result.getTipodocumento()
//              + " id=" + result.getTipodocumentoid());
//           }
//}
