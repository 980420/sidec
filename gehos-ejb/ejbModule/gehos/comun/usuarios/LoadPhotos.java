package gehos.comun.usuarios;

import gehos.configuracion.management.entity.Usuario_configuracion;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.servlet.ServletContext;

import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.core.SeamResourceBundle;

@Name("loadPhotos")
public class LoadPhotos {
	
	@In
	private EntityManager entityManager;

	private void resizeImage(String url, String destination){
        try{
            BufferedImage originalImage = ImageIO.read(new URL(url));
            int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizedImage = new BufferedImage(74, 74, type);
            Graphics2D g = resizedImage.createGraphics();
            g.drawImage(originalImage, 0, 0, 74, 74, null);
            g.dispose(); 
            ImageIO.write(resizedImage, "png", new File(destination)); 
        }
        catch(IOException e){}
    }
	
	@SuppressWarnings("unchecked")
	public void loadPhotos(){
		String rootpath = System.getProperty("jboss.server.home.dir") + "/deploy/gehos-ear.ear/gehos.war/resources/modCommon/userphotos";
		
		List<Usuario_configuracion> usuario = (List<Usuario_configuracion>)entityManager.createQuery("select u from Usuario_configuracion u " +
				"where u.username like '%@pdvsa.com' and (u.eliminado = false or u.eliminado <> null)")
				.getResultList();
		
		for (Usuario_configuracion usuario_configuracion : usuario) {
			java.io.File dir = new java.io.File(
					rootpath + "/"
					+ usuario_configuracion.getUsername() + ".png");
			if(!dir.exists()){
				String ced2 = usuario_configuracion.getCedula();				
				while(ced2.length() < 9){
					ced2 = "0" + ced2;
				}				
				resizeImage("http://ccschu14.pdvsa.com/photos/" + ced2 + ".jpg" , rootpath + "/" 
						+ usuario_configuracion.getUsername() + ".png");
			}
		}
	}
	
}
