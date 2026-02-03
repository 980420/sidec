package gehos.pki.ra;

import gehos.configuracion.management.entity.Nacion_configuracion;
import gehos.configuracion.management.entity.Usuario_configuracion;
import gehos.pki.actions.TrustManagerController;
import gehos.pki.entity.CaKeystore_pki;
import gehos.pki.entity.UsuarioKeystore_pki;
import gehos.pki.entity.Usuario_pki;
import gehos.pki.utils.Validations_pki;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.InvalidParameterException;
import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.PublicKey;
import java.security.Security;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.x509.BasicConstraints;
import org.bouncycastle.asn1.x509.X509Extensions;
import org.bouncycastle.jce.X509Principal;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.x509.X509V3CertificateGenerator;
import org.bouncycastle.x509.extension.AuthorityKeyIdentifierStructure;
import org.bouncycastle.x509.extension.SubjectKeyIdentifierStructure;
import org.component.javasign1.trust.ITrustServices;
import org.component.javasign1.trust.TrustFactory;
import org.component.javasign1.trust.TrusterType;
import org.component.javasign2.libreria.utilidades.UtilidadCertificados;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.core.SeamResourceBundle;
import org.jboss.seam.faces.FacesMessages;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

@Name("raFunctions")
@Scope(ScopeType.CONVERSATION)
public class RaFunctions {

	@In
	EntityManager entityManager;
	@In(create = true)
	FacesMessages facesMessages;

	private char[] stringPass;

	public RaFunctions() {
		this.stringPass = "anotherstringusing".toCharArray();
	}

	public char[] getStringPass() {
		return stringPass;
	}

	public void setStringPass(char[] stringPass) {
		this.stringPass = stringPass;
	}

	private static final String rootalias = "rootCA";

	private boolean crearNueva = true;

	private boolean execute = true;

	private CaKeystore_pki ca_keystore = null;

	private boolean error = false;

	private String rootCacountry = null;
	private String rootCastate = null;
	private String rootCalocality = null;
	private String rootCaorganization = null;
	private String rootCaorganizationalUnit = null;
	private String rootCacommonName = null;

	public void generateCertificatesAutomatically() {
		@SuppressWarnings("unchecked")
		List<Usuario_configuracion> usuarios = entityManager
				.createQuery(
						"from Usuario_configuracion u where u.id not in(select uk.usuario.id from UsuarioKeystore_pki uk)")
				.getResultList();
		for (Usuario_configuracion usuario : usuarios) {
			try {
				this.createUserKeyStore(usuario);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public X509Principal generateX509Name(final String country,
			final String state, final String locality,
			final String organization, final String organizationalUnit,
			final String commonName, String uid) {
		Hashtable<DERObjectIdentifier, String> attrs = new Hashtable<DERObjectIdentifier, String>();
		Vector<DERObjectIdentifier> order = new Vector<DERObjectIdentifier>();

		if (country != null && !country.equals("")) {
			attrs.put(X509Principal.C, country);
			order.addElement(X509Principal.C);
		}
		if (state != null && !state.equals("")) {
			attrs.put(X509Principal.ST, state);
			order.addElement(X509Principal.ST);
		}
		if (locality != null && !locality.equals("")) {
			attrs.put(X509Principal.L, locality);
			order.addElement(X509Principal.L);
		}
		if (organization != null && !organization.equals("")) {
			attrs.put(X509Principal.O, organization);
			order.addElement(X509Principal.O);
		}
		if (organizationalUnit != null && !organizationalUnit.equals("")) {
			attrs.put(X509Principal.OU, organizationalUnit);
			order.addElement(X509Principal.OU);
		}
		if (commonName != null && !commonName.equals("")) {
			attrs.put(X509Principal.CN, commonName);
			order.addElement(X509Principal.CN);
		}

		if (uid != null && !uid.equals("")) {
			attrs.put(X509Principal.UID, uid);
			order.addElement(X509Principal.UID);
		}

		// Make sure they gave us at least one element
		if (order.size() > 0) {
			return new X509Principal(order, attrs);
		} else {
			return null;
		}
	}

	private static final int CERT_TYPE_USER = 1;
	private static final int CERT_TYPE_CA = 0;

	public X509Certificate createCertificate(PublicKey keyToCertify, // The key
			// to
			// make
			// into
			// a
			// certificate
			PrivateKey signingKey, // The key to sign with (typically the CA's
			// key)
			X509Certificate signingCert, // The cert matching signingKey
			X509Principal certDN, BigInteger serialNumber, // The serial number
			// for
			// the new certificate
			int certType) {

		Date startDate = null;
		Date endDate = null;

		startDate = new Date();
		// ms/s s/m m/h h/d
		long millisPerDay = 1000 * 60 * 60 * 24;
		endDate = new Date(startDate.getTime() + (3650 * 2 * millisPerDay));

		// Create our certificate generator
		X509V3CertificateGenerator certGen = new X509V3CertificateGenerator();

		// Fill in all of the fields
		if (certType == CERT_TYPE_CA) {
			certGen.setIssuerDN(certDN);
		} else {
			// Get the CA's DN for our issuer DN
			// It takes a little trickery to go from a JCE object
			// to a BouncyCastle object.

			certGen.setIssuerDN(signingCert.getSubjectX500Principal());

		}
		certGen.setSubjectDN(certDN);

		if (certType == CERT_TYPE_CA) {
			// As required by section 4.2.1.10 of RFC 2459
			// Just as a note, OpenSSL does not set this as critical
			certGen.addExtension(X509Extensions.BasicConstraints, true,
					new BasicConstraints(true));
		}
		try {
			certGen.addExtension(X509Extensions.SubjectKeyIdentifier, false,
					new SubjectKeyIdentifierStructure(keyToCertify));
		} catch (CertificateParsingException e1) {

			e1.printStackTrace();
		}
		if (certType != CERT_TYPE_CA) {
			try {
				certGen.addExtension(X509Extensions.AuthorityKeyIdentifier,
						false, new AuthorityKeyIdentifierStructure(signingCert));
			} catch (CertificateParsingException e) {

				e.printStackTrace();
			}
		}
		// ***We should also be setting the Key Usage extension
		certGen.setNotBefore(startDate);
		certGen.setNotAfter(endDate);
		certGen.setSerialNumber(serialNumber);
		// ***This should be prompted for or user-settable somehow. It
		// needs to be based off of what createKeyPair does.
		// See http://www.bouncycastle.org/specifications.html for
		// supported signature algorithms.
		certGen.setSignatureAlgorithm("MD5withRSA");
		certGen.setPublicKey(keyToCertify);

		// Generate a certificate using the key provided
		X509Certificate cert = null;
		try {
			cert = certGen.generate(signingKey);
		} catch (SecurityException e) {

			System.err.println("Error when generating certificate:  "
					+ e.getMessage());
			return null;
		} catch (SignatureException e) {

			System.err.println("Error when generating certificate:  "
					+ e.getMessage());
			return null;
		} catch (InvalidKeyException e) {

			System.err.println("Error when generating certificate:  "
					+ e.getMessage());
			return null;
		} catch (CertificateEncodingException e) {
			System.err.println("Error when generating certificate:  "
					+ e.getMessage());
		} catch (IllegalStateException e) {
			System.err.println("Error when generating certificate:  "
					+ e.getMessage());
		} catch (NoSuchAlgorithmException e) {
			System.err.println("Error when generating certificate:  "
					+ e.getMessage());
		}

		return cert;
	}

	// Creates and returns a public/private pair of keys
	// Returns null if there is an error
	private KeyPair createKeyPair() {
		// ***We should prompt for these
		String keyType = "RSA";
		int keySize = 2048;

		KeyPair keypair = null;
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance(keyType);
			keyGen.initialize(keySize);
			keypair = keyGen.generateKeyPair();
		} catch (NoSuchAlgorithmException e) {

			System.err.println("Failed to generate a pair of " + keyType
					+ " keys:  " + e.getMessage());
			return null;
		} catch (InvalidParameterException e) {

			System.err.println("Failed to generate a pair of " + keyType
					+ " keys:  " + e.getMessage());
			return null;
		}

		return keypair;
	}

	public Certificate getCertificate(Usuario_configuracion usuario) {
		UsuarioKeystore_pki userKey = null;
		try {
			userKey = (UsuarioKeystore_pki) entityManager
					.createQuery(
							"select userKey from UsuarioKeystore_pki userKey "
									+ "where userKey.usuario.id = :Id")
					.setParameter("Id", usuario.getId()).getSingleResult();

		} catch (NoResultException e) {
			return null;
		}
		KeyStore newKS = null;
		// Create a new KeyStore
		try {
			Provider p = new BouncyCastleProvider();
			Security.addProvider(p);
			newKS = KeyStore.getInstance("PKCS12", "BC");
			newKS.load(
					new ByteArrayInputStream(new BASE64Decoder()
							.decodeBuffer(userKey.getKeystore())), this
							.getStringPass());
		} catch (KeyStoreException e) {

			e.printStackTrace();
			return null;
		} catch (NoSuchProviderException e) {

			e.printStackTrace();
			return null;
		} catch (Exception e) {

			e.printStackTrace();
			return null;
		}
		Certificate cert;
		try {
			cert = newKS.getCertificate(usuario.getUsername());
		} catch (KeyStoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return cert;

	}

	public boolean modUserKeyStore(Long id) throws Exception {
		Usuario_configuracion user;
		user = (Usuario_configuracion) entityManager.find(
				Usuario_configuracion.class, id);
		return modUserKeyStore(user);
	}

	public boolean modUserKeyStore(Usuario_configuracion usuario)
			throws Exception {
		UsuarioKeystore_pki userKey = null;
		try {
			userKey = (UsuarioKeystore_pki) entityManager
					.createQuery(
							"select userKey from UsuarioKeystore_pki userKey "
									+ "where userKey.usuario.id = :Id")
					.setParameter("Id", usuario.getId()).getSingleResult();

		} catch (NoResultException e) {
			return this.createUserKeyStore(usuario);

		}
		// Obteniendo el KeyStore
		CaKeystore_pki ca = null;
		try {
			ca = (CaKeystore_pki) entityManager.createQuery(
					"select ca from "
							+ "CaKeystore_pki ca where ca.eliminado = false")
					.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
			facesMessages.add("msg_notExistRaiz_modConfig");
			return false;
		}

		KeyStore caKeyStore;
		try {
			caKeyStore = KeyStore.getInstance("JKS");
			caKeyStore.load(
					new ByteArrayInputStream(new BASE64Decoder()
							.decodeBuffer(ca.getKeystore())), this
							.getStringPass());
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}

		// Load the CA key and cert from the keystore for
		// passing along to createCertificate()
		PrivateKey caKey = null;
		X509Certificate caCert = null;
		try {
			caKey = (PrivateKey) caKeyStore.getKey(getrootalias(),
					this.getStringPass());
			caCert = (X509Certificate) caKeyStore
					.getCertificate(getrootalias());
		} catch (KeyStoreException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
			return false;
		} catch (UnrecoverableKeyException e) {

			e.printStackTrace();
			return false;
		}

		if (userKey != null) {
			KeyStore newKS = null;
			// Create a new KeyStore
			try {
				Provider p = new BouncyCastleProvider();
				Security.addProvider(p);
				newKS = KeyStore.getInstance("PKCS12", "BC");
			} catch (KeyStoreException e) {

				e.printStackTrace();
				return false;
			} catch (NoSuchProviderException e) {

				e.printStackTrace();
				return false;
			}
			newKS.load(
					new ByteArrayInputStream(new BASE64Decoder()
							.decodeBuffer(userKey.getKeystore())), this
							.getStringPass());

			Enumeration<String> aliases = newKS.aliases();
			Key key = null;
			String alias = null;

			while (aliases.hasMoreElements()) {
				alias = (String) aliases.nextElement();
				if (newKS.isKeyEntry(alias)) {
					key = newKS.getKey(alias, this.getStringPass());
					// newKS.deleteEntry(alias);
					break;
				}
			}

			// Create the certificate
			BigInteger serial = new BigInteger(ca.getNumeroSerie());
			Certificate cert = createCertificate(newKS.getCertificate(alias)
					.getPublicKey(), caKey, caCert, this.generateX509Name(null,
					null, null, null, null,
					usuario.getNombre() + " " + usuario.getPrimerApellido()
							+ " " + usuario.getSegundoApellido(),
					usuario.getUsername()), serial, CERT_TYPE_USER);
			ca.setNumeroSerie(serial.add(BigInteger.ONE).toString());
			try {
				//Persistir object persona antes por si trae cambios el usuario de cedula,fechaNac, no arroje exeption unsave transient object
				entityManager.persist(usuario.getPersona());
				entityManager.persist(usuario);
				entityManager.flush();
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			// Retrieve the CA's cert chain
			Certificate[] caChain = null;
			try {
				caChain = caKeyStore.getCertificateChain(getrootalias());
			} catch (KeyStoreException e) {

				e.printStackTrace();
				return false;
			}
			// Add our certificate to the chain
			Certificate[] newChain = new Certificate[caChain.length + 1];
			// System.arraycopy(caChain, 0, newChain, 0, caChain.length);
			newChain[0] = cert;
			for (int i = 0; i < caChain.length; i++) {
				newChain[i + 1] = caChain[i];
			}

			newKS.deleteEntry(alias);
			newKS.setKeyEntry(usuario.getUsername(), key, this.getStringPass(),
					newChain);
			ByteArrayOutputStream keystoreStream = new ByteArrayOutputStream();
			// Save the keystore
			newKS.store(keystoreStream, this.getStringPass());
			String keystoreStreamtoString = new BASE64Encoder()
					.encode(keystoreStream.toByteArray());
			keystoreStream.close();
			userKey.setKeystore(keystoreStreamtoString);
			userKey.setFechacreado(((X509Certificate) cert).getNotBefore());
			userKey.setFechavencimiento(((X509Certificate) cert).getNotAfter());
			entityManager.merge(userKey);
			entityManager.flush();
			return true;
		} else
			return false;

	}

	public boolean createUserKeyStore(Usuario_configuracion usuario)
			throws Exception {

		// Obteniendo el KeyStore
		CaKeystore_pki ca = null;
		try {
			ca = (CaKeystore_pki) entityManager.createQuery(
					"select ca from "
							+ "CaKeystore_pki ca where ca.eliminado = false")
					.getSingleResult();
		} catch (NoResultException e) {
			e.printStackTrace();
			facesMessages.add("msg_notExistRaiz_modConfig");
			return false;
		}

		KeyStore caKeyStore;
		try {
			caKeyStore = KeyStore.getInstance("JKS");
			caKeyStore.load(
					new ByteArrayInputStream(new BASE64Decoder()
							.decodeBuffer(ca.getKeystore())), this
							.getStringPass());
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}

		KeyPair keypair = null;
		X509Certificate cert = null;
		keypair = createKeyPair();
		if (keypair == null) {

			return false;

		}
		// Load the CA key and cert from the keystore for
		// passing along to createCertificate()
		PrivateKey caKey = null;
		X509Certificate caCert = null;
		try {
			caKey = (PrivateKey) caKeyStore.getKey(getrootalias(),
					this.getStringPass());
			caCert = (X509Certificate) caKeyStore
					.getCertificate(getrootalias());
		} catch (KeyStoreException e) {
			e.printStackTrace();
			return false;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return false;
		} catch (UnrecoverableKeyException e) {
			e.printStackTrace();
			return false;
		}

		// Create the certificate
		BigInteger serial = new BigInteger(ca.getNumeroSerie());
		cert = createCertificate(
				keypair.getPublic(),
				caKey,
				caCert,
				this.generateX509Name(null, null, null, null, null,
						usuario.getNombre() + " " + usuario.getPrimerApellido()
								+ " " + usuario.getSegundoApellido(),
						usuario.getUsername()), serial, CERT_TYPE_USER);
		ca.setNumeroSerie(serial.add(BigInteger.ONE).toString());
		try {
			entityManager.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		KeyStore newKS = null;
		// Create a new KeyStore
		try {
			Provider p = new BouncyCastleProvider();
			Security.addProvider(p);
			newKS = KeyStore.getInstance("PKCS12", "BC");
		} catch (KeyStoreException e) {

			e.printStackTrace();
			return false;
		} catch (NoSuchProviderException e) {

			e.printStackTrace();
			return false;
		}
		// Initialize it
		try {
			newKS.load(null, null);
		} catch (NoSuchAlgorithmException e1) {

			e1.printStackTrace();
			return false;
		} catch (CertificateException e1) {

			e1.printStackTrace();
			return false;
		} catch (IOException e1) {

			e1.printStackTrace();
			return false;
		}
		// Retrieve the CA's cert chain
		Certificate[] caChain = null;
		try {
			caChain = caKeyStore.getCertificateChain(getrootalias());
		} catch (KeyStoreException e1) {

			e1.printStackTrace();
			return false;
		}
		// Add our certificate to the chain
		Certificate[] newChain = new Certificate[caChain.length + 1];
		// System.arraycopy(caChain, 0, newChain, 0, caChain.length);
		newChain[0] = cert;
		for (int i = 0; i < caChain.length; i++) {
			newChain[i + 1] = caChain[i];
		}
		// Store the key and cert
		String keystoreStreamtoString = "";
		try {			
			newKS.setKeyEntry(usuario.getUsername(), keypair.getPrivate(),
					this.getStringPass(), newChain);
			newKS.setCertificateEntry("trustedCA", caCert);
			ByteArrayOutputStream keystoreStream = new ByteArrayOutputStream();
			
			// Save the keystore
			newKS.store(keystoreStream, this.getStringPass());
			keystoreStreamtoString = new BASE64Encoder().encode(keystoreStream
					.toByteArray());
			keystoreStream.close();
		} catch (Exception e) {

			e.printStackTrace();
			return false;
		}
		UsuarioKeystore_pki userKey = null;
		try {

			userKey = new UsuarioKeystore_pki();
			userKey.setUsuario(entityManager.find(Usuario_pki.class,
					usuario.getId()));
			userKey.setKeystore(keystoreStreamtoString);
			userKey.setFechacreado(((X509Certificate) cert).getNotBefore());
			userKey.setFechavencimiento(((X509Certificate) cert).getNotAfter());
			entityManager.persist(userKey);
			entityManager.flush();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;

	}

	public void modcaKeystore() throws Exception {
		this.error = false;

		this.rootCacommonName = this.rootCacommonName.trim();
		this.rootCaorganizationalUnit = this.rootCaorganizationalUnit.trim();
		this.rootCaorganization = this.rootCaorganization.trim();

		Validations_pki validations = new Validations_pki();
		boolean[] r = new boolean[12];
		r[0] = validations.requeridoM(this.rootCacommonName, "commonName",
				facesMessages);
		r[1] = validations.longitudM(this.rootCacommonName, "commonName",
				facesMessages, 64);
		r[2] = validations.textM(this.rootCacommonName, "commonName",
				facesMessages);
		r[3] = validations.requeridoM(this.rootCaorganizationalUnit,
				"organizationalUnit", facesMessages);
		r[4] = validations.longitudM(this.rootCaorganizationalUnit,
				"organizationalUnit", facesMessages, 64);
		r[5] = validations.textM(this.rootCaorganizationalUnit,
				"organizationalUnit", facesMessages);
		r[6] = validations.requeridoM(this.rootCaorganization, "organization",
				facesMessages);
		r[7] = validations.longitudM(this.rootCaorganization, "organization",
				facesMessages, 64);
		r[8] = validations.textM(this.rootCaorganization, "organization",
				facesMessages);
		r[9] = validations.comborequeridoM(this.rootCacountry, "country",
				facesMessages);
		r[10] = validations.comborequeridoM(this.rootCastate, "state",
				facesMessages);
		r[11] = validations.comborequeridoM(this.rootCalocality, "locality",
				facesMessages);

		for (int i = 0; i < r.length; i++) {
			if (r[i]) {
				this.error = true;
				return;
			}
		}

		// Any option needs to assign to these variables
		// At the end of the method we store these into our CA keystore
		PrivateKey caPrivateKey = null;
		PublicKey caPublicKey = null;
		Certificate oldCACert = null;
		X509Certificate[] caCertChain = new X509Certificate[0];
		// This is the serial number that the CA should start issuing
		// certs at. Useful when importing a CA that has already
		// issued some certs so that we don't reuse serial numbers.
		// Create a new, empty keystore
		KeyStore ks = KeyStore.getInstance("JKS");
		ks.load(new ByteArrayInputStream(new BASE64Decoder()
				.decodeBuffer(this.ca_keystore.getKeystore())), this
				.getStringPass());
		caPrivateKey = (PrivateKey) ks.getKey(getrootalias(),
				this.getStringPass());
		oldCACert = ks.getCertificate(getrootalias());
		caPublicKey = oldCACert.getPublicKey();
		this.rootCacountry = this.rootCacountry.substring(0,
				this.rootCacountry.indexOf("("));
		X509Certificate newCACert = createCertificate(caPublicKey,
				caPrivateKey, null, this.generateX509Name(this.rootCacountry,
						this.rootCastate, this.rootCalocality,
						this.rootCaorganization, this.rootCaorganizationalUnit,
						this.rootCacommonName, null), BigInteger.ONE,
				CERT_TYPE_CA);
		if (newCACert == null) {
			facesMessages.clear();
			facesMessages
					.add("msg_obtenerRaiz_modConfig");
			throw new Exception();
		}
		caCertChain = new X509Certificate[] { newCACert };

		ITrustServices trustServ = null;
		try {

			trustServ = (ITrustServices) TrustFactory.getInstance().getTruster(
					"alas.his");
			((TrustManagerController) trustServ)
					.setEntityManager(entityManager);
			trustServ.addCA(newCACert, TrusterType.TRUSTER_SIGNCERTS_ISSUER);
			// ((TrustManagerController)
			// trustServ).deleteCA((X509Certificate)oldCACert,
			// TrusterType.TRUSTER_SIGNCERTS_ISSUER);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			facesMessages.clear();
			facesMessages
					.add("msg_guardarCRcomoCC_modConfig");
			throw new Exception();
		}

		// Quick sanity check, we shouldn't get to this point and have
		// either one of these still at the initial values we set at the
		// beginning.
		if (caPrivateKey == null || caCertChain.length == 0) {
			System.err
					.println("CA key or cert not set when we went to store them, "
							+ "THIS IS A BUG!");
			throw new Exception();
		}

		// Create a new keystore and stuff our key/cert into it
		// Insert the key/cert
		ks.setKeyEntry(getrootalias(), caPrivateKey, this.getStringPass(),
				caCertChain);

		// Insert the trusted cert
		ks.setCertificateEntry("trustedCA", newCACert);

		// Write the keystore out to a file
		ByteArrayOutputStream kstream = new ByteArrayOutputStream();
		ks.store(kstream, this.getStringPass());
		String kstreamtoString = new BASE64Encoder().encode(kstream
				.toByteArray());
		kstream.close();
		this.ca_keystore.setKeystore(kstreamtoString);
		entityManager.merge(this.ca_keystore);
		entityManager.flush();

	}

	public void createcaKeystore() throws Exception {

		this.error = false;

		this.rootCacommonName = this.rootCacommonName.trim();
		this.rootCaorganizationalUnit = this.rootCaorganizationalUnit.trim();
		this.rootCaorganization = this.rootCaorganization.trim();

		Validations_pki validations = new Validations_pki();
		boolean[] r = new boolean[12];
		r[0] = validations.requeridoM(this.rootCacommonName, "commonName",
				facesMessages);
		r[1] = validations.longitudM(this.rootCacommonName, "commonName",
				facesMessages, 64);
		r[2] = validations.textM(this.rootCacommonName, "commonName",
				facesMessages);
		r[3] = validations.requeridoM(this.rootCaorganizationalUnit,
				"organizationalUnit", facesMessages);
		r[4] = validations.longitudM(this.rootCaorganizationalUnit,
				"organizationalUnit", facesMessages, 64);
		r[5] = validations.textM(this.rootCaorganizationalUnit,
				"organizationalUnit", facesMessages);
		r[6] = validations.requeridoM(this.rootCaorganization, "organization",
				facesMessages);
		r[7] = validations.longitudM(this.rootCaorganization, "organization",
				facesMessages, 64);
		r[8] = validations.textM(this.rootCaorganization, "organization",
				facesMessages);
		r[9] = validations.comborequeridoM(this.rootCacountry, "country",
				facesMessages);
		r[10] = validations.comborequeridoM(this.rootCastate, "state",
				facesMessages);
		r[11] = validations.comborequeridoM(this.rootCalocality, "locality",
				facesMessages);

		for (int i = 0; i < r.length; i++) {
			if (r[i]) {
				this.error = true;
				return;
			}
		}

		// Any option needs to assign to these variables
		// At the end of the method we store these into our CA keystore
		PrivateKey caKey = null;
		X509Certificate[] caCertChain = new X509Certificate[0];
		// This is the serial number that the CA should start issuing
		// certs at. Useful when importing a CA that has already
		// issued some certs so that we don't reuse serial numbers.

		BigInteger initialSerialNumber = new BigInteger("2");

		KeyPair keypair = createKeyPair();
		if (keypair == null) {
			facesMessages.clear();
			facesMessages
					.add("msg_obtenerClaves_modConfig");
			throw new Exception();
		}
		caKey = keypair.getPrivate();

		this.rootCacountry = this.rootCacountry.substring(0,
				this.rootCacountry.indexOf("("));
		X509Certificate newCACert = createCertificate(keypair.getPublic(),
				keypair.getPrivate(), null, this.generateX509Name(
						this.rootCacountry, this.rootCastate,
						this.rootCalocality, this.rootCaorganization,
						this.rootCaorganizationalUnit, this.rootCacommonName,
						null), BigInteger.ONE, CERT_TYPE_CA);
		if (newCACert == null) {
			facesMessages.clear();
			facesMessages
					.add("msg_obtenerRaiz_modConfig");
			throw new Exception();
		}
		caCertChain = new X509Certificate[] { newCACert };

		ITrustServices trustServ = null;
		try {

			trustServ = (ITrustServices) TrustFactory.getInstance().getTruster(
					"alas.his");
			((TrustManagerController) trustServ)
					.setEntityManager(entityManager);
			trustServ.addCA(newCACert, TrusterType.TRUSTER_SIGNCERTS_ISSUER);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			facesMessages.clear();
			facesMessages
					.add("msg_guardarCRcomoCC_modConfig");
			throw new Exception();
		}

		// Quick sanity check, we shouldn't get to this point and have
		// either one of these still at the initial values we set at the
		// beginning.
		if (caKey == null || caCertChain.length == 0) {
			System.err
					.println("CA key or cert not set when we went to store them, "
							+ "THIS IS A BUG!");
			return;
		}

		// Create a new keystore and stuff our key/cert into it
		try {
			// Create a new, empty keystore
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(null, null);

			// Insert the key/cert
			ks.setKeyEntry(getrootalias(), caKey, this.getStringPass(),
					caCertChain);

			// Insert the trusted cert
			ks.setCertificateEntry("trustedCA", newCACert);

			// Write the keystore out to a file
			ByteArrayOutputStream kstream = new ByteArrayOutputStream();
			ks.store(kstream, this.getStringPass());
			String kstreamtoStrin = new BASE64Encoder().encode(kstream
					.toByteArray());
			kstream.close();
			CaKeystore_pki ca = new CaKeystore_pki();
			ca.setEliminado(false);
			ca.setKeystore(kstreamtoStrin);
			ca.setNumeroSerie(initialSerialNumber.toString());
			try {
				entityManager.persist(ca);
				entityManager.flush();
			} catch (Exception e) {
				e.printStackTrace();
				facesMessages.clear();
				facesMessages.add("msg_persistirRaiz_modConfig");
				throw new Exception();
			}

		} catch (KeyStoreException e) {
			e.printStackTrace();
			facesMessages.clear();
			facesMessages.add("msg_keyRaiz_modConfig");
			throw new Exception();
		} catch (FileNotFoundException e) {

			e.printStackTrace();
			facesMessages.clear();
			facesMessages.add("msg_keyRaiz_modConfig");
			throw new Exception();
		} catch (IOException e) {

			e.printStackTrace();
			facesMessages.clear();
			facesMessages.add("msg_keyRaiz_modConfig");
			throw new Exception();
		} catch (NoSuchAlgorithmException e) {

			e.printStackTrace();
			facesMessages.clear();
			facesMessages.add("msg_keyRaiz_modConfig");
			throw new Exception();
		} catch (CertificateException e) {

			e.printStackTrace();
			facesMessages.clear();
			facesMessages.add("msg_keyRaiz_modConfig");
			throw new Exception();
		} catch (Exception e) {
			e.printStackTrace();
			facesMessages.clear();
			facesMessages.add("msg_keyRaiz_modConfig");
			throw new Exception();
		}

	}

	public void init() {

		try {
			ca_keystore = (CaKeystore_pki) entityManager.createQuery(
					"select ca from CaKeystore_pki ca where "
							+ "ca.eliminado = false").getSingleResult();
			KeyStore ks = KeyStore.getInstance("JKS");
			ks.load(new ByteArrayInputStream(new BASE64Decoder()
					.decodeBuffer(this.ca_keystore.getKeystore())), this
					.getStringPass());
			X509Certificate cert = (X509Certificate) ks
					.getCertificate(getrootalias());
			this.rootCacommonName = UtilidadCertificados.getPartDN(cert,
					X509Principal.CN);
			this.rootCacountry = UtilidadCertificados.getPartDN(cert,
					X509Principal.C);
			String nacion = (String) entityManager
					.createQuery(
							"select nacion.valor from Nacion_configuracion nacion where nacion.eliminado = false and "
									+ "nacion.codigo = :cod")
					.setParameter("cod", this.rootCacountry).getSingleResult();
			this.rootCacountry = this.rootCacountry + "(" + nacion + ")";
			fillEstados();
			fillLocalidades();
			this.rootCalocality = UtilidadCertificados.getPartDN(cert,
					X509Principal.L);
			this.rootCaorganization = UtilidadCertificados.getPartDN(cert,
					X509Principal.O);
			this.rootCaorganizationalUnit = UtilidadCertificados.getPartDN(
					cert, X509Principal.OU);
			this.rootCastate = UtilidadCertificados.getPartDN(cert,
					X509Principal.ST);

			this.crearNueva = false;
		} catch (NoResultException e) {
			this.crearNueva = true;
		} catch (Exception e) {

		}
		this.execute = false;
	}

	public static String getrootalias() {
		return rootalias;
	}

	public String getRootCacountry() {
		return rootCacountry;
	}

	public void setRootCacountry(String rootCacountry) {
		this.rootCacountry = rootCacountry;

	}

	public String getRootCastate() {
		return rootCastate;
	}

	public void setRootCastate(String rootCastate) {
		this.rootCastate = rootCastate;

	}

	public String getRootCalocality() {
		return rootCalocality;
	}

	public void setRootCalocality(String rootCalocality) {
		this.rootCalocality = rootCalocality;
	}

	public String getRootCaorganization() {
		return rootCaorganization;
	}

	public void setRootCaorganization(String rootCaorganization) {
		this.rootCaorganization = rootCaorganization;
	}

	public String getRootCaorganizationalUnit() {
		return rootCaorganizationalUnit;
	}

	public void setRootCaorganizationalUnit(String rootCaorganizationalUnit) {
		this.rootCaorganizationalUnit = rootCaorganizationalUnit;
	}

	public String getRootCacommonName() {
		return rootCacommonName;
	}

	public void setRootCacommonName(String rootCacommonName) {
		this.rootCacommonName = rootCacommonName;
	}

	public boolean isError() {
		return error;
	}

	public void setError(boolean error) {
		this.error = error;
	}

	public CaKeystore_pki getCa_keystore() {
		return ca_keystore;
	}

	public void setCa_keystore(CaKeystore_pki caKeystore) {
		ca_keystore = caKeystore;
	}

	public boolean isCrearNueva() {
		return crearNueva;
	}

	public void setCrearNueva(boolean crearNueva) {
		this.crearNueva = crearNueva;
	}

	public boolean isExecute() {
		return execute;
	}

	public void setExecute(boolean execute) {
		this.execute = execute;
	}

	private List<String> estados;

	public List<String> getEstados() {
		return estados;
	}

	public void setEstados(List<String> estados) {
		this.estados = estados;
	}

	public void fillEstados() {
		this.estados = Estados();
	}

	@SuppressWarnings("unchecked")
	private List<String> Estados() {
		this.setRootCalocality(SeamResourceBundle.getBundle().getString(
				"seleccione"));
		this.setRootCastate(SeamResourceBundle.getBundle().getString(
				"seleccione"));
		List<String> ret = null;
		if (!this.rootCacountry.equals(SeamResourceBundle.getBundle()
				.getString("seleccione"))) {
			String search = this.rootCacountry.substring(0,
					this.rootCacountry.indexOf("("));
			ret = entityManager
					.createQuery(
							"select e.valor from Estado_configuracion e where e.nacion.codigo = :nacion order by e.valor")
					.setParameter("nacion", search).getResultList();
			ret.add(0, SeamResourceBundle.getBundle().getString("seleccione"));

			return ret;
		}
		ret = new ArrayList<String>();
		ret.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
		return ret;
	}

	@SuppressWarnings("unchecked")
	public List<String> getNaciones() {

		List<String> result = new ArrayList<String>();
		result.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
		List<Nacion_configuracion> estadoBuscarControlador = entityManager
				.createQuery(
						"select nacion from Nacion_configuracion nacion where nacion.eliminado = false"
								+ " order by nacion.valor").getResultList();
		for (int i = 0; i < estadoBuscarControlador.size(); i++) {
			result.add(estadoBuscarControlador.get(i).getCodigo() + "("
					+ estadoBuscarControlador.get(i).getValor() + ")");

		}

		return result;
	}

	private List<String> localidades = new ArrayList<String>();

	public List<String> getLocalidades() {
		return localidades;
	}

	public void setLocalidades(List<String> localidades) {
		this.localidades = localidades;
	}

	public void fillLocalidades() {
		this.localidades = Localidades();
	}

	@SuppressWarnings("unchecked")
	private List<String> Localidades() {

		List<String> ll = new ArrayList();
		if (!this.rootCastate.equals(SeamResourceBundle.getBundle().getString(
				"seleccione"))) {
			ll = entityManager
					.createQuery(
							"select l.valor from Localidad_configuracion l where l.municipio.estado.valor = :estadoValor order by l.valor")
					.setParameter("estadoValor", this.rootCastate)
					.getResultList();
			ll.add(0, SeamResourceBundle.getBundle().getString("seleccione"));

			return ll;
		}

		ll.add(0, SeamResourceBundle.getBundle().getString("seleccione"));
		return ll;
	}

}
